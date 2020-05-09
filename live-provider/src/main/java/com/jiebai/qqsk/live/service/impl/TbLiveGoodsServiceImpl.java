package com.jiebai.qqsk.live.service.impl;

import com.jiebai.framework.service.AbstractService;
import com.jiebai.qqsk.live.dao.TbLiveGoodsMapper;
import com.jiebai.qqsk.live.dto.LiveGoodsDTO;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceException;
import com.jiebai.qqsk.live.model.TbLiveGoodsDO;
import com.jiebai.qqsk.live.service.TbLiveGoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jiebai.qqsk.live.exception.RemoteLiveServiceErrorCodeEnum.LIVE_GOODS_CANNOT_DELETED;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 15:29:15
 */
@Service
public class TbLiveGoodsServiceImpl extends AbstractService<TbLiveGoodsDO> implements TbLiveGoodsService {
    @Resource
    private TbLiveGoodsMapper tbLiveGoodsMapper;

    @Override
    public void updateById(TbLiveGoodsDO model) {
        super.updateById(model);
    }

    @Override
    public TbLiveGoodsDO getById(Integer id) {
        return super.getById(id);
    }

    @Override
    public void removeById(Integer id) {
        super.removeById(id);
    }


    @Override
    public List<TbLiveGoodsDO> getGoodsByRoomId(Integer roomId) {
        return tbLiveGoodsMapper.getGoodsByRoomId(roomId);
    }

    @Override
    public List<String> getGoodsByRoomIdAndLimit(Integer roomId, Integer count) {
        return tbLiveGoodsMapper.getGoodsByRoomIdAndLimit(roomId, count);
    }

    @Override
    public int insertSelective(TbLiveGoodsDO tbLiveGoodsDO) {
        return tbLiveGoodsMapper.insertSelective(tbLiveGoodsDO);
    }

