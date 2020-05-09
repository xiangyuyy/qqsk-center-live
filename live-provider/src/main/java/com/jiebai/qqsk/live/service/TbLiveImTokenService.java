package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.model.TbLiveImTokenDO;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 15:57:21
 */
public interface TbLiveImTokenService extends Service<TbLiveImTokenDO> {
    /**
     * 获得ImToken
     * @param userId
     * @return
     */
    TbLiveImTokenDO getImTokenByUserId(Integer userId);

    /**
     * 获得今天生成ImToken总计
     * @return
     */
    int getTodayTokenCount();

}
