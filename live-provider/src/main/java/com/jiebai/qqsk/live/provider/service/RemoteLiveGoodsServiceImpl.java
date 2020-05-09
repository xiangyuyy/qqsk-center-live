package com.jiebai.qqsk.live.provider.service;

import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.ListCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.qqsk.live.constant.RedisKeyConstant;
import com.jiebai.qqsk.live.dto.LiveGoodsDTO;
import com.jiebai.qqsk.live.dto.SaveLiveGoodsDTO;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceErrorCodeEnum;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceException;
import com.jiebai.qqsk.live.model.TbLiveGoodsDO;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import com.jiebai.qqsk.live.remote.RemoteLiveGoodsService;
import com.jiebai.qqsk.live.service.TbLiveGoodsService;
import com.jiebai.qqsk.live.service.TbLiveRoomService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jiebai.qqsk.live.constant.LiveConstant.EXPIRE_SECOND;

/**
 * @author lichenguang
 * 2019/11/15
 */
@Slf4j
@Component
@Service(interfaceClass = RemoteLiveGoodsService.class, version = "${provider.live.version}", validation = "false", retries = 0,
        timeout = 3000)
public class RemoteLiveGoodsServiceImpl implements RemoteLiveGoodsService {

    private static BaseBeanCopier<LiveGoodsDTO, TbLiveGoodsDO> LIVE_GOODS_DTO2DO_COPIER =
            new SimpleBeanCopier<>(LiveGoodsDTO.class, TbLiveGoodsDO.class);

    private static BaseBeanCopier<TbLiveGoodsDO, LiveGoodsDTO> LIVE_GOODS_DO2DTO_COPIER =
            new SimpleBeanCopier<>(TbLiveGoodsDO.class, LiveGoodsDTO.class);

    @Resource
    private TbLiveGoodsService tbLiveGoodsService;

    @Resource
    private TbLiveRoomService tbLiveRoomService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public int insertMyLiveGood(LiveGoodsDTO liveGoodsDTO) {
        Integer roomId = liveGoodsDTO.getRoomId();
        int total = tbLiveGoodsService.selectCountByUserIdAndRoomId(liveGoodsDTO.getUserId(), roomId);
        int goodsLimitCount = 30;
        if (total >= goodsLimitCount) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_GOODS_COUNT_REACH_LIMIT);
        }
        int count = tbLiveGoodsService.selectCountByRoomIdAndSpuCode(roomId, liveGoodsDTO.getSpuCode());
        if (count > 0) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.EXISET_IN_LIVE_GOODS);
        }
        TbLiveGoodsDO tbLiveGoodsDO = LIVE_GOODS_DTO2DO_COPIER.copy(liveGoodsDTO);
        int res = tbLiveGoodsService.insertSelective(tbLiveGoodsDO);
        if (res > 0) {
            Long expireSecond = stringRedisTemplate.opsForValue().getOperations()
                    .getExpire(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId);
            if (Objects.nonNull(expireSecond) && expireSecond > 1) {
                stringRedisTemplate.opsForValue().increment(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId);
            } else {
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId,
                        String.valueOf(new AtomicInteger(total).incrementAndGet()), EXPIRE_SECOND, TimeUnit.SECONDS);
            }
        }
        return res;
    }

    @Override
    public int updateMyLiveGoodPriority(SaveLiveGoodsDTO saveLiveGoodsDTO) {
        Integer userId = saveLiveGoodsDTO.getUserId();
        Integer roomId = saveLiveGoodsDTO.getRoomId();
        TbLiveRoomDO liveRoomDO = tbLiveRoomService.getById(roomId);
        if (Objects.isNull(liveRoomDO)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
        }
        //如果用户违规操作更新非自己的直播间
        if (!userId.equals(liveRoomDO.getLiveUserId())) {
            log.error("【直播模块-更新橱窗商品排序出现错误】, 用户userId = {}, 进行违规调用！", userId);
            throw new RemoteLiveServiceException("用户错误操作!");
        }
        List<String> spuCodeList = saveLiveGoodsDTO.getSpuCodeList();
        //先倒序排列橱窗商品
        Collections.reverse(spuCodeList);
        //再处理橱窗排序
        int res =  tbLiveGoodsService.updateLiveGoodsPriority(userId, roomId, saveLiveGoodsDTO.getSpuCodeList());
        if (res > 0) {
            stringRedisTemplate.opsForValue().set(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId,
                    spuCodeList.size() + "", EXPIRE_SECOND, TimeUnit.SECONDS);
        }
        return res;
    }

    @Override
    public int deleteMyLiveGood(LiveGoodsDTO liveGoodsDTO) {
        Integer roomId = liveGoodsDTO.getRoomId();
        List<TbLiveGoodsDO> list =
                tbLiveGoodsService.getGoodsByUserIdAndRoomId(liveGoodsDTO.getUserId(), roomId);
        if (CollectionUtils.isEmpty(list) || list.size() <= 1) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_GOODS_CANNOT_DELETED);
        }
        int res = tbLiveGoodsService.deleteByRoomIdAndSpuCode(liveGoodsDTO);
        if (res > 0) {
            //缓存橱窗总数
            Long expireSecond = stringRedisTemplate.opsForValue().getOperations()
                    .getExpire(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId);
            if (Objects.nonNull(expireSecond) && expireSecond > 1) {
                stringRedisTemplate.opsForValue().decrement(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId);
            } else {
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId,
                        String.valueOf(new AtomicInteger(list.size()).decrementAndGet()), EXPIRE_SECOND,
                        TimeUnit.SECONDS);
            }
        }
        return res;
    }

    @Override
    public List<LiveGoodsDTO> getMyLiveGoods(Integer userId, Integer roomId) {
        List<TbLiveGoodsDO> tbLiveGoodsDOList = tbLiveGoodsService.getGoodsByUserIdAndRoomId(userId, roomId);
        if (CollectionUtils.isEmpty(tbLiveGoodsDOList)) {
            return null;
        }
        return ListCopier.transform(tbLiveGoodsDOList, LIVE_GOODS_DO2DTO_COPIER);
    }
}
