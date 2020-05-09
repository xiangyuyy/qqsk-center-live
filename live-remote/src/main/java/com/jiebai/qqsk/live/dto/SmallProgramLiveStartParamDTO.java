package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lichenguang
 * @date 2019/12/4
 */
@Data
public class SmallProgramLiveStartParamDTO implements Serializable {

    private static final long serialVersionUID = 9066775323335370662L;
    /**
     * 用户openId
     */
    private List<String> toUserList;

    /**
     * 直播间id
     */
    private Integer roomId;

    /**
     * 预约时间
     */
    private String appointStart;
}
