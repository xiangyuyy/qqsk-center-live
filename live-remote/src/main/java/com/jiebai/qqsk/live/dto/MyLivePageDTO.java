package com.jiebai.qqsk.live.dto;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 直播分页实体
 * @author lichenguang
 * 2019/11/15
 */
@Data
public class MyLivePageDTO implements Serializable {

    private static final long serialVersionUID = 6322786788189088172L;

    /**
     * 是否可以开播，true为可以开播
     */
    private Boolean liveRoomContinue;

    private PageInfo<MyLiveDTO> pageInfo;

}
