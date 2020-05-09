package com.jiebai.qqsk.live.service.impl;

import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.framework.service.AbstractService;
import com.jiebai.qqsk.goods.remote.RemotePopGoodsService;
import com.jiebai.qqsk.live.constant.OpenStatusEnum;
import com.jiebai.qqsk.live.dao.TbLivePopShopMapper;
import com.jiebai.qqsk.live.dao.TbLivePopshopLogMapper;
import com.jiebai.qqsk.live.dto.LivePopshopLogDTO;
import com.jiebai.qqsk.live.enums.PopshopLogStatusEnum;
import com.jiebai.qqsk.live.enums.PopshopStatusEnum;
import com.jiebai.qqsk.live.model.LivePopShopManagerQueryDO;
import com.jiebai.qqsk.live.model.TbLivePopShopDO;
import com.jiebai.qqsk.live.model.TbLivePopshopLogDO;
import com.jiebai.qqsk.live.service.TbLivePopShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @author cxy
 * @version v1.0.0
 * @date 2019/12/17 19:11:15
 */
@Slf4j
@Service
public class TbLivePopShopImpl extends AbstractService<TbLivePopShopDO> implements TbLivePopShopService {

    private static BaseBeanCopier<LivePopshopLogDTO, TbLivePopshopLogDO> LIVEPOPSHOPLOG_DTO2DO_COPIER =
            new SimpleBeanCopier<>(LivePopshopLogDTO.class, TbLivePopshopLogDO.class);
    @Resource
    private TbLivePopShopMapper tbLivePopShopMapper;

    @Resource
    private TbLivePopshopLogMapper tbLivePopshopLogMapper;

    @Reference(version = "${consumer.goods.version}", validation = "false")
    private RemotePopGoodsService remotePopGoodsService;

    @Override
    public Boolean isOpenLive(Integer userId) {
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopMapper.getByUserId(userId);
        if (Objects.isNull(tbLivePopShopDO)){
            return  false;
        }
        else{
            if (tbLivePopShopDO.getIsOpenLive().equals(1) && tbLivePopShopDO.getIsCloseLive().equals(0)){
                return  true;
            }
        }
        return false;
    }

    @Override
    public Boolean isOpenPopShop(Integer userId) {
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopMapper.getByUserId(userId);
        if (Objects.isNull(tbLivePopShopDO)){
            return  false;
        }
        else{
            if (tbLivePopShopDO.getIsOpenPopShop().equals(1) && tbLivePopShopDO.getIsClosePopShop().equals(0)){
                return  true;
            }
        }
        return false;
    }

    @Override
    public Boolean openLive(Integer userId) {
        TbLivePopShopDO tbLivePopShopDO = new TbLivePopShopDO();
        tbLivePopShopDO.setUserId(userId);
        tbLivePopShopDO.setIsOpenLive(1);
        tbLivePopShopDO.setGmtCreate(new Date());
        tbLivePopShopDO.setGmtModified(new Date());
        try {
            this.save(tbLivePopShopDO);
            return true;
        }
        catch (Exception ex){
            log.error("---------tb_live_popshop插入失败--------------" +ex.getMessage());
            return false;
        }
    }

    @Override
    public Boolean openLivePopShop(Integer userId, BigDecimal promiseMoney) {
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopMapper.getByUserId(userId);
        if (Objects.isNull(tbLivePopShopDO)){
            return  false;
        }
        else {
            tbLivePopShopDO.setIsOpenPopShop(1);
            tbLivePopShopDO.setGmtModified(new Date());
            tbLivePopShopDO.setGmtOpenPopShop(new Date());
            tbLivePopShopDO.setPromiseMoney(promiseMoney);
            this.updateById(tbLivePopShopDO);
            return  true;
        }
    }

    @Override
    public TbLivePopShopDO selectOneByUserId(Integer userId) {
        TbLivePopShopDO tbLivePopShopDO = new TbLivePopShopDO();
        tbLivePopShopDO.setUserId(userId);
        return tbLivePopShopMapper.selectOne(tbLivePopShopDO);
    }

    @Override
    public int manageOpenLive(Integer userId) {
        TbLivePopShopDO record = new TbLivePopShopDO();
        record.setUserId(userId);
        //设置为开通
        record.setIsOpenLive(OpenStatusEnum.IS_OPEN.getStatus());
        return tbLivePopShopMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimarySelective(Integer popId) {
        TbLivePopShopDO record = new TbLivePopShopDO();
        record.setId(popId);
        record.setIsOpenLive(OpenStatusEnum.IS_OPEN.getStatus());
        record.setGmtModified(new Date());
        return tbLivePopShopMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<TbLivePopShopDO> getDiscoverLiveList(List<Integer> discoverUserIdList) {
        Condition condition = new Condition(TbLivePopShopDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andIn("userId", discoverUserIdList);
        //开通过
        criteria.andEqualTo("isOpenLive", OpenStatusEnum.IS_OPEN.getStatus());
        //未关闭
        criteria.andEqualTo("isCloseLive", OpenStatusEnum.NOT_OPEN.getStatus());
        return tbLivePopShopMapper.selectByCondition(condition);
    }

    @Override
    public int updateByPrimarySelective(TbLivePopShopDO tbLivePopShopDO) {
        return tbLivePopShopMapper.updateByPrimaryKeySelective(tbLivePopShopDO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int checkPopShop(LivePopshopLogDTO livePopshopLogDTO) {
        Integer popshopId = livePopshopLogDTO.getPopshopId();
        Integer passStatus = livePopshopLogDTO.getPassStatus();
        TbLivePopShopDO resultDO = tbLivePopShopMapper.selectByPrimaryKey(popshopId);
        TbLivePopShopDO updatePopShopDO = new TbLivePopShopDO();
        updatePopShopDO.setId(popshopId);
        TbLivePopshopLogDO record = LIVEPOPSHOPLOG_DTO2DO_COPIER.copy(livePopshopLogDTO);
        //如果审核不通过
        if (passStatus == 0) {
            record.setOperateName("时刻小店审核不通过");
            record.setStatus(PopshopLogStatusEnum.TWO.getStatus());
            updatePopShopDO.setStatus(PopshopStatusEnum.TWO.getStatus());
            remotePopGoodsService.takeOffAllPopShopProductByUserId(resultDO.getUserId());
        } else {
            record.setOperateName("时刻小店审核通过");
            record.setStatus(PopshopLogStatusEnum.THREE.getStatus());
            //审核通过更改状态
            updatePopShopDO.setStatus(PopshopStatusEnum.THREE.getStatus());
        }
        tbLivePopShopMapper.updateByPrimaryKeySelective(updatePopShopDO);
        return tbLivePopshopLogMapper.insertSelective(record);
    }

    @Override
    public List<TbLivePopShopDO> getListForManager(LivePopShopManagerQueryDO tbLivePopShopDO) {
        return tbLivePopShopMapper.getListForManager(tbLivePopShopDO);
    }

    @Override
    public TbLivePopShopDO getLivePopShopInfor(Integer popShopId) {
        return tbLivePopShopMapper.getLivePopShopInfor(popShopId);
    }
}
