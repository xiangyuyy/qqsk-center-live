package com.jiebai.qqsk.live.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.jiebai.qqsk.live.config.YunConfig;
import io.rong.RongCloud;
import io.rong.messages.BaseMessage;
import io.rong.methods.chatroom.Chatroom;
import io.rong.methods.chatroom.ban.Ban;
import io.rong.methods.chatroom.block.Block;
import io.rong.methods.chatroom.gag.Gag;
import io.rong.methods.chatroom.keepalive.Keepalive;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.chatroom.ChatroomMember;
import io.rong.models.chatroom.ChatroomModel;
import io.rong.models.message.ChatroomMessage;
import io.rong.models.response.*;
import io.rong.models.user.UserModel;
import io.rong.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * @author xiaoh
 * @description: 融云服务
 * @date 2019/11/1218:18
 */
@Service
@Slf4j
public class RongYunService {
    private static RongCloud rongCloud = null;
    ;
    @NacosValue(value = "${rongyun_appKey}", autoRefreshed = true)
    private String rongyun_appKey;

    @NacosValue(value = "${rongyun_appSecret}", autoRefreshed = true)
    private String rongyun_appSecret;

    public static RongCloud getInstance(String key,String appSecret){
        if(Objects.isNull(rongCloud)){
            synchronized(RongCloud.class) {
                if(Objects.isNull(rongCloud)){
                    rongCloud = RongCloud.getInstance(key, appSecret);
                }
            }
        }
        return rongCloud;
    }

    /**
     * 获得融云房间id
     *
     * @param userId
     * @return
     */
    public String getImId(Integer userId) {
        return YunConfig.rongyun_imKeyPrefix + "-" + userId + "-" + String.valueOf(new Date().getTime());
    }

