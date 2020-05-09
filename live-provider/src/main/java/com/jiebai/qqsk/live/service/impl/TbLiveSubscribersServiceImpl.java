package com.jiebai.qqsk.live.service.impl;

import com.jiebai.framework.service.AbstractService;
import com.jiebai.qqsk.live.dao.TbLiveSubscribersMapper;
import com.jiebai.qqsk.live.model.TbLiveSubscribersDO;
import com.jiebai.qqsk.live.service.TbLiveSubscribersService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 16:03:57
 */
@Service
public class TbLiveSubscribersServiceImpl extends AbstractService<TbLiveSubscribersDO> implements
    TbLiveSubscribersService {
    @Resource
    private TbLiveSubscribersMapper tbLiveSubscribersMapper;

    @Override
    public void updateById(TbLiveSubscribersDO model) {
    super.updateById(model);
    }

    @Override
    public TbLiveSubscribersDO getById(Integer id) {
    return super.getById(id);
    }

    @Override
    public void removeById(Integer id) {
    super.removeById(id);
    }

    @Override
    public int insertSelective(TbLiveSubscribersDO tbLiveSubscribersDO) {
        return tbLiveSubscribersMapper.insertSelective(tbLiveSubscribersDO);
    }

    @Override
    public TbLiveSubscribersDO getByRoomIdAndUserId(Integer roomId, Integer userId) {
        TbLiveSubscribersDO tbLiveSubscribersDO = new TbLiveSubscribersDO();
        tbLiveSubscribersDO.setRoomId(roomId);
        tbLiveSubscribersDO.setUserId(userId);
        return tbLiveSubscribersMapper.selectOne(tbLiveSubscribersDO);
    }

    @Override
    public List<Integer> getUserIdByRoomId(Integer roomId) {
        return tbLiveSubscribersMapper.getUserIdByRoomId(roomId);
    }
}
