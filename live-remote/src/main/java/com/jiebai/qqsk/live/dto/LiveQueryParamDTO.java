package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 直播首页头部搜索条件
 *
 * @author cxy
 */
@Data
public class LiveQueryParamDTO implements Serializable{
    private static final long serialVersionUID = -8242801417396539142L;
    private Integer pageNum;
    private Integer pageSize;
    private Integer userId;
    private String search;
}