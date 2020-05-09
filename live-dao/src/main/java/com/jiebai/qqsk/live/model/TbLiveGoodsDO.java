package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "tb_live_goods")
public class TbLiveGoodsDO {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 主播的userId
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 直播间id
     */
    @Column(name = "room_id")
    private Integer roomId;

    /**
     * 商品spuCode
     */
    @Column(name = "spu_code")
    private String spuCode;

    /**
     * 展示状态：0没操作，1待讲解，2讲解中
     */
    @Column(name = "show_state")
    private Integer showState;
}