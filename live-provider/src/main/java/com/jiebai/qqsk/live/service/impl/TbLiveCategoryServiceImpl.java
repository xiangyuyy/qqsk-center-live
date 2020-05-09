package com.jiebai.qqsk.live.service.impl;

import com.jiebai.framework.service.AbstractService;

import com.jiebai.qqsk.live.dao.TbLiveCategoryMapper;
import com.jiebai.qqsk.live.model.TbLiveCategoryDO;
import com.jiebai.qqsk.live.service.TbLiveCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author lilin
 * @date 2020/02/09 16:22:47
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TbLiveCategoryServiceImpl extends AbstractService<TbLiveCategoryDO> implements TbLiveCategoryService {
    @Resource
    private TbLiveCategoryMapper tbLiveCategoryMapper;

    @Override
    public List<TbLiveCategoryDO> getAllEnableMark() {
        return tbLiveCategoryMapper.getAllEnableMark();
    }

    @Override
    public List<TbLiveCategoryDO> getAllMark() {
        return tbLiveCategoryMapper.getAllMark();
    }

    @Override
    public List<TbLiveCategoryDO> getLiveCategory(Integer id) {
        return tbLiveCategoryMapper.getLiveCategory(id);
    }

    @Override
    public TbLiveCategoryDO getByName(String name) {
        return tbLiveCategoryMapper.getByName(name);
    }

}
