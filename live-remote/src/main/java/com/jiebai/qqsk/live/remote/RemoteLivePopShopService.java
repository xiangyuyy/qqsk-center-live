package com.jiebai.qqsk.live.remote;

import com.github.pagehelper.PageInfo;
import com.jiebai.qqsk.live.dto.*;

import java.util.List;
import java.util.Map;

/**
 * pop店服务
 *
 * @author cxy
 */
public interface RemoteLivePopShopService {
    /**
     * 是否开通直播
     * @param userId
     * @return
     */
    Boolean isOpenLive(Integer userId);

    /**
     * 是否开通pop店
     * @param userId
     * @return
     */
    Boolean isOpenPopShop(Integer userId);

    /**
     *获取用户权限
     * @param userId 用户id
     * @return
     */
    LivePowerDTO getUserPower(Integer userId);

    /**
     * 获取pop店主页数据
     * @param userId 用户id
     * @return PopHomePageDTO
     */
    PopHomePageDTO getPopShopHomePageData(Integer userId);

    /**
     * 管理后台为用户开通直播间
     * @param userId 用户id
     * @return int
     */
    int manageOpenLive(Integer userId);

    /**
     * 获取店铺类型
     * @return
     */
    List<Map<String,String>> getPopshopType();

    /**
     * 获取店铺分类
     * @return
     */
    List<Map<String,String>> getPopshopCatgory();

    /**
     * 修改pop店铺信息
     * @return
     */
    Integer updateLivePopShop(LivePopShopDTO livePopShopDTO);

    /**
     * 根据userID获取pop店铺信息
     * @param userId
     * @return
     */
    LivePopShopDTO getLivePopShopInfo(Integer userId);

    /**
     * 审核popShop
     * @param livePopshopLogDTO 传输实体
     * @return int
     */
    int checkPopShop(LivePopshopLogDTO livePopshopLogDTO);

    /**
     * 主播列表（后台管理）
     * @param queryDTO 搜索实体
     * @return PageInfo
     */
    PageInfo<TbLivePopShopManagerDTO> getListForManager(LivePopShopManagerQueryDTO queryDTO);

    /**
     *
     * @param popShopId  popShopId
     * @param IfShowLog 是否展示审核日志
     * @return
     */
    TbLivePopShopManagerDTO getLivePopShopManagerInfor(Integer popShopId,Boolean IfShowLog);
}
