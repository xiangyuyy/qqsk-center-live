package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.LivePopShopManagerQueryDO;
import com.jiebai.qqsk.live.model.TbLivePopShopDO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TbLivePopShopMapper extends Mapper<TbLivePopShopDO> {

    TbLivePopShopDO getByUserId(@Param("userId") Integer userId);

    /**
     * 更新pop店账户余额
     * @param id    主键
     * @param money 金钱
     * @return int
     */
    int updateAccountRemainById(@Param("id") Integer id, @Param("money") BigDecimal money);

    /**
     * 查询账户余额
     * @param userId 用户id
     * @return BigDecimal
     */
    BigDecimal selectAccountRemain(Integer userId);

    /**
     * 主播列表（后台管理）
     * @param tbLivePopShopDO
     * @return
     */
    List<TbLivePopShopDO> getListForManager(LivePopShopManagerQueryDO tbLivePopShopDO);

    /**
     *  获得主播pop店信息
     * @param popShopId popShopId
     * @return
     */
    TbLivePopShopDO getLivePopShopInfor(@Param("popShopId") Integer popShopId);
}