package com.jiebai.qqsk.live;

import com.alibaba.fastjson.JSONObject;
import com.jiebai.qqsk.live.dto.LivePopShopDTO;
import com.jiebai.qqsk.live.dto.PopHomePageDTO;
import com.jiebai.qqsk.live.enums.PopshopTypeEnum;
import com.jiebai.qqsk.live.remote.RemoteLivePopShopService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiveApplication.class)
public class RemoteLivePopShopServiceTest {
    @Autowired
    private RemoteLivePopShopService remoteLivePopShopService;

    @Test
    public void testGetPopshopType(){
        List<Map<String, String>> popshopType = remoteLivePopShopService.getPopshopType();
        System.out.println(JSONObject.toJSONString(popshopType));
    }

    @Test
    public void testGetPopshopCatgory(){
        List<Map<String, String>> popshopCatgory = remoteLivePopShopService.getPopshopCatgory();
        System.out.println(JSONObject.toJSONString(popshopCatgory));
    }

    @Test
    public void testGetLivePopShopInfo(){
        LivePopShopDTO livePopShopInfo = remoteLivePopShopService.getLivePopShopInfo(13423784);
        System.out.println(JSONObject.toJSONString(livePopShopInfo));
    }

    @Test
    public void testUpdateLivePopShop(){
        LivePopShopDTO livePopShopDTO = new LivePopShopDTO();
        livePopShopDTO.setPopshopName("修改名字2");
        livePopShopDTO.setPopshopType(PopshopTypeEnum.ENTERPRISE.getType());
        livePopShopDTO.setPopshopCategory("103");
        livePopShopDTO.setUserId(13423784);
        livePopShopDTO.setId(1066);
        livePopShopDTO.setType("VERIFY");
        remoteLivePopShopService.updateLivePopShop(livePopShopDTO);
    }

    @Test
    public void testGetPopShopHomePageData(){
        PopHomePageDTO popShopHomePageData = remoteLivePopShopService.getPopShopHomePageData(13087650);
        System.out.println(JSONObject.toJSONString(popShopHomePageData));
    }

}
