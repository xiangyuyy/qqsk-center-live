package com.jiebai.qqsk.live.service.impl;

import com.jiebai.framework.service.AbstractService;
import com.jiebai.qqsk.live.constant.LiveConstant;
import com.jiebai.qqsk.live.dao.TbLiveVisitorMapper;
import com.jiebai.qqsk.live.model.TbLiveVisitorDO;
import com.jiebai.qqsk.live.model.TbUserDO;
import com.jiebai.qqsk.live.service.TbLiveVisitorService;
import com.jiebai.qqsk.live.utils.MapRedisTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 16:00:22
 */
@Service
//@Transactional(rollbackFor = Exception.class)
public class TbLiveVisitorServiceImpl extends AbstractService<TbLiveVisitorDO> implements TbLiveVisitorService {
    @Resource
    private TbLiveVisitorMapper tbLiveVisitorMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void updateById(TbLiveVisitorDO model) {
        super.updateById(model);
    }

    @Override
    public TbLiveVisitorDO getById(Integer id) {
        return super.getById(id);
    }

    @Override
    public void removeById(Integer id) {
        super.removeById(id);
    }

    @Override
    public TbUserDO getTbUserByUserId(Integer userId) {
        return tbLiveVisitorMapper.getTbUserByUserId(userId);
    }

    @Override
    public List<TbLiveVisitorDO> getVisitorByRoomId(Integer roomId) {
        return tbLiveVisitorMapper.getVisitorByRoomId(roomId);
    }

    @Override
    public int getTodayVisitors() {
        return tbLiveVisitorMapper.getTodayVisitors();
    }

    @Override
    public int getRandForChatRomm() {
        return tbLiveVisitorMapper.getRandForChatRomm();
    }

    @Override
    public List<Map<String, Object>> getRandVisitors(int limit) {
        MapRedisTemplateUtils.setMapRedisTemplate(redisTemplate);//初始化
        List<Map<String, Object>> result = (List<Map<String, Object>>) redisTemplate.opsForList().range(LiveConstant.VISITORSREDISKEY, 0, -1);
        int size = result.size();
        if (result == null || result.size() == 0) { //写缓存
            List<Map<String, Object>> all = this.getAllRandVisitors();
            size = all.size();
            redisTemplate.opsForList().leftPushAll(LiveConstant.VISITORSREDISKEY, all);
            redisTemplate.expire(LiveConstant.VISITORSREDISKEY, 30, TimeUnit.DAYS);//30天过期
        }
        Random rd = new Random();
        int rand = rd.nextInt(size - limit - 1);//正常业务肯定是大于0的
        List<Map<String, Object>> list = redisTemplate.opsForList().range(LiveConstant.VISITORSREDISKEY, Long.valueOf(String.valueOf(rand)), Long.valueOf(String.valueOf(rand + limit - 1)));
        Collections.shuffle(list);
        return list;
    }

    @Override
    public List<Map<String, Object>> getAllRandVisitors() {
        return tbLiveVisitorMapper.getAllRandVisitors();
    }
}
