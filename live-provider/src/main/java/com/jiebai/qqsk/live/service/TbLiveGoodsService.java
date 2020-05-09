package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.dto.LiveGoodsDTO;
import com.jiebai.qqsk.live.model.TbLiveGoodsDO;

import java.util.List;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 15:29:15
 */
public interface TbLiveGoodsService extends Service<TbLiveGoodsDO> {
    /**
     * 通过roomId获得直播间商品
     *
     * @param roomId
     * @return
     */
    List<TbLiveGoodsDO> getGoodsByRoomId(Integer roomId);

    /**
     * 查询限制展示商品列表
     * @param roomId 直播间id
     * @param count 限制数量
     * @return List
     */
    List<String> getGoodsByRoomIdAndLimit(Integer roomId, Integer count);

    /**
     * 添加商品到橱窗
     * @param tbLiveGoodsDO 橱窗商品
     * @return int
     */
    int insertSelective(TbLiveGoodsDO tbLiveGoodsDO);

    /**
     * 根据直播间id获取橱窗商品列表
     * @param userId id
     * @param roomId 直播间id
     * @return List
     */
    List<TbLiveGoodsDO> getGoodsByUserIdAndRoomId(Integer userId, Integer roomId);

    /**
     * 根据用户id，roomId和spuCode删除橱窗商品
     * @param liveGoodsDTO 橱窗商品实体
     * @return int
     */
    int deleteByRoomIdAndSpuCode(LiveGoodsDTO liveGoodsDTO);

    /**
     * 查询直播间商品数目(校验重复)
     * @param roomId 直播间id
     * @param spuCode 商品code
     * @return int
     */
    int selectCountByRoomIdAndSpuCode(Integer roomId, String spuCode);

    /**
     * 根据主键查询
     * @param id 主键
     * @return TbLiveGoodsDO
     */
    TbLiveGoodsDO selectByPrimaryKey(Long id);

    /**
     * 情况直播间橱窗
     * @param userId 用户id
     * @param roomId 直播间id
     * @return int
     */
    int deleteByUserIdAndRoomId(Integer userId, Integer roomId);

    /**
     * 查询直播间商品总数
     * @param userId 用户id
     * @param roomId 直播间Id
     * @return 商品总数
     */
    int selectCountByUserIdAndRoomId(Integer userId, Integer roomId);


    /**
     *  获得展示的商品
     * @param roomId
     * @return
     */
    List<TbLiveGoodsDO> GetShowGoods(Integer roomId);

    /**
     *  商品展示上下屏
     * @param roomId
     * @param spuCode
     * @param type  0 下屏 1上屏
     * @return
     */
    int upAndDownGood(Integer roomId,String spuCode,Integer type);

    /**
     * 获得橱窗商品
     * @param roomId  roomId
     * @param spuCode  spuCode
     * @return
     */
    TbLiveGoodsDO getTbLiveGood(Integer roomId,String spuCode);

    /**
     * 处理橱窗排序
     * @param userId 用户id
     * @param roomId 直播间id
     * @param spuCodeList 商品列表
     * @return int
     */
    int updateLiveGoodsPriority(Integer userId, Integer roomId, List<String> spuCodeList);

}
