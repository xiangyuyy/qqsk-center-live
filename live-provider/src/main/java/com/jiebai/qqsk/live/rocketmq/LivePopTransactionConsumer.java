package com.jiebai.qqsk.live.rocketmq;

import com.alibaba.fastjson.JSON;
import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.qqsk.live.dto.LivePopTransactionDTO;
import com.jiebai.qqsk.live.model.TbLivePopTransactionDO;
import com.jiebai.qqsk.live.service.TbLivePopTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * pop商品相关交易
 * @author lichenguang
 * @date 2019/12/23
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "MESSAGE-SEND_POPTRANSACTION_TOPIC", consumerGroup = "MESSAGE-POPTRANSACTION-SEND")
public class LivePopTransactionConsumer implements RocketMQListener<String> {

    private static BaseBeanCopier<LivePopTransactionDTO, TbLivePopTransactionDO> LIVE_POP_DTO2DO_COPIER =
        new SimpleBeanCopier<>(LivePopTransactionDTO.class, TbLivePopTransactionDO.class);

    @Resource
    private TbLivePopTransactionService tbLivePopTransactionService;

    @Override
    public void onMessage(String message) {
        log.info("pop商品相关交易消息进行消费 start:" + message);
        LivePopTransactionDTO livePopTransactionDTO = JSON.parseObject(message, LivePopTransactionDTO.class);
        TbLivePopTransactionDO transactionDO = LIVE_POP_DTO2DO_COPIER.copy(livePopTransactionDTO);
        try {
            int res = tbLivePopTransactionService.insertSelective(transactionDO);
            if (res > 0) {
                log.info("插入流水成功, message = {}", message);
            }
        } catch (Exception e) {
            log.error("插入pop商品流水表出现异常, 错误信息errorMessage = {}", ExceptionUtils.getMessage(e));
        }
        log.info("pop商品相关交易消息进行消费 end");
    }
}
