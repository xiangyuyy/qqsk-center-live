package com.jiebai.qqsk.live.dto;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 关注列表直播分页实体
 * @author lichenguang
 * 2020/01/10
 */
@Data
public class DiscoverLivePageDTO implements Serializable {


    private static final long serialVersionUID = -4623632998881118434L;

    /**
     * 用户头像列表
     */
    private List<String> headImageUrls;

    private PageInfo<LiveHomeListDTO> pageInfo;

}
