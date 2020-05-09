package com.jiebai.qqsk.live.remote;

import com.github.pagehelper.PageInfo;
import com.jiebai.qqsk.live.dto.DiscoverLivePageDTO;
import com.jiebai.qqsk.live.dto.DiscoverPersonalLiveDTO;
import com.jiebai.qqsk.live.dto.LiveHomeListDTO;
import com.jiebai.qqsk.live.dto.LiveParamDTO;

import java.util.List;
import java.util.Set;

/**
 * @author lichenguang
 * date 2020/1/10
 */
public interface RemoteLiveDiscoverService {

    /**
     * 获取关注人中正在直播的set
     * @return Map<Integer, Boolean> key: userId, value: 是否在直播
     */
    Set<Integer> getDiscoverLivingUserIdList(List<Integer> userIdList);

    /**
     * 获取用户关注的主播和正在直播的列表
     * @param liveParamDTO 分页参数
     * @return DiscoverLivePageDTO
     */
    DiscoverLivePageDTO getDiscoverLiveListByUserId(LiveParamDTO liveParamDTO);

    /**
     * 获取关注的主播直播间列表
     * @param liveParamDTO 分页参数
     * @return PageInfo
     */
    PageInfo<LiveHomeListDTO> getFollowedLiveList(LiveParamDTO liveParamDTO);

    /**
     * 获取关注的个人最近的直播
     * @param userId 用户id
     * @return DiscoverPersonalLiveDTO
     */
    DiscoverPersonalLiveDTO getFollowPersonalLive(Integer userId);
}
