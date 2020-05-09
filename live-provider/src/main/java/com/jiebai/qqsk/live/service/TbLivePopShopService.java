package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.dto.LivePopshopLogDTO;
import com.jiebai.qqsk.live.model.LivePopShopManagerQueryDO;
import com.jiebai.qqsk.live.model.TbLivePopShopDO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author cxy
 * @version v1.0.0
 * @date 2019/12/17 19:11:15
 */
public interface TbLivePopShopService extends Service<TbLivePopShopDO> {

    /**
     * 是否开通直播
     * @param userId 用户Id
     * @return Boolean
     */
    Boolean isOpenLive(Integer userId);

    /**
     * 是否开通pop店
     * @param userId 用户Id
     * @return Boolean
     */
    Boolean isOpenPopShop(Integer userId);

    /**
     * 开通直播
     * @param userId 用户Id
     * @return Boolean
     */
    Boolean openLive(Integer userId);

    /**
     * 开通pop店
     * @param userId       用户Id
     * @param promiseMoney 保证金
     * @return Boolean
     */
    Boolean openLivePopShop(Integer userId, BigDecimal promiseMoney);

    /**
     * 根据用户查询直播及店铺信息
     * @param userId 用户Id
     * @return TbLivePopShopDO
     */
    TbLivePopShopDO selectOneByUserId(Integer userId);

    /**
     * 管理后台为用户开通直播间
     * @param userId 用户id
     * @return int
     */
    int manageOpenLive(Integer userId);

    /**
     * 根据主键更新
     * @param popId 用户直播相关id
     * @return int
     */
    int updateByPrimarySelective(Integer popId);

    /**
     * 获取关注人中开通了直播的
     * @param discoverUserIdList discoverUserIdList
     * @return List<TbLivePopShopDO>
     */
    List<TbLivePopShopDO> getDiscoverLiveList(List<Integer> discoverUserIdList);

    /**
     * 根据主键更新
     * @param tbLivePopShopDO
     * @return
     */
    int updateByPrimarySelective(TbLivePopShopDO tbLivePopShopDO);

    /**
     * 审核popShop
     * @param livePopshopLogDTO 传输实体
     * @return int
     */
    int checkPopShop(LivePopshopLogDTO livePopshopLogDTO);

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
    TbLivePopShopDO getLivePopShopInfor(Integer popShopId);
}
