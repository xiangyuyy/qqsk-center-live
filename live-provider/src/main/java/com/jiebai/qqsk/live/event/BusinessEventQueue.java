package com.jiebai.qqsk.live.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author cxy
 * @description: 本地队列
 * @date 2019/11/169:24
 */
@Slf4j
@Component
public class BusinessEventQueue {
    private LinkedBlockingQueue<BusinessEventMessage> queue;

    @PostConstruct
    public void init() {
        queue = new LinkedBlockingQueue<>(BusinessEventConfig.queueCapacity);
    }

    public void offer(BusinessEventMessage eventMessage) {
        queue.offer(eventMessage);
    }

    public BusinessEventMessage take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error("BusinessEventQueue take error", e);
        }
        return null;
    }

    public int size() {
        return queue.size();
    }
}
