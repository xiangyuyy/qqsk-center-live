package com.jiebai.qqsk.live.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoh
 * @description: 直播中的直播间定时刷欢迎消息线程池
 * @date 2020/2/2515:34
 */
public final class ChatRoomThreadPoolManager {
    private static ChatRoomThreadPoolManager sThreadPoolManager = new ChatRoomThreadPoolManager();

    // 线程池核心线程数
    private static final int SIZE_CORE_POOL = 40;

    /*
     * 线程池单例创建方法
     */
    public static ChatRoomThreadPoolManager getInstance() {
        return sThreadPoolManager;
    }

    private final ScheduledThreadPoolExecutor mThreadPool = new ScheduledThreadPoolExecutor(SIZE_CORE_POOL);

    /*
     * 将构造方法访问修饰符设为私有，禁止任意实例化。
     */
    private ChatRoomThreadPoolManager() {
    }

    /*
     * 向线程池中添加任务方法
     */
    public void addExecuteTask(Runnable task, long period) {
        if (task != null) {
            mThreadPool.scheduleAtFixedRate(task, 1, period, TimeUnit.SECONDS);
        }
    }

    /*
     * 关闭线程池，不在接受新的任务，会把已接受的任务执行玩
     */
    public void shutdown() {
        mThreadPool.shutdownNow();
    }
}
