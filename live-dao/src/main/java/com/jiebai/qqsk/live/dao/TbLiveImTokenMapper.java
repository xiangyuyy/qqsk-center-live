package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.TbLiveImTokenDO;
import org.apache.ibatis.annotations.Param;

public interface TbLiveImTokenMapper extends Mapper<TbLiveImTokenDO> {
    /**
     * 获得ImToken
     * @param userId
     * @return
     */
    TbLiveImTokenDO getImTokenByUserId(@Param("userId")Integer userId);

    /**
     * 获得今天生成ImToken总计
     * @return
     */
    int getTodayTokenCount();
}