package com.jiebai.qqsk.live.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

import static com.jiebai.qqsk.live.utils.ConvertorTimeUtils.msecToTime;

/**
 * @author xiaoh
 * @description: 观众平滑曲线
 * @date 2019/12/2516:13
 */
public class VisitorUtils {
    public static void main(String[] args) {
        System.out.println();
    }

    public static int getVisitors(int visitor, String rule) {
        if (StringUtils.isEmpty(rule)) {
            return visitor;
        }
        String[] split = rule.split(",");
        for (String item : split) {
            String[] splitItem = item.split("-");
            if (visitor >= Integer.valueOf(splitItem[0]) && visitor <= Integer.valueOf(splitItem[1])) {
                visitor = visitor * Integer.valueOf(splitItem[2]) + Integer.valueOf(splitItem[3]);
                break;
            }
        }
        return visitor;
    }

    /**
     * 人气值反拿真实的观众数
     *
     * @param num
     * @param rule
     * @return
     */
    public static int getBackVisitors(int num, String rule) {
        if (StringUtils.isEmpty(rule)) {
            return num;
        }
        int visitor = num;
        String[] split = rule.split(",");
        for (String item : split) {
            String[] splitItem = item.split("-");
            visitor = (num - Integer.valueOf(splitItem[3])) / Integer.valueOf(splitItem[2]);
            if (visitor >= Integer.valueOf(splitItem[0]) && visitor <= Integer.valueOf(splitItem[1])) {
                break;
            }
        }
        return visitor;
    }

    /**
     * 获得回放时长
     *
     * @param url
     * @return
     */
    public static String getBackTime(String url) {
        if (!StringUtils.isEmpty(url)) {
            url = url + "?avinfo";
            String json = HttpUtils.sendGet(url);
            if (!StringUtils.isEmpty(json)) {
                JSONObject jsonObject = JSONObject.parseObject(json);
                JSONObject format = jsonObject.getJSONObject("format");
                if (!StringUtils.isEmpty(format)) {
                    String duration = format.getString("duration");
                    if (!StringUtils.isEmpty(duration)) {
                        BigDecimal bigDecimal = new BigDecimal(duration);
                        Integer integer = bigDecimal.intValue() * 1000;
                        return msecToTime(integer).substring(0,8);
                    }
                }

            }
        }
        return "";
    }
}
