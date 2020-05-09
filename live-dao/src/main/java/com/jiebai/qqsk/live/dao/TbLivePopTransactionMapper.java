package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.TbLivePopTransactionDO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author lcg
 * @date 2019/12/23
 */
public interface TbLivePopTransactionMapper extends Mapper<TbLivePopTransactionDO> {

    /**
     * 根据userId进行汇总计算
     * @param userId 用户id
     * @return BigDecimal
     */
    BigDecimal selectSumMoneyByUserId(@Param("userId") Integer userId);

}