package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_live_im_token")
public class TbLiveImTokenDO {
    /**
     * 观众的userId
     */
    @Id
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 融云用户登录token
     */
    @Column(name = "im_user_token")
    private String imUserToken;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;
}