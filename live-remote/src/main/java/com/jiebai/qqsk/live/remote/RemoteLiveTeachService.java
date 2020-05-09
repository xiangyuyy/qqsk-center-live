package com.jiebai.qqsk.live.remote;

import com.jiebai.qqsk.live.dto.LiveStarDTO;

import java.util.List;

/**
 *
 * @author : eyestarrysky
 * date : Created in 2020/2/28
 */
public interface RemoteLiveTeachService {

    /**
     * 查询红人列表
     * @param userId 观众的userId
     * @return List
     */
    List<LiveStarDTO> findLiveStarList(Integer userId);
}
