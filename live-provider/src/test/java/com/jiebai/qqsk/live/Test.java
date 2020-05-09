/*
package com.jiebai.qqsk.live;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jiebai.qqsk.live.dto.VisitorActionDTO;
import com.jiebai.qqsk.live.model.TbLiveVisitorDO;
import com.jiebai.qqsk.live.model.TbUserDO;
import com.jiebai.qqsk.live.remote.RemoteLiveShowService;
import com.jiebai.qqsk.live.service.QiNiuService;
import com.jiebai.qqsk.live.service.TbLiveVisitorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiveApplication.class)
public class Test {

    @Autowired
    QiNiuService qiNiuService;
    @Autowired
    RemoteLiveShowService remoteLiveShowService;

    @Autowired
    TbLiveVisitorService tbLiveVisitorService;
    @org.junit.Test
    public void saveManageInfo() {
        //TbUserDO tbUserDO = tbLiveVisitorService.getTbUserByUserId(10);
        //qiNiuService.create(qiNiuService.getKey(123123123));
        //第一次进入直播间 增加观众
        qiNiuService.getPlayBackFname("QQSK-ROOM-13311946-1574994524310",1574994540,1574994600,"flv");

        System.out.println(12);
    }
}
*/
