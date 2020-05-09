package com.jiebai.qqsk.live.service;

import com.jiebai.qqsk.live.config.YunConfig;
import com.qiniu.pili.Client;
import com.qiniu.pili.Hub;
import com.qiniu.pili.PiliException;
import com.qiniu.pili.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author xiaoh
 * @description: 七牛云服务
 * @date 2019/11/1218:18
 */
@Service
@Slf4j
public class QiNiuService {
    private Client client;
    private Hub hub;

    public QiNiuService() {
        client = new Client(YunConfig.qiniu_accessKey, YunConfig.qiniu_secretKey);
        hub = client.newHub(YunConfig.qiniu_hubName);
    }

    /**
     * 获得流key
     *
     * @param userId
     * @return
     */
    public String getKey(Integer userId) {
        return YunConfig.qiniu_streamKeyPrefix + "-" + userId + "-" + String.valueOf(System.currentTimeMillis());
    }

    public String getBackUrl(String fname) {
        if (StringUtils.isEmpty(fname)) {
            return null;
        } else {
            return "https://"+ YunConfig.qiniu_downloadDomainOfStorageBucket + "/" + fname;
        }
    }

    public String getFlvBackUrl(String fname) {
        if (StringUtils.isEmpty(fname)) {
            return null;
        } else {
            return "https://"+ YunConfig.qiniu_downloadDomainOfStorageBucket + "/" + fname;
        }
    }

    /**
     * 创建流
     *
     * @param key
     * @return
     */
    public Stream create(String key) {
        Stream stream = null;
        try {
            stream = hub.create(key);
        } catch (PiliException e) {
            //e.printStackTrace();
            log.info(key + "--------------- 创建流报错 ------------"
                    + e.toString());
        }
        return stream;
    }

    /**
     * 获得流
     *
     * @param key
     * @return
     */
    public Stream get(String key) {
        Stream stream = null;
        try {
            stream = hub.get(key);
        } catch (PiliException e) {
            //e.printStackTrace();
            log.info(key + "--------------- 获得流报错 ------------"
                    + e.toString());
        }
        return stream;
    }

    /**
     * 获得所有流
     *
     * @return
     */
    public Hub.ListRet getList() {
        Hub.ListRet listRet;
        try {
            listRet = hub.list(YunConfig.qiniu_streamKeyPrefix, 0, "");
        } catch (PiliException e) {
            listRet = null;
            //e.printStackTrace();
            log.info("--------------- 获得所有流报错 ------------"
                    + e.toString());
        }
        return listRet;
    }

    /**
     * 获得所有正在直播的流
     *
     * @return
     */
    public Hub.ListRet getLiveList() {
        Hub.ListRet listRet;
        try {
            listRet = hub.listLive(YunConfig.qiniu_streamKeyPrefix, 0, "");
        } catch (PiliException e) {
            //e.printStackTrace();
            listRet = null;
            log.info("--------------- 获得所有正在直播的流报错 ------------"
                    + e.toString());
        }
        return listRet;
    }

    /**
     * 关闭流
     *
     * @param key
     * @return
     */
    public Stream stop(String key) {
        Stream stream;
        try {
            stream = hub.get(key);
            stream.disable();
            stream = hub.get(key);
        } catch (PiliException e) {
            //e.printStackTrace();
            stream = null;
            log.info(key + "--------------- 关闭流报错 ------------"
                    + e.toString());
        }
        return stream;
    }

    /**
     * 开启流
     *
     * @param key
     * @return
     */
    public Stream start(String key) {
        Stream stream = null;
        try {
            stream = hub.get(key);
            stream.enable();
            stream = hub.get(key);
        } catch (PiliException e) {
            //e.printStackTrace();
            log.info(key + "--------------- 开启流报错 ------------"
                    + e.toString());
        }
        return stream;
    }

    /**
     * 获得流状态
     *
     * @param key
     * @return status 为null 说明流不在直播
     */
    public Stream.LiveStatus getiveStatus(String key) {
        Stream.LiveStatus status;
        try {
            Stream stream = hub.get(key);
            status = stream.liveStatus();
        } catch (PiliException e) {
            //e.printStackTrace();
            status = null;
            log.info(key + "--------------- 获得流状态报错 ------------"
                    + e.toString());
        }
        return status;
    }

    /**
     * 获得流的推流历史
     *
     * @param key
     * @return
     */
    public Stream.Record[] getiveRecords(String key) {
        Stream.Record[] records;
        try {
            Stream stream = hub.get(key);
            records = stream.historyRecord(0, 0);
        } catch (PiliException e) {
            //e.printStackTrace();
            records = null;
            log.info(key + "--------------- 获得流的推流历史报错 ------------"
                    + e.toString());
        }
        return records;
    }

    /**
     * 获得流的回放文件名称
     *
     * @param key
     * @return
     */
    public String getPlayBackFname(String key) {
        String fname;
        try {
            Stream stream = hub.get(key);
            fname = stream.save(0, 0);
        } catch (PiliException e) {
            //e.printStackTrace();
            fname = null;
            log.info(key + "--------------- 获得流的回放文件名称报错 ------------"
                    + e.toString());
        }
        return fname;
    }

    /**
     * 获得流的回放文件名称
     *
     * @param key
     * @return
     */
    public String getPlayBackFname(String key,long start,long end,String format) {
        String fname;
        try {
            Stream stream = hub.get(key);
            Stream.SaveOptions args = new Stream.SaveOptions(start, end);
            args.format = format;
            fname = stream.save(args);
        } catch (PiliException e) {
            //e.printStackTrace();
            fname = null;
            log.info(key + "--------------- 获得流的回放文件名称报错 ------------"
                    + e.toString());
        }
        return fname;
    }

    /**
     * RTMP推流地址
     *
     * @param key
     * @param expireAfterSeconds
     * @return
     */
    public String getRTMPPublishURL(String key, int expireAfterSeconds) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        String url = client.RTMPPublishURL(YunConfig.qiniu_RTMPPublishDomain, YunConfig.qiniu_hubName, key, expireAfterSeconds);
        return url;
    }

    /**
     * RTMP直播地址
     *
     * @param key
     * @return
     */

    public String getRTMPPlayURL(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        String url = client.RTMPPlayURL(YunConfig.qiniu_RTMPPlayURL, YunConfig.qiniu_hubName, key);
        return url;
    }

    /**
     * HLS直播地址
     *
     * @param key
     * @return
     */
    public String getHLSPlayURL(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        String url = client.HLSPlayURL(YunConfig.qiniu_HLSPlayURL, YunConfig.qiniu_hubName, key);
        return url;
    }

    /**
     * HDL直播地址
     *
     * @param key
     * @return
     */
    public String getHDLPlayURL(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        String url = client.HDLPlayURL(YunConfig.qiniu_HDLPlayURL, YunConfig.qiniu_hubName, key);
        return url;
    }

    /**
     * 截图直播地址
     *
     * @param key
     * @return
     */
    public String getSnapshotPlayURL(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        String url = client.SnapshotPlayURL(YunConfig.qiniu_SnapshotPlayURL, YunConfig.qiniu_hubName, key);
        return url;
    }
}
