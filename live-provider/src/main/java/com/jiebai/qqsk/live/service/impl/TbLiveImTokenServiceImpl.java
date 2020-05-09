package com.jiebai.qqsk.live.service.impl;

import com.jiebai.framework.service.AbstractService;
import com.jiebai.qqsk.live.dao.TbLiveImTokenMapper;
import com.jiebai.qqsk.live.model.TbLiveImTokenDO;
import com.jiebai.qqsk.live.service.TbLiveImTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 15:57:21
 */
@Service
//@Transactional(rollbackFor = Exception.class)
public class TbLiveImTokenServiceImpl extends AbstractService<TbLiveImTokenDO> implements TbLiveImTokenService {
    @Resource
    private TbLiveImTokenMapper tbLiveImTokenMapper;

    @Override
    public void updateById(TbLiveImTokenDO model) {
    super.updateById(model);
    }

    @Override
    public TbLiveImTokenDO getById(Integer id) {
    return super.getById(id);
    }

    @Override
    public void removeById(Integer id) {
    super.removeById(id);
    }

    @Override
    public TbLiveImTokenDO getImTokenByUserId(Integer userId) {
        return tbLiveImTokenMapper.getImTokenByUserId(userId);
    }

    @Override
    public int getTodayTokenCount() {
        return tbLiveImTokenMapper.getTodayTokenCount();
    }
}
