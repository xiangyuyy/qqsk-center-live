package com.jiebai.qqsk.live.event;


import com.jiebai.qqsk.live.constant.LiveConstant;
import com.jiebai.qqsk.live.constant.LiveStatusEnum;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import com.jiebai.qqsk.live.service.EnterRoomDelayService;
import com.jiebai.qqsk.live.service.TbLiveRoomService;
import com.jiebai.qqsk.live.utils.DelayQueueManager;
import com.jiebai.qqsk.live.utils.DelayedElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DelayQuequeConsumer implements InitializingBean {
    @Autowired
    EnterRoomDelayService enterRoomDelayService;

    @Autowired
    TbLiveRoomService tbLiveRoomService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void afterPropertiesSet() {
        if (ObjectUtils.isEmpty(stringRedisTemplate.opsForValue().get(LiveConstant.ONLIVEREDISINITTKEY))) {
            //几台服务器启动间隔不大于2分钟
            stringRedisTemplate.opsForValue().set(LiveConstant.ONLIVEREDISINITTKEY, "1", 2, TimeUnit.MINUTES);
            Condition condition = new Condition(TbLiveRoomDO.class);    //重启初始化延迟队列
            Example.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("state", LiveStatusEnum.LIVE_STARTING.getStatus());//直播中的
            criteria.andEqualTo("isPublic", 1);
            criteria.andEqualTo("isDeleted", 0);
            List<TbLiveRoomDO> list = tbLiveRoomService.listByCondition(condition);
            if (list.size() > 0) {
                stringRedisTemplate.delete(LiveConstant.ONLIVEREDISDELAYLISTKEY);
                for (TbLiveRoomDO item : list) {
                    //如果没有被其他服务器初始化 防止重复初始化
                    enterRoomDelayService.join(item.getId());
                }
            }
        }
/*
        ScheduledExecutorService executorService =
                new ScheduledThreadPoolExecutor(BusinessEventConfig.delayQuequeThreadNumber);
        for (int i = 0; i < BusinessEventConfig.delayQuequeThreadNumber; i++) {
            executorService.execute(new Task());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("delayqueque-consumer thread exception." + e.getMessage());
            }
        }*/
    }

/*    private class Task implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("当前线程" + Thread.currentThread().getName());
                    List<BlockingQueue> list = DelayQueueManager.getInstance().getList();
                    if (list.size() > 0) {
                        for (BlockingQueue<DelayedElement> item : list) {
                            if (item.size() > 0) {
                                DelayedElement delayedElement = item.take();
                                if (enterRoomDelayService.isExistKey(delayedElement.getKey())) { //是否存在redis key
                                    TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getById(delayedElement.getRoomId());
                                    if (tbLiveRoomDO == null) {
                                        return;
                                    }
                                    if (tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_OVER.getStatus())) {
                                        return;
                                    }
                                    enterRoomDelayService.execute(delayedElement.getRoomId(), delayedElement.getChatroomId(), delayedElement.getNickName(), delayedElement.getUserId());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.info("delayqueque-consumer run" + "延时发送进入消息报错" + e.getMessage());
            } finally {
            }
        }
    }*/
}


