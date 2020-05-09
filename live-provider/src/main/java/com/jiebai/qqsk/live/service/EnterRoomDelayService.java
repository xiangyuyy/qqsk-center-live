package com.jiebai.qqsk.live.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.jiebai.qqsk.live.constant.LiveConstant;
import com.jiebai.qqsk.live.constant.LiveOpenTypeEnum;
import com.jiebai.qqsk.live.constant.LiveStatusEnum;
import com.jiebai.qqsk.live.event.BusinessEventMessage;
import com.jiebai.qqsk.live.event.BusinessEventQueue;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import com.jiebai.qqsk.live.utils.DelayQueueManager;
import com.jiebai.qqsk.live.utils.DelayQueueThreadPoolManager;
import com.jiebai.qqsk.live.utils.DelayedElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * @author xiaoh
 * @description: 延迟任务方案 直播中的直播间定时刷欢迎消息服务
 * @date 2020/2/2515:52
 */
@Slf4j
@Service
public class EnterRoomDelayService {

    @Autowired
    TbLiveVisitorService tbLiveVisitorService;

    @Autowired
    TbLiveRoomService tbLiveRoomService;

    @Autowired
    RongYunService rongYunService;

    @Autowired
    BusinessEventQueue businessEventQueue;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @NacosValue(value = "${live_room_open_type_A:1,5,1000,1500,2100}", autoRefreshed = true)
    private String live_room_open_type_A;

    @NacosValue(value = "${live_room_open_type_B:2,8,1000,900,1300}", autoRefreshed = true)
    private String live_room_open_type_B;

    @NacosValue(value = "${live_room_open_type_C:3,10,1000,300,800}", autoRefreshed = true)
    private String live_room_open_type_C;

    @NacosValue(value = "${live_room_open_type_D:}", autoRefreshed = true)
    private String live_room_open_type_D;

    public BlockingQueue createQueue(Integer roomId, String redisKey, String chatroomId,String type) {
        BlockingQueue<DelayedElement> qeque = new DelayQueue<>();
        int size = 0;
        int randomMix = 0;
        int randomMax = 0;
        String[] rules = getTypeRule(type);
        if (rules != null){
            randomMix = Integer.valueOf(rules[0]);
            randomMax = Integer.valueOf(rules[1]);
            size = Integer.valueOf(rules[2]);
        }
        if (size == 0) {
            return qeque;
        }
        List<Map<String, Object>> visitors = tbLiveVisitorService.getRandVisitors(size);// 观众数量存入队列
        long delay = 0;//延迟时间
        for (Map<String, Object> item : visitors) {
            int random = (int) (Math.random() * (randomMax-randomMix) + randomMix);//随机3-10秒
            delay += random;
            DelayedElement delayedElement = new DelayedElement(delay, redisKey, roomId, chatroomId, item.get("nickName").toString(), Integer.parseInt(item.get("userId").toString()));
            try {
                qeque.put(delayedElement);
            } catch (InterruptedException e) {
                log.info("deque" + roomId + "delayedElement入队失败");
            }
        }
        return qeque;
    }

