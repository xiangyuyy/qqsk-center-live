package com.jiebai.qqsk.live.utils;

import com.alibaba.nacos.client.config.utils.MD5;

/**
 * @author xiaoh
 * @description: 数字10位转16位
 * @date 2019/11/2817:47
 */
public class Base16 {
    public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;
        }
        a = s.reverse().toString().toLowerCase();
        return a;
    }

    /**
     * 获得七牛防盗sign
     * @param s
     * @return
     */
    public static String getQiNiuSign(String s) {
       return MD5.getInstance().getMD5String(s.toLowerCase()).toLowerCase();
    }
}
