package com.jiebai.qqsk.live.service.impl;

import com.jiebai.framework.common.configurer.RedisLockService;
import com.jiebai.framework.service.AbstractService;
import com.jiebai.qqsk.live.constant.LivePopTransactionTypeEnum;
import com.jiebai.qqsk.live.dao.TbLivePopShopMapper;
import com.jiebai.qqsk.live.dao.TbLivePopTransactionMapper;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceException;
import com.jiebai.qqsk.live.model.TbLivePopShopDO;
import com.jiebai.qqsk.live.model.TbLivePopTransactionDO;
import com.jiebai.qqsk.live.service.TbLivePopTransactionService;
import com.jiebai.qqsk.order.dto.PopOrderTransactionDTO;
import com.jiebai.qqsk.order.remote.RemotePopShopOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

import static com.jiebai.qqsk.live.constant.RedisKeyConstant.LIVE_POP_ACCOUNT_PREFIX;

/**
 * Created by lichenguang
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/12/23 11:44:43
 */
@Slf4j
@Service
public class TbLivePopTransactionServiceImpl extends AbstractService<TbLivePopTransactionDO>
    implements TbLivePopTransactionService {

    @Reference(version = "${consumer.order.version}", validation = "false")
    private RemotePopShopOrderService remotePopShopOrderService;

    @Resource
    private RedisLockService redisLockService;

    @Resource
    private TbLivePopTransactionMapper tbLivePopTransactionMapper;

    @Resource
    private TbLivePopShopMapper tbLivePopShopMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertSelective(TbLivePopTransactionDO transactionDO) {
        //买家支付，即收货订单
        Integer type = LivePopTransactionTypeEnum.BUYER_PAY.getType();
        int result = 0;
        PopOrderTransactionDTO popOrderTransactionDTO;
        //如果是确认收货的订单
        String orderNo = transactionDO.getOrderNo();
        String refundNo = transactionDO.getRefundNo();
        Condition condition = new Condition(TbLivePopTransactionDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("orderNo", orderNo);
        criteria.andEqualTo("type", type);
        //查出收货订单数量
        int count = tbLivePopTransactionMapper.selectCountByCondition(condition);
        try {
            if (StringUtils.isBlank(refundNo) || type.equals(transactionDO.getType())) {
                //如果这笔订单已经确认收货
                if (count > 0) {
                    log.warn("pop订单已存在结算流水, orderNo = {}", orderNo);
                    return result;
                }
                popOrderTransactionDTO = remotePopShopOrderService.calculatePopOrderIncome(orderNo);

            } else {
                //退款订单
                //1.查出这笔退款订单是否已经确认收货
                if (count == 0) {
                    //如果没有确认收货,不需要记账
                    return result;
                }
                popOrderTransactionDTO = remotePopShopOrderService.calculatePopRefundMoney(orderNo, refundNo);
            }
        } catch (Exception e) {
            log.error("调用order服务计算出错, 参数transactionDO = {}, 错误信息message = {}", transactionDO,
                ExceptionUtils.getMessage(e));
            throw new RemoteLiveServiceException("调用order服务计算出错");
        }
        if (Objects.isNull(popOrderTransactionDTO)) {
            log.warn("该订单不是pop订单, orderNo = {}", orderNo);
            return result;
        }
        BigDecimal money = popOrderTransactionDTO.getMoney();
        //如果是退款订单,金额取反
        if (LivePopTransactionTypeEnum.BUYER_REFUND.getType().equals(transactionDO.getType())) {
            money = money.negate();
        }
        Integer userId = popOrderTransactionDTO.getUserId();
        transactionDO.setMoney(money);
        transactionDO.setUserId(userId);
        //更新账户余额，顺便获取余额
        BigDecimal balance = processRemainAccount(userId, money);
        transactionDO.setBalance(balance);
        return tbLivePopTransactionMapper.insertSelective(transactionDO);
    }

    private BigDecimal processRemainAccount(Integer userId, BigDecimal money) {
        TbLivePopShopDO record = new TbLivePopShopDO();
        record.setUserId(userId);
        String redisKey = LIVE_POP_ACCOUNT_PREFIX + userId;
        try {
            boolean isSuccess = redisLockService.lock(redisKey, redisKey, 3000, 3);
            if (isSuccess) {
                TbLivePopShopDO tbLivePopShopDO = tbLivePopShopMapper.selectOne(record);
                BigDecimal accountRemain = tbLivePopShopDO.getAccountRemain();
                log.info("pop店主余额变动, 原先remainAccount = {}, 变动金额money = {}", accountRemain, money);
                tbLivePopShopMapper
                    .updateAccountRemainById(Objects.requireNonNull(tbLivePopShopDO.getId(), "查询不到pop" + "账户信息"),
                        money);
            } else {
                log.warn("pop店主userId = {}, 余额变动失败", userId);
            }
        } catch (Exception e) {
            log.error("pop店主userId = {}, 余额变动发生异常, 错误信息message = {}", userId, ExceptionUtils.getMessage(e));
            throw new RemoteLiveServiceException("pop店主userId = " + userId + "余额变动发生异常");
        } finally {
            redisLockService.unlock(redisKey);
        }
        return tbLivePopShopMapper.selectAccountRemain(userId);
    }
}
