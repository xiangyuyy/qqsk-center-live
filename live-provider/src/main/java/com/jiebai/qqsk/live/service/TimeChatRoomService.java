package com.jiebai.qqsk.live.service;

import com.jiebai.qqsk.live.constant.LiveConstant;
import com.jiebai.qqsk.live.event.BusinessEventMessage;
import com.jiebai.qqsk.live.event.BusinessEventQueue;
import com.jiebai.qqsk.live.utils.ChatRoomThreadPoolManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoh
 * @description: 定时任务方案 直播中的直播间定时刷欢迎消息服务(未解决服务器重启 定时任务初始化问题)
 * @date 2020/2/2515:52
 */
@Slf4j
@Service
public class TimeChatRoomService {

    @Autowired
    TbLiveVisitorService tbLiveVisitorService;

    @Autowired
    BusinessEventQueue businessEventQueue;

    @Autowired
    RongYunService rongYunService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void join(Integer roomId, String chatroomId, long period, int limit) {
        try {
            if (isNull(roomId.toString())) { //防止一个直播间多次进入 起个定时任务
                addListRedis(roomId.toString());
                ChatRoomThreadPoolManager.getInstance().addExecuteTask(new Task(roomId, limit, roomId.toString(), chatroomId), period);
            }
        } catch (Exception e) {
            log.info("房间号" + roomId + "生成进入刷消息定时任务报错" + e.getMessage());
        }
    }

    public void quit(Integer roomId) {
        try {
            List<String> list = stringRedisTemplate.opsForList().range(LiveConstant.ONLIVEREDISLISTKEY,0,-1);
            if (ObjectUtils.isEmpty(list) || list.size() == 0){
                return ;
            }
            for (String item: list) {
                if (item.split("s")[0].equals(roomId.toString())){
                    stringRedisTemplate.opsForList().remove(LiveConstant.ONLIVEREDISLISTKEY,1,roomId.toString());
                }
            }
        } catch (Exception e) {
            log.info("房间号" + roomId + "退出进入刷消息定时任务报错" + e.getMessage());
        }
    }

    public void change(Integer roomId, String chatroomId, long period, int limit) {
        Boolean isExist = false;//已经存在定时任务
        List<String> list = stringRedisTemplate.opsForList().range(LiveConstant.ONLIVEREDISLISTKEY,0,-1);
        if (ObjectUtils.isEmpty(list) || list.size() == 0){
            return ;
        }
        for (String item: list) {
            if (item.split("s")[0].equals(roomId.toString())){
                stringRedisTemplate.opsForList().remove(LiveConstant.ONLIVEREDISLISTKEY,1,roomId);
                isExist = true;
            }
        }
        if (isExist) {
            String key = roomId.toString() + "s"+new Date().getTime();//标识不同的改变
            addListRedis(key);
            ChatRoomThreadPoolManager.getInstance().addExecuteTask(new Task(roomId, limit, key, chatroomId), period);
        } else {
            join(roomId, chatroomId, period, limit);
        }
    }
    private Boolean isNull(String roomId){
        List<String> list = stringRedisTemplate.opsForList().range(LiveConstant.ONLIVEREDISLISTKEY,0,-1);
        if (ObjectUtils.isEmpty(list) || list.size() == 0){
            return true;
        }
        for (String item: list) {
            if (item.split("s")[0].equals(roomId)){
                return false;
            }
        }
        return true;
    }

    private Boolean isExist(String roomId){
        List<String> list = stringRedisTemplate.opsForList().range(LiveConstant.ONLIVEREDISLISTKEY,0,-1);
        if (ObjectUtils.isEmpty(list) || list.size() == 0){
            return false;
        }
        for (String item: list) {
            if (item.equals(roomId)){
                return true;
            }
        }
        return false;
    }

    private  void addListRedis(String roomId){
        stringRedisTemplate.opsForList().leftPush(LiveConstant.ONLIVEREDISLISTKEY,roomId);
    }
    private class Task implements Runnable {

        private Integer roomId;
        private int limit;//执行几次
        private String mapKey;//mapKey
        private String chatroomId;//IM 房间号

        public Task(Integer roomId, int limit, String mapKey, String chatroomId) {
            this.roomId = roomId;
            this.limit = limit;
            this.mapKey = mapKey;
            this.chatroomId = chatroomId;
        }

        @Override
        public void run() {
            try {
                if (isExist(mapKey)) {
                    execute(roomId, limit, chatroomId);
                }
            } catch (Exception e) {
                log.info("房间号" + roomId + "定时发送进入消息报错" + e.getMessage());
            } finally {
            }
        }

        public void execute(Integer roomId, int limit, String chatroomId) throws InterruptedException {
            List<Map<String, Object>> visitors = tbLiveVisitorService.getRandVisitors(limit);//每个周期发送几个进入信息
            for (Map<String, Object> item : visitors) {
                Integer userId = Integer.parseInt(item.get("userId").toString());
                String nickName = item.get("nickName").toString();
                BusinessEventMessage eventMessage = new BusinessEventMessage();
                eventMessage.setRoomId(roomId);
                eventMessage.setUserId(userId);
                eventMessage.setIsTrue(0);
                businessEventQueue.offer(eventMessage);
                rongYunService.sendChatroomWelcomeMessage(new String[]{chatroomId}, userId, nickName);
                log.info("房间号" + roomId + "定时成功发送进入消息----" + nickName + "----" + new Date());
                Thread.currentThread().sleep(1000);
            }
        }
    }
}
