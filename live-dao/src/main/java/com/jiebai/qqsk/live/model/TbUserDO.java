package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "tb_user")
public class TbUserDO implements Serializable {
    private static final long serialVersionUID = 88853453312L;
    /**
     * 自增主键
     */
    @Id
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 所属团队主键
     */
    @Column(name = "team_id")
    private Integer teamId;

    /**
     * 公众号openid
     */
    @Column(name = "weixin_openid")
    private String weixinOpenid;

    /**
     * app开放平台授权openid
     */
    @Column(name = "app_openid")
    private String appOpenid;

    /**
     * 店铺店主姓名
     */
    @Column(name = "owner_name")
    private String ownerName;

    /**
     * 微信UNIONID
     */
    @Column(name = "weixin_unionid")
    private String weixinUnionid;

    /**
     * 代理商是否 ‘资深’级别  0-否 1-是
     */
    private Boolean senior;

    /**
     * 微信名
     */
    private String nickname;

    /**
     * 头像路径
     */
    private String headimgurl;

    /**
     * Tb_Groupon_User表中数据
     * '联创GROUPON_LEADER; 代理GROUPON_AGENT; 团长GROUPON_FOUNDER; 游客GROUPON_GUEST;
     */
    @Transient
    private String memberLevel;


    /**
     * 微信号
     */
    @Column(name = "weixin_number")
    private String weixinNumber;

    /**
     * 游客-GUEST(非会员); 金卡-FANS; 黑卡-NORMAL; 旗舰店-ULTIMATE; 大客户经理-MANAGER; 合伙人-PARTNER; 购物满199体验会员-EXPERIENCE;  (可成为会员)-QUALIFIED; 原688会员-688NORMAL; 高佣体验会员-EXPERIENCE_GY;
     */
    @Column(name = "user_member_role")
    private String userMemberRole;

    /**
     * 性别'UNKNOWN','MALE'男,'FEMALE'女
     */
    private String gender;

    /**
     * 直推上一级id(必须是会员,游客是不能推荐的)
     */
    @Column(name = "parent_member_id")
    private Integer parentMemberId;

    /**
     * 真实姓名(实名制)
     */
    private String realname;

    /**
     * 身份证号
     */
    @Column(name = "id_card_number")
    private String idCardNumber;

    /**
     * 登录号码(用户用电话注册)
     */
    @Column(name = "login_mobile")
    private String loginMobile;

    /**
     * 密码
     */
    private String password;

    /**
     * 银行预留手机号
     */
    private String mobile;

    /**
     * 来源 ZT-中通;CHT-楚天;DNY-迪妮娅蛋糕店;SHY-摄影;JWTP-河北酒窝甜品连锁
     */
    private String source;

    /**
     * 最后访问商城Id
     */
    @Column(name = "lastvisit_mallid")
    private Integer lastvisitMallid;

    /**
     * 默认分配店铺Id
     */
    @Column(name = "default_member_id")
    private Integer defaultMemberId;

    /**
     * 所属省份
     */
    @Column(name = "province_name")
    private String provinceName;

    /**
     * 所属地级市
     */
    @Column(name = "city_name")
    private String cityName;

    /**
     * 所属区
     */
    @Column(name = "district_name")
    private String districtName;

    /**
     * 详细地址
     */
    @Column(name = "street_address")
    private String streetAddress;

    /**
     * 店铺名称
     */
    @Column(name = "shop_name")
    private String shopName;

    /**
     * 店铺描述
     */
    @Column(name = "shop_desc")
    private String shopDesc;

    /**
     * 店铺编号
     */
    @Column(name = "shop_number")
    private String shopNumber;

    /**
     * 结算余额  单位:分
     */
    @Column(name = "cash_balance")
    private Long cashBalance;

    /**
     * WXWALLET' 微信钱包 'ALIPAY'  支付宝  'BANK'  alter table tb_user modify column银行卡
     */
    @Column(name = "withdraw_method")
    private String withdrawMethod;

    /**
     * 银行卡持有者
     */
    private String payee;

    /**
     * 支付宝账户
     */
    @Column(name = "pay_account")
    private String payAccount;

    /**
     * 银行卡号
     */
    private String cardnumber;

    /**
     * 所属银行(开户行)
     */
    private String cardbank;

    /**
     * 标识
     */
    private String mark;

    /**
     * 备注
     */
    private String remark;

    /**
     * 金币数量
     */
    @Column(name = "gold_number")
    private BigDecimal goldNumber;

    /**
     * 人民币数量(直推店铺)
     */
    @Column(name = "rmb_number")
    private Long rmbNumber;

    /**
     * 可提现总金额(在会员日期内)
     */
    @Column(name = "cash_withdrawal")
    private BigDecimal cashWithdrawal;

    /**
     * 充值会员可提现总额
     */
    @Column(name = "recharge_cash_withdrawal")
    private BigDecimal rechargeCashWithdrawal;

    /**
     * 升级提现
     */
    @Column(name = "upgrade_withdrawal")
    private BigDecimal upgradeWithdrawal;

    /**
     * 授权日期
     */
    @Column(name = "gmt_license")
    private Date gmtLicense;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 将要获得金币
     */
    @Column(name = "will_receive_gold")
    private BigDecimal willReceiveGold;

    /**
     * 将要获得人民币
     */
    @Column(name = "will_receive_rmb")
    private Long willReceiveRmb;

    /**
     * 黑名单 0-否 1-是
     */
    @Column(name = "black_list")
    private Byte blackList;

    /**
     * 旗舰店ID
     */
    @Column(name = "flagship_id")
    private Integer flagshipId;

    /**
     * 客户经理Id
     */
    @Column(name = "manager_id")
    private Integer managerId;

    /**
     * 会员过期时间
     */
    @Column(name = "member_expired_time")
    private Date memberExpiredTime;

    /**
     * 微信管家 Id
     */
    @Column(name = "wx_manager_id")
    private Integer wxManagerId;

    /**
     * app开放平台授权openid  工猫
     */
    @Column(name = "app_openid_gongmao")
    private String appOpenidGongmao;

    /**
     * user_uuid
     */
    @Column(name = "user_uuid")
    private String userUuid;

    /**
     * 渠道短码
     */
    @Column(name = "channel_code")
    private String channelCode;

    /**
     * 支付人
     */
    @Column(name = "pay_name")
    private String payName;
}