package com.jiebai.qqsk.live.service.impl;

import com.jiebai.framework.service.AbstractService;
import com.jiebai.qqsk.live.dao.TbLivePopshopLogMapper;
import com.jiebai.qqsk.live.model.TbLivePopshopLogDO;
import com.jiebai.qqsk.live.service.TbLivePopshopLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by lichenguang
 * @author lichenguang
 * @version v1.0.0
 * @date 2020/02/17 18:16:50
 */
@Service
public class TbLivePopshopLogServiceImpl extends AbstractService<TbLivePopshopLogDO> implements TbLivePopshopLogService {
    @Resource
    private TbLivePopshopLogMapper tbLivePopshopLogMapper;

    @Override
    public void updateById(TbLivePopshopLogDO model) {
    super.updateById(model);
    }

    @Override
    public TbLivePopshopLogDO getById(Integer id) {
    return super.getById(id);
    }

    @Override
    public void removeById(Integer id) {
    super.removeById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertSelective(TbLivePopshopLogDO tbLivePopshopLogDO) {
        return tbLivePopshopLogMapper.insertSelective(tbLivePopshopLogDO);
    }

    @Override
    public TbLivePopshopLogDO getMarkByUserId(Integer userId) {
        return tbLivePopshopLogMapper.getMarkByUserId(userId);
    }

    @Override
    public List<TbLivePopshopLogDO> getLogsByPopShopId(Integer popShopId) {
        return tbLivePopshopLogMapper.getLogsByPopShopId(popShopId);
    }
}
