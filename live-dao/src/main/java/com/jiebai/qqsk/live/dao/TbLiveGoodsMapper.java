package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.TbLiveGoodsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbLiveGoodsMapper extends Mapper<TbLiveGoodsDO> {
    /**
     * 通过roomId获得直播间商品
     *
     * @param roomId
     * @return
     */
    List<TbLiveGoodsDO> getGoodsByRoomId(@Param("roomId") Integer roomId);

    /**
     * 查询限制展示商品列表
     * @param roomId 直播间id
     * @param count 限制数量
     * @return List
     */
    List<String> getGoodsByRoomIdAndLimit(@Param("roomId") Integer roomId, @Param("count") Integer count);

    /**
     * 批量插入橱窗商品
     * @param liveGoodsDOList 橱窗商品
     * @return int
     */
    int insertListSelective(@Param("list") List<TbLiveGoodsDO> liveGoodsDOList);
}