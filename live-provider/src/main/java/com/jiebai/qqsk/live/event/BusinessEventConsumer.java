package com.jiebai.qqsk.live.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jiebai.qqsk.live.dto.VisitorActionDTO;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import com.jiebai.qqsk.live.model.TbLiveVisitorDO;
import com.jiebai.qqsk.live.model.TbUserDO;
import com.jiebai.qqsk.live.service.TbLiveRoomService;
import com.jiebai.qqsk.live.service.TbLiveVisitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 本地业务事件信息队列消费监听器，spring初始化完成后启动队列消费线程，拿到队列处理业务逻辑
 */
@Slf4j
@Component
public class BusinessEventConsumer implements InitializingBean {
    @Autowired
    private BusinessEventQueue businessEventQueue;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private TbLiveRoomService tbLiveRoomService;

    @Resource
    private TbLiveVisitorService tbLiveVisitorService;

    @Override
    public void afterPropertiesSet() {
        ScheduledExecutorService executorService =
                new ScheduledThreadPoolExecutor(BusinessEventConfig.consumerThreadNumber,
                        new BasicThreadFactory.Builder().namingPattern("businessEvent-consumer-%d").daemon(true).build());
        for (int i = 0; i < BusinessEventConfig.consumerThreadNumber; i++) {
            executorService.execute(new Consumer());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("business_event consumer thread exception." + e.getMessage());
            }
        }
    }

    class Consumer implements Runnable {
        private long lastSendMQTime = System.currentTimeMillis();

        @Override
        public void run() {
            List<BusinessEventMessage> eventMessageList = Lists.newArrayList();
            while (true) {
                try {
                    BusinessEventMessage message = businessEventQueue.take();
                    if (Objects.nonNull(message)) {
                        // 添加缓存锁
                        String lockKey = "QQSK:LIVEROOMVISITORLOCK:ROOMID" + message.getRoomId();
                        Lock lock = redisLockRegistry.obtain(lockKey);//多线程交互 加分布式锁
                        lock.lock();
                        try {
                            TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getByRoomId(message.getRoomId());
                            if (Objects.nonNull(tbLiveRoomDO)) {
                                tbLiveRoomDO.setVisitorNum(tbLiveRoomDO.getVisitorNum() + 1);
                                tbLiveRoomService.updateById(tbLiveRoomDO);
                            }
                            Condition condition = new Condition(TbLiveVisitorDO.class);
                            Example.Criteria criteria = condition.createCriteria();
                            criteria.andEqualTo("userId", message.getUserId());
                            criteria.andEqualTo("roomId", message.getRoomId());
                            List<TbLiveVisitorDO> liveVisitorDOList = tbLiveVisitorService.listByCondition(condition);
                            if (CollectionUtils.isNotEmpty(liveVisitorDOList)) {
                                //更新用户行为
                                TbLiveVisitorDO liveVisitorDO = liveVisitorDOList.get(0);
                                List<VisitorActionDTO> list = JSON.parseArray(liveVisitorDO.getExt(), VisitorActionDTO.class);
                                VisitorActionDTO visitorActionDTO = new VisitorActionDTO();
                                if (ObjectUtils.isEmpty(message.getIsTrue())){
                                    visitorActionDTO.setIsTrue(1);
                                }
                                else {
                                    visitorActionDTO.setIsTrue(0);
                                }
                                visitorActionDTO.setAction(0);
                                visitorActionDTO.setGmtCreate(new Date());
                                list.add(visitorActionDTO);
                                String jsonMessage = JSON.toJSONString(list);
                                liveVisitorDO.setExt(jsonMessage);
                                tbLiveVisitorService.updateById(liveVisitorDO);
                            } else {
                                //第一次进入直播间 增加观众
                                TbLiveVisitorDO tbLiveVisitorDO = new TbLiveVisitorDO();
                                tbLiveVisitorDO.setGmtCreate(new Date());
                                tbLiveVisitorDO.setRoomId(message.getRoomId());
                                tbLiveVisitorDO.setUserId(message.getUserId());
                                List<VisitorActionDTO> list = Lists.newArrayList();
                                //用户行为
                                VisitorActionDTO visitorActionDTO = new VisitorActionDTO();
                                if (ObjectUtils.isEmpty(message.getIsTrue())){
                                    visitorActionDTO.setIsTrue(1);
                                }
                                else {
                                    visitorActionDTO.setIsTrue(0);
                                }
                                visitorActionDTO.setAction(0);
                                visitorActionDTO.setGmtCreate(new Date());
                                list.add(visitorActionDTO);
                                String jsonMessage = JSON.toJSONString(list);
                                tbLiveVisitorDO.setExt(jsonMessage);
                                tbLiveVisitorService.save(tbLiveVisitorDO);

                                //缓存前面3个观众头像
                                List<TbLiveVisitorDO> listVisitor = tbLiveVisitorService.getVisitorByRoomId(message.getRoomId());
                                List<String> stringList = Lists.newArrayList();
                                if (listVisitor.size() >= 3) {
                                    listVisitor = listVisitor.subList(0, 3);
                                }
                                //展示最新的3个观众头像
                                for (TbLiveVisitorDO item : listVisitor) {
                                    TbUserDO tbUser = tbLiveVisitorService.getTbUserByUserId(item.getUserId());
                                    //观众头像
                                    if (Objects.nonNull(tbUser)) {
                                        stringList.add(tbUser.getHeadimgurl());
                                    }
                                }
                                stringRedisTemplate.opsForValue().set("QQSK:THREELIVEVISITOR" + message.getRoomId(), JSONObject.toJSONString(stringList),
                                        60 * 60, TimeUnit.SECONDS);
                            }
                        } catch (Exception e) {
                            log.error("businessEvent 处理观众数出错 " + e.getMessage());
                        } finally {
                            lock.unlock();
                        }
                    }
                } catch (Exception e) {
                    log.error("businessEvent consumer thread abnormal exit " + e.getMessage());
                }
            }
        }
    }

}


