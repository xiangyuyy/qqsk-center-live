package com.jiebai.qqsk.live.event;

/**
 * @author cxy
 * @description: 本地队列配置
 * @date 2019/11/169:33
 */
public class BusinessEventConfig {
    /**
     * 本地队列消费者线程数
     */
    public static final Integer consumerThreadNumber = 5;

    /**
     * 本地队列容量
     */
    public static final Integer queueCapacity = 10000;

}