    /**
     * 创建融云聊天室
     *
     * @param imId
     * @return
     */
    public ResponseResult create(String imId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Chatroom chatroom = rongCloud.chatroom;
        ChatroomModel[] chatrooms = {
                new ChatroomModel().setId(imId).setName("RoomName" + imId)
        };
        try {
            result = chatroom.create(chatrooms);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(imId + "--------------- 创建融云聊天室报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 销毁融云聊天室
     *
     * @param imId
     * @return
     */
    public ResponseResult destroy(String imId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        ChatroomModel chatroomModel = new ChatroomModel().setId(imId);
        Chatroom chatroom = rongCloud.chatroom;
        try {
            result = chatroom.destroy(chatroomModel);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(imId + "--------------- 销毁融云聊天室报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 融云获得token
     *
     * @param userId
     * @param nickName
     * @param imgUrl
     * @return
     */
    public TokenResult Register(String userId, String nickName, String imgUrl) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        TokenResult result = null;
        User userCloud = rongCloud.user;
        UserModel user = new UserModel().setId(userId).setName(nickName).setPortrait(imgUrl);
        try {
            result = userCloud.register(user);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(userId + "--------------- 融云获得token报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 检查用户在线状态
     *
     * @param userId
     * @return
     */
    public CheckOnlineResult checkOnline(String userId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        CheckOnlineResult result = null;
        User userCloud = rongCloud.user;
        UserModel user = new UserModel().setId(userId);
        try {
            result = userCloud.onlineStatus.check(user);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(userId + "--------------- 融云检查用户在线状态报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 融云更新用户信息
     *
     * @param userId
     * @param nickName
     * @param imgUrl
     * @return
     */
    public Result update(String userId, String nickName, String imgUrl) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        Result result = null;
        User userCloud = rongCloud.user;
        UserModel user = new UserModel().setId(userId).setName(nickName).setPortrait(imgUrl);
        try {
            result = userCloud.update(user);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(userId + "--------------- 融云更新用户信息报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 融云获得用户信息
     *
     * @param userId
     * @return
     */
    public UserResult getUser(String userId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        UserResult result = null;
        User userCloud = rongCloud.user;
        UserModel user = new UserModel().setId(userId);
        try {
            result = userCloud.get(user);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(userId + "--------------- 融云获得用户信息报错 ------------"
                    + e.getMessage());
        }
        return result;
    }


    /**
     * 获得融云聊天室所有用户
     *
     * @param imId
     * @return
     */
    public ChatroomUserQueryResult getUsers(String imId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        getInstance(rongyun_appKey,rongyun_appSecret);
        ChatroomUserQueryResult result = null;
        ChatroomModel chatroomModel = new ChatroomModel().setId(imId).setCount(500).setOrder(1);
        Chatroom chatroom = rongCloud.chatroom;
        try {
            result = chatroom.get(chatroomModel);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(imId + "--------------- 获得融云聊天室所有用户报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 查询融云聊天室成员是否存在
     *
     * @param imId
     * @param userId
     * @return
     */
    public CheckChatRoomUserResult getUsers(String imId, String userId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        CheckChatRoomUserResult result = null;
        ChatroomMember member = new ChatroomMember().setId(imId).setChatroomId(userId);
        Chatroom chatroom = rongCloud.chatroom;
        try {
            result = chatroom.isExist(member);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(imId + "--------------- 查询融云聊天室成员是否存在报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 添加聊天室全局禁言成员
     *
     * @return
     */
    public ResponseResult addBan(ChatroomMember[] members, Integer minute) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Ban ban = rongCloud.chatroom.ban;
        ChatroomModel chatroom = new ChatroomModel()
                .setMembers(members)
                .setMinute(minute);
        try {
            result = ban.add(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(members.toString() + "--------------- 添加聊天室全局禁言成员报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 删除聊天室全局禁言成员
     *
     * @return
     */
    public ResponseResult removeBan(ChatroomMember[] members) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Ban ban = rongCloud.chatroom.ban;
        ChatroomModel chatroom = new ChatroomModel()
                .setMembers(members);
        try {
            result = ban.remove(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(members.toString() + "--------------- 删除聊天室全局禁言成员报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 获取聊天室全局禁言列表
     *
     * @return
     */
    public ListGagChatroomUserResult getBanUsers() {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ListGagChatroomUserResult result = null;
        Ban ban = rongCloud.chatroom.ban;
        try {
            result = ban.getList();
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("--------------- 获取聊天室全局禁言列表报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 添加聊天室禁言成员
     *
     * @return
     */
    public ResponseResult addGag(String imId, ChatroomMember[] members, Integer minute) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Gag gag = rongCloud.chatroom.gag;
        ChatroomModel chatroom = new ChatroomModel().setId(imId)
                .setMembers(members)
                .setMinute(minute);
        try {
            result = gag.add(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(members.toString() + "--------------- 添加聊天室禁言成员报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 删除聊天室禁言成员
     *
     * @return
     */
    public ResponseResult removeGag(String imId, ChatroomMember[] members) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Gag gag = rongCloud.chatroom.gag;
        ChatroomModel chatroom = new ChatroomModel().setId(imId)
                .setMembers(members);
        try {
            result = gag.remove(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(members.toString() + "--------------- 删除聊天室禁言成员报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 获取聊天室禁言列表
     *
     * @return
     */
    public ListGagChatroomUserResult getGagUsers(String imId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ListGagChatroomUserResult result = null;
        Gag gag = rongCloud.chatroom.gag;
        ChatroomModel chatroom = new ChatroomModel().setId(imId);
        try {
            result = gag.getList(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("--------------- 获取聊天室禁言列表报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 添加封禁聊天室成员
     *
     * @return
     */
    public ResponseResult addBlock(String imId, ChatroomMember[] members, Integer minute) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Block block = rongCloud.chatroom.block;
        ChatroomModel chatroom = new ChatroomModel().setId(imId)
                .setMembers(members)
                .setMinute(minute);
        try {
            result = block.add(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(members.toString() + "--------------- 添加封禁聊天室成员报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 删除封禁聊天室成员
     *
     * @return
     */
    public ResponseResult removeBlock(String imId, ChatroomMember[] members) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Block block = rongCloud.chatroom.block;
        ChatroomModel chatroom = new ChatroomModel().setId(imId)
                .setMembers(members);
        try {
            result = block.remove(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(members.toString() + "--------------- 删除封禁聊天室成员报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 获取封禁聊天室成员列表
     *
     * @return
     */
    public ListBlockChatroomUserResult getBlockUsers(String imId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ListBlockChatroomUserResult result = null;
        Block block = rongCloud.chatroom.block;
        try {
            result = block.getList(imId);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("--------------- 获取封禁聊天室成员列表报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 发送聊天室欢迎消息
     *
     * @return
     */
    public ResponseResult sendChatroomWelcomeMessage(String[] chatroomIds ,Integer userId,String nickName) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        ChatroomWelcomeMessage ctm = new ChatroomWelcomeMessage("hello word",nickName);
        ChatroomMessage chatroomMessage = new ChatroomMessage()
                .setSenderId(userId.toString())
                .setTargetId(chatroomIds)
                .setContent(ctm)
                .setObjectName("RC:Chatroom:Welcome");
        try {
            result = rongCloud.message.chatroom.send(chatroomMessage);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(JSONObject.toJSONString(chatroomMessage) + "--------------- 发送聊天室消息失败 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 添加保活聊天室
     *
     * @return
     */
    public ResponseResult addKeepalive(String imId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Keepalive keepalive = rongCloud.chatroom.keepalive;
        ChatroomModel chatroom = new ChatroomModel().setId(imId);
        try {
            result = keepalive.add(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(imId + "--------------- 添加保活聊天室报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 删除保活聊天室
     *
     * @return
     */
    public ResponseResult removeKeepalive(String imId) {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ResponseResult result = null;
        Keepalive keepalive = rongCloud.chatroom.keepalive;
        ChatroomModel chatroom = new ChatroomModel().setId(imId);
        try {
            result = keepalive.remove(chatroom);
        } catch (Exception e) {
            //e.printStackTrace();
            log.info(imId + "--------------- 删除保活聊天室报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     * 获取保活聊天室列表
     *
     * @return
     */
    public ChatroomKeepaliveResult getKeepalives() {
        getInstance(rongyun_appKey,rongyun_appSecret);
        ChatroomKeepaliveResult result = null;
        Keepalive keepalive = rongCloud.chatroom.keepalive;
        try {
            result = keepalive.getList();
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("--------------- 获取保活聊天室列表报错 ------------"
                    + e.getMessage());
        }
        return result;
    }

    /**
     *  ChatroomWelcomeMessage 自定义匹配APP 端消息
     */
    public class ChatroomWelcomeMessage extends BaseMessage {
        private String content = "";
        private String extra = "";
        private static final transient String TYPE = "RC:Chatroom:Welcome";

        public ChatroomWelcomeMessage(String content, String extra) {
            this.content = content;
            this.extra = extra;
        }

        public String getType() {
            return "RC:Chatroom:Welcome";
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getExtra() {
            return this.extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String toString() {
            return GsonUtil.toJson(this, ChatroomWelcomeMessage.class);
        }
    }

}
