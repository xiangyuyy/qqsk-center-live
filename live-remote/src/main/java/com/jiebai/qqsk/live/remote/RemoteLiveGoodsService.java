package com.jiebai.qqsk.live.remote;

import com.jiebai.qqsk.live.dto.LiveGoodsDTO;
import com.jiebai.qqsk.live.dto.SaveLiveGoodsDTO;

import java.util.List;

/**
 * @author lichenguang
 * 2019/11/15
 */
public interface RemoteLiveGoodsService {

    /**
     * 将商品添加到橱窗
     * @param liveGoodsDTO 橱窗商品
     * @return int
     */
    int insertMyLiveGood(LiveGoodsDTO liveGoodsDTO);

    /**
     * 修改橱窗商品排序
     * @param saveLiveGoodsDTO 橱窗商品列表
     * @return int
     */
    int updateMyLiveGoodPriority(SaveLiveGoodsDTO saveLiveGoodsDTO);

    /**
     * 根据用户id，roomId和spuCode删除橱窗商品
     * @param liveGoodsDTO 橱窗商品实体
     * @return int
     */
    int deleteMyLiveGood(LiveGoodsDTO liveGoodsDTO);

    /**
     * 根据直播间id获取橱窗商品列表
     * @param userId id
     * @param roomId 直播间id
     * @return List
     */
    List<LiveGoodsDTO> getMyLiveGoods(Integer userId, Integer roomId);
}
