package com.jiebai.qqsk.live.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author xiaoh
 * @description: 直播中的直播间定时刷欢迎消息线程池 延迟队列
 * @date 2020/2/2515:34
 */
public final class DelayQueueManager {
    private static DelayQueueManager sThreadPoolManager = new DelayQueueManager();
    private List<BlockingQueue> list = Collections.synchronizedList(new ArrayList<>());

    public static DelayQueueManager getInstance() {
        return sThreadPoolManager;
    }

    /*
     * 将构造方法访问修饰符设为私有，禁止任意实例化。
     */
    private DelayQueueManager() {
    }

    public void addQueue(BlockingQueue queue) {
        if (queue != null) {
            list.add(queue);
        }
    }

    public void removeQueue(BlockingQueue queue) {
        if (queue != null) {
            list.remove(queue);
        }
    }

    public int size() {
        return list.size();
    }

    public List<BlockingQueue> getList() {
        return list;
    }
}
