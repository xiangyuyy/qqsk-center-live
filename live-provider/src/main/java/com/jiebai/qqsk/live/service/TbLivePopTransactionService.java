package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.model.TbLivePopTransactionDO;

/**
 * Created by lichenguang
 *
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/12/23 11:44:43
 */
public interface TbLivePopTransactionService extends Service<TbLivePopTransactionDO> {

    /**
     * 新增方法
     * @param tbLivePopTransactionDO 新增实体
     * @return int
     */
    int insertSelective(TbLivePopTransactionDO tbLivePopTransactionDO);
}
