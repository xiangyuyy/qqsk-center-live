package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.model.TbLivePopshopLogDO;

import java.util.List;

/**
 * Created by lichenguang
 * @author lichenguang
 * @version v1.0.0
 * @date 2020/02/17 18:16:50
 */
public interface TbLivePopshopLogService extends Service<TbLivePopshopLogDO> {

    int insertSelective(TbLivePopshopLogDO tbLivePopshopLogDO);

    /**
     * 获取pop店铺审核失败原因
     * @param userId
     * @return
     */
    TbLivePopshopLogDO getMarkByUserId(Integer userId);

    /**
     * 获得日志列表
     * @param popShopId popShopId
     * @return
     */
    List<TbLivePopshopLogDO> getLogsByPopShopId(Integer popShopId);
}
