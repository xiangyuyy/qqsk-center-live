package com.jiebai.qqsk.live.utils;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoh
 * @description: 进入直播间定时刷进入消息延时队列对象
 * @date 2020/3/210:53
 */
@Data
public class DelayedElement implements Delayed {
    private  long expire;  //到期时间
    private String key;   //redis key
    private Integer roomId;   //roomId
    private String chatroomId;  //融云 chatroomId
    private String nickName;  //nickName
    private Integer userId ;  //userId

    public DelayedElement(long delay, String key,Integer roomId,String chatroomId,String nickName,Integer userId) {
        this.key = key;
        this.roomId = roomId;
        this.chatroomId = chatroomId;
        this.nickName = nickName;
        this.userId = userId;
        this.expire = System.currentTimeMillis() + delay*1000;    //到期时间 = 当前时间+延迟时间
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
