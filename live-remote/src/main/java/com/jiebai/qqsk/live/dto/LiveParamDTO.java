package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 直播首页列表搜索条件
 *
 * @author cxy
 */
@Data
public class LiveParamDTO implements Serializable{
    private static final long serialVersionUID = -8242801417396539142L;
    /**
     * 页数
     */
    private Integer pageNum;
    /**
     * 页容量
     */
    private Integer pageSize;
    /**
     * 房间号
     */
    private Integer roomId;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 直播类型
     */
    private String category;
}