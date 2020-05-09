package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lilin
 * @date 2020-02-09
 */
@Data
public class LiveCategoryDTO implements Serializable {
    private static final long serialVersionUID = -748329129124L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 标签类型
     */
    private String name;
    /**
     * 标签备注
     */
    private String mark;
    /**
     * 标签权重
     */
    private Integer weight;

    /**
     * 是否可用
     1:可用
     0:禁用
     */
    private Integer enable;

}