    @Override
    public List<TbLiveGoodsDO> getGoodsByUserIdAndRoomId(Integer userId, Integer roomId) {
        Condition condition = new Condition(TbLiveGoodsDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("roomId", roomId);
        condition.orderBy("id").desc();
        return tbLiveGoodsMapper.selectByCondition(condition);
    }

    @Override
    public int deleteByRoomIdAndSpuCode(LiveGoodsDTO liveGoodsDTO) {
        TbLiveGoodsDO tbLiveGoodsDO = new TbLiveGoodsDO();
        tbLiveGoodsDO.setUserId(liveGoodsDTO.getUserId());
        tbLiveGoodsDO.setRoomId(liveGoodsDTO.getRoomId());
        tbLiveGoodsDO.setSpuCode(liveGoodsDTO.getSpuCode());
        return tbLiveGoodsMapper.delete(tbLiveGoodsDO);
    }

    @Override
    public int selectCountByRoomIdAndSpuCode(Integer roomId, String spuCode) {
        TbLiveGoodsDO tbLiveGoodsDO = new TbLiveGoodsDO();
        tbLiveGoodsDO.setRoomId(roomId);
        tbLiveGoodsDO.setSpuCode(spuCode);
        return tbLiveGoodsMapper.selectCount(tbLiveGoodsDO);
    }

    @Override
    public TbLiveGoodsDO selectByPrimaryKey(Long id) {
        return tbLiveGoodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public int deleteByUserIdAndRoomId(Integer userId, Integer roomId) {
        Condition condition = new Condition(TbLiveGoodsDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("roomId", roomId);
        return tbLiveGoodsMapper.deleteByCondition(condition);
    }

    @Override
    public int selectCountByUserIdAndRoomId(Integer userId, Integer roomId) {
        TbLiveGoodsDO tbLiveGoodsDO = new TbLiveGoodsDO();
        tbLiveGoodsDO.setUserId(userId);
        tbLiveGoodsDO.setRoomId(roomId);
        return tbLiveGoodsMapper.selectCount(tbLiveGoodsDO);
    }

    @Override
    public List<TbLiveGoodsDO> GetShowGoods(Integer roomId) {
        Condition condition = new Condition(TbLiveGoodsDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("roomId", roomId);
        criteria.andGreaterThan("showState", 0);
        condition.orderBy("showState").desc();
        return tbLiveGoodsMapper.selectByCondition(condition);
    }

    @Override
    public int upAndDownGood(Integer roomId, String spuCode, Integer type) {
        Condition condition = new Condition(TbLiveGoodsDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("roomId", roomId);
        criteria.andEqualTo("spuCode", spuCode);
        TbLiveGoodsDO tbLiveGoodsDO = getTbLiveGood(roomId, spuCode);
        if (tbLiveGoodsDO == null) {
            return -1;
        }
        if (type.equals(0)) {//下屏;
            tbLiveGoodsDO.setShowState(0);//么操作
            Condition conditionOne = new Condition(TbLiveGoodsDO.class);
            Example.Criteria criteriaOne = conditionOne.createCriteria();
            criteriaOne.andEqualTo("roomId", roomId);
            criteriaOne.andEqualTo("showState", 1);
            List<TbLiveGoodsDO> listOne = tbLiveGoodsMapper.selectByCondition(conditionOne);
            if (listOne.size() > 0) {
                TbLiveGoodsDO one = listOne.get(0);
                one.setShowState(2);
                tbLiveGoodsMapper.updateByPrimaryKeySelective(one);
            }
            tbLiveGoodsMapper.updateByPrimaryKeySelective(tbLiveGoodsDO);
            return 1;
        } else { //上屏
            Condition conditionTwo = new Condition(TbLiveGoodsDO.class);
            Example.Criteria criteriaTwo = conditionTwo.createCriteria();
            criteriaTwo.andEqualTo("roomId", roomId);
            criteriaTwo.andEqualTo("showState", 2);
            List<TbLiveGoodsDO> listTwo = tbLiveGoodsMapper.selectByCondition(conditionTwo);
            if (listTwo.size() <= 0) { //没有2 第一次 变成2
                tbLiveGoodsDO.setShowState(2);//讲解中
                return tbLiveGoodsMapper.updateByPrimaryKeySelective(tbLiveGoodsDO);
            } else {
                Condition conditionOne = new Condition(TbLiveGoodsDO.class);
                Example.Criteria criteriaOne = conditionOne.createCriteria();
                criteriaOne.andEqualTo("roomId", roomId);
                criteriaOne.andEqualTo("showState", 1);
                List<TbLiveGoodsDO> listOne = tbLiveGoodsMapper.selectByCondition(conditionOne);
                if (listOne.size() > 0) {
                    TbLiveGoodsDO one = listOne.get(0);
                    TbLiveGoodsDO two = listTwo.get(0);
                    one.setShowState(2);
                    two.setShowState(0);
                    tbLiveGoodsDO.setShowState(1);
                    tbLiveGoodsMapper.updateByPrimaryKeySelective(tbLiveGoodsDO);
                    tbLiveGoodsMapper.updateByPrimaryKeySelective(one);
                    tbLiveGoodsMapper.updateByPrimaryKeySelective(two);
                    return 1;
                } else {
                    tbLiveGoodsDO.setShowState(1);
                    return tbLiveGoodsMapper.updateByPrimaryKeySelective(tbLiveGoodsDO);
                }
            }
        }
    }

    @Override
    public TbLiveGoodsDO getTbLiveGood(Integer roomId, String spuCode) {
        Condition condition = new Condition(TbLiveGoodsDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("roomId", roomId);
        criteria.andEqualTo("spuCode", spuCode);
        List<TbLiveGoodsDO> list = tbLiveGoodsMapper.selectByCondition(condition);
        if (list.size() <= 0) {
            return null;
        }
        TbLiveGoodsDO tbLiveGoodsDO = list.get(0);
        return tbLiveGoodsDO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateLiveGoodsPriority(Integer userId, Integer roomId, List<String> spuCodeList) {
        //先查出
        Condition condition = new Condition(TbLiveGoodsDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("roomId", roomId);
        criteria.andEqualTo("userId", userId);
        List<TbLiveGoodsDO> tbLiveGoodsDOList = tbLiveGoodsMapper.selectByCondition(condition);
        if (CollectionUtils.isEmpty(tbLiveGoodsDOList)) {
            throw new RemoteLiveServiceException(LIVE_GOODS_CANNOT_DELETED);
        }
        Map<String, Integer> goodsStatusMap =
                tbLiveGoodsDOList.stream().collect(Collectors.toMap(TbLiveGoodsDO::getSpuCode,
                        TbLiveGoodsDO::getShowState));
        //再清空
        deleteByUserIdAndRoomId(userId, roomId);
        //再插入新的直播间橱窗
        List<TbLiveGoodsDO> liveGoodsDOList = spuCodeList.stream().map(spuCode -> {
            TbLiveGoodsDO tbLiveGoodsDO = new TbLiveGoodsDO();
            tbLiveGoodsDO.setUserId(userId);
            tbLiveGoodsDO.setRoomId(roomId);
            tbLiveGoodsDO.setSpuCode(spuCode);
            Integer showStatus = goodsStatusMap.get(spuCode);
            tbLiveGoodsDO.setShowState(null == showStatus ? 0 : showStatus);
            return tbLiveGoodsDO;
        }).collect(Collectors.toList());
        return tbLiveGoodsMapper.insertListSelective(liveGoodsDOList);
    }
}
