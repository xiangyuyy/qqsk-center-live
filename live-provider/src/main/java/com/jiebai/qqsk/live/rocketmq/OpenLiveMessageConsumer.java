package com.jiebai.qqsk.live.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.jiebai.qqsk.live.service.TbLivePopShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author cxy
 * @date 2019/12/17
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "MESSAGE-SEND_OPENLIVE_TOPIC", consumerGroup = "MESSAGE-OPENLIVE-SEND")
public class OpenLiveMessageConsumer implements RocketMQListener<String> {

    @Resource
    private TbLivePopShopService tbLivePopShopService;

    @Override
    public void onMessage(String message) {
        log.info("开通直播服务订阅消费 start:" + message);
        try {
            if (!StringUtils.isEmpty(message)) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                Boolean result = tbLivePopShopService.openLive(jsonObject.getInteger("userId"));
                if (result){
                    log.info("开通直播服务订阅消费 开通成功");
                }
                else{
                    log.info("开通直播服务订阅消费 开通失败");
                }
            }
            log.info("开通直播服务订阅消费 end: " + "json" + message);
        } catch (Exception ex) {
            log.warn("开通直播服务消费出现错误, 错误信息 = {}", ex.getMessage() + "json" + message);
        }
    }

}
