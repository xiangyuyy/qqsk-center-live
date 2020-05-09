package com.jiebai.qqsk.live.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_live_category")
public class TbLiveCategoryDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String mark;

    private Integer weight;

    /**
     * 是否可用  测试
1:可用
0:禁用
     */
    private Integer enable;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return mark
     */
    public String getMark() {
        return mark;
    }

    /**
     * @param mark
     */
    public void setMark(String mark) {
        this.mark = mark;
    }

    /**
     * @return weight
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * @param weight
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * 获取是否可用
1:可用
0:禁用
     *
     * @return enable - 是否可用
1:可用
0:禁用
     */
    public Integer getEnable() {
        return enable;
    }

    /**
     * 设置是否可用
1:可用
0:禁用
     *
     * @param enable 是否可用
1:可用
0:禁用
     */
    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}