    public void join(Integer roomId) {
        try {
            if (isNull(roomId.toString())) { //防止一个直播间多次进入 起个定时任务
                addListRedis(roomId.toString());//新增redid缓存
                TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getById(roomId);
                if (tbLiveRoomDO == null) {
                    return;
                }
                if (tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_OVER.getStatus())) {
                    return;
                }
                BlockingQueue<DelayedElement> qeque = createQueue(roomId, roomId.toString(), tbLiveRoomDO.getImId(),tbLiveRoomDO.getOpenType());
                if (qeque.size() >= 1) {
                    //DelayQueueManager.getInstance().addQueue(qeque);
                    DelayQueueThreadPoolManager.getInstance().addExecuteTask(new Task(qeque));
                }
            } else {
                log.info("房间号" + roomId + "已经有生成了延迟消息刷观众进入");
            }
        } catch (Exception e) {
            log.info("房间号" + roomId + "生成进入延迟消息任务报错" + e.getMessage());
        }
    }

    public String[] getTypeRule(String type) {
        if (!StringUtils.isEmpty(type)) {
            String rule = getRule(type);
            if (!StringUtils.isEmpty(rule)) {
                return rule.split(",");
            }
        }
        return null;
    }

    public void quit(Integer roomId) {
        try {
            List<String> list = stringRedisTemplate.opsForList().range(LiveConstant.ONLIVEREDISDELAYLISTKEY, 0, -1);
            if (ObjectUtils.isEmpty(list) || list.size() == 0) {
                return;
            }
            for (String item : list) {
                if (item.split("s")[0].equals(roomId.toString())) {
                    stringRedisTemplate.opsForList().remove(LiveConstant.ONLIVEREDISDELAYLISTKEY, 1, item);
                }
            }
        } catch (Exception e) {
            log.info("房间号" + roomId + "退出进入延迟消息报错" + e.getMessage());
        }
    }

    public void change(Integer roomId) {
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getById(roomId);
        if (tbLiveRoomDO == null) {
            return;
        }
        if (tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_OVER.getStatus()) || tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_NOT_START.getStatus())) {
            return;
        }
        Boolean isExist = false;//已经存在定时任务
        List<String> list = stringRedisTemplate.opsForList().range(LiveConstant.ONLIVEREDISDELAYLISTKEY, 0, -1);
        if (ObjectUtils.isEmpty(list) || list.size() == 0) {
            return;
        }
        for (String item : list) {
            if (item.split("s")[0].equals(roomId.toString())) {
                stringRedisTemplate.opsForList().remove(LiveConstant.ONLIVEREDISDELAYLISTKEY, 1, item);
                isExist = true;
            }
        }
        if (isExist) {
            String key = roomId.toString() + "s" + new Date().getTime();//标识直播间不同的改变 111s时间
            addListRedis(key);
            BlockingQueue<DelayedElement> qeque = createQueue(roomId, key, tbLiveRoomDO.getImId(),tbLiveRoomDO.getOpenType());
            if (qeque.size() >= 1) {
                //DelayQueueManager.getInstance().addQueue(qeque);
                DelayQueueThreadPoolManager.getInstance().addExecuteTask(new Task(qeque));
            }
        } else {
            join(roomId);
        }
    }

    //  111s时间  111 表示的一个直播间 带s的表示后台改了类型
    public Boolean isNull(String roomId) {
        List<String> list = stringRedisTemplate.opsForList().range(LiveConstant.ONLIVEREDISDELAYLISTKEY, 0, -1);
        if (ObjectUtils.isEmpty(list) || list.size() == 0) {
            return true;
        }
        for (String item : list) {
            if (item.split("s")[0].equals(roomId)) {
                return false;
            }
        }
        return true;
    }

    public Boolean isExistKey(String key) {
        List<String> list = stringRedisTemplate.opsForList().range(LiveConstant.ONLIVEREDISDELAYLISTKEY, 0, -1);
        if (ObjectUtils.isEmpty(list) || list.size() == 0) {
            return false;
        }
        for (String item : list) {
            if (item.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public void execute(Integer roomId, String chatroomId, String nickName, Integer userId) {
        BusinessEventMessage eventMessage = new BusinessEventMessage();
        eventMessage.setRoomId(roomId);
        eventMessage.setUserId(userId);
        eventMessage.setIsTrue(0);
        businessEventQueue.offer(eventMessage);
        rongYunService.sendChatroomWelcomeMessage(new String[]{chatroomId}, userId, nickName);
        log.info("房间号" + roomId + "延时任务成功发送进入消息-" + nickName);
    }

    public void addListRedis(String roomId) {
        stringRedisTemplate.opsForList().leftPush(LiveConstant.ONLIVEREDISDELAYLISTKEY, roomId);
    }

    private String getRule(String type) {
        String rule = "";
        switch (type) {
            case "A":
                rule = live_room_open_type_A;
                break;
            case "B":
                rule = live_room_open_type_B;
                break;
            case "C":
                rule = live_room_open_type_C;
                break;
            case "D":
                rule = live_room_open_type_D;
                break;
            default:
        }
        return rule;
    }

    public Integer getVistors(Integer visitors,String openType){
        String[] rules = getTypeRule(openType);
        if (rules != null){
            if (visitors < Integer.parseInt(rules[3])){
                int randomMix = Integer.valueOf(rules[3]);
                int randomMax = Integer.valueOf(rules[4]);
                visitors = (int) (Math.random() * (randomMax - randomMix ) + randomMix); //如果小于下一个等级的最小值 更改为下一级最小值和最大值随机
            }
        }
        return visitors;
    }

    //加入线程池的的实现
    private class Task implements Runnable {
        private BlockingQueue<DelayedElement> queue;

        public Task(BlockingQueue<DelayedElement> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                //log.info("delayqueque-consumer run*********" + "延时发送进入消息" + new Date());
                while (queue.size() > 0) {
                    //log.info("delayqueque-consumer run------" + "延时发送进入消息" + new Date());
                        DelayedElement delayedElement = queue.take();
                        if (isExistKey(delayedElement.getKey())) { //是否存在redis key
                            TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getById(delayedElement.getRoomId());
                            if (tbLiveRoomDO == null) {
                                return;
                            }
                            if (tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_OVER.getStatus())) {
                                return;
                            }
                            execute(delayedElement.getRoomId(), delayedElement.getChatroomId(), delayedElement.getNickName(), delayedElement.getUserId());
                        }
                }
            } catch (Exception e) {
                log.info("delayqueque-run" + "延时发送进入消息报错" + e.getMessage());
            } finally {
            }
        }
    }
}
