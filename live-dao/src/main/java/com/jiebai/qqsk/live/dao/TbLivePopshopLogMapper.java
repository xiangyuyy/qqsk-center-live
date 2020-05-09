package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.TbLivePopshopLogDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lichenguang
 * @author lichenguang
 * @version v1.0.0
 * @date 2020/02/17 18:16:50
 */
public interface TbLivePopshopLogMapper extends Mapper<TbLivePopshopLogDO> {
    /**
     *
     * @param userId
     * @return
     */
    TbLivePopshopLogDO getMarkByUserId(@Param("userId") Integer userId);


    /**
     * 获得日志列表
     * @param popShopId
     * @return
     */
    List<TbLivePopshopLogDO> getLogsByPopShopId(@Param("popShopId") Integer popShopId);

}