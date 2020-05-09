package com.jiebai.qqsk.live.utils;

import com.google.common.collect.Maps;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.annotation.concurrent.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

/**
 * 静态版本的http请求工具，使用场景为小规模，偶尔用用。 大面积连接请用连接池版本的，或是netty版本的。
 */
@Slf4j
@ThreadSafe
public class HttpUtils {
    private static final CloseableHttpClient httpclient = HttpClients.createDefault();
    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";

    /**
     * @param @param reqUrl
     * @param @param params
     * @param @throws Exception
     * @Description: http get 请求共用方法
     * @author clive
     */
    public static String sendGet(String reqUrl, Map<String, String> params, String type) {
        String result = "";
        try {
            String target = "";
            String pars = buildUrl(params);
            if (StringUtils.isEmpty(pars)) {
                target = reqUrl;
            } else {
                target = reqUrl + "?" + buildUrl(params);
            }
            log.info("请求：" + target);
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(5 * 1000);// 设置连接主机超时时间
            urlConn.setReadTimeout(5 * 1000);  //设置从主机读取数据超时
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Cache-Control", "no-cache");
            urlConn.setRequestProperty("Content-Type", type);
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            urlConn.setRequestProperty("Accept-Encoding", "gzip");
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
//        result = streamToString(urlConn.getInputStream());
                result = getStringFromGZIP(urlConn.getInputStream());
                log.info("Get方式请求成功，result--->" + result);
            } else {
                log.info("Get方式请求失败");
            }
            // 关闭连接
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /***
     * 模拟post请求
     * @param baseUrl
     * @param paramsMap
     * @param type
     * @return
     */
    public static String sendPost(String baseUrl, Map<String, String> paramsMap, String type) {
        String result = "";
        try {
            String params = buildUrl(paramsMap);
            System.out.println(params);
            // 请求的参数转换为byte数组
            byte[] postData = params.getBytes();
            URL url = new URL(baseUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(5 * 1000);// 设置连接超时时间
            urlConn.setReadTimeout(5 * 1000); //设置从主机读取数据超时
            urlConn.setDoOutput(true);// Post请求必须设置允许输出 默认false
            urlConn.setDoInput(true);//设置请求允许输入 默认是true
            urlConn.setUseCaches(false); // Post请求不能使用缓存
            urlConn.setRequestMethod("POST");      // 设置为Post请求
            urlConn.setInstanceFollowRedirects(true); //设置本次连接是否自动处理重定向
            urlConn.setRequestProperty("Content-Type", type);
            // 开始连接
            urlConn.connect();
            // 发送请求参数
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.write(postData);
            dos.flush();
            dos.close();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                result = streamToString(urlConn.getInputStream());
                log.info("Post方式请求成功，result--->" + result);
            } else {
                log.info("Post方式请求失败");
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            log.info(e.toString());
        }
        return result;
    }

    //减少参数的优化方法
    public static String sendGet(String reqUrl) {
        return sendGet(reqUrl, Maps.newHashMap(), MimeTypeUtils.TEXT_HTML_VALUE);
    }

    public static String sendGet(String reqUrl, String type) {
        return sendGet(reqUrl, Maps.newHashMap(), type);
    }

    public static String sendGet(String reqUrl, Map<String, String> params) {
        return sendGet(reqUrl, params, MimeTypeUtils.TEXT_HTML_VALUE);
    }

    public static String sendPost(String baseUrl, Map<String, String> paramsMap) {
        return sendPost(baseUrl, paramsMap, MimeTypeUtils.TEXT_HTML_VALUE);
    }


    public static String buildUrl(Map<String, String> params) throws Exception {
        StringBuilder query = new StringBuilder();
        Set<String> set = params.keySet();
        for (String key : set) {
            query.append(String.format("%s=%s&", key, URLEncoder.encode(params.get(key), "utf-8")));
        }
        return query.toString();
    }

    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            log.info(e.toString());
            return null;
        }
    }

    private static String getStringFromGZIP(InputStream is) {
        String jsonString = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(2);
            // 取前两个字节
            byte[] header = new byte[2];
            int result = bis.read(header);
            // reset 输入流到开始位置
            bis.reset();
            // 判断是否是 GZIP 格式
            int headerData = getShort(header);
            // Gzip 流 的前两个字节是 0x1f8b
            if (result != -1 && headerData == 0x1f8b) {
                // LogUtil.i("HttpTask", " use GZIPInputStream  ");
                is = new GZIPInputStream(bis);
            } else {
                // LogUtil.d("HttpTask", " not use GZIPInputStream");
                is = bis;
            }
            InputStreamReader reader = new InputStreamReader(is, "utf-8");
            char[] data = new char[100];
            int readSize;
            StringBuffer sb = new StringBuffer();
            while ((readSize = reader.read(data)) > 0) {
                sb.append(data, 0, readSize);
            }
            jsonString = sb.toString();
            bis.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    private static int getShort(byte[] data) {
        return (data[0] << 8) | data[1] & 0xFF;
    }

    public static String Post(String url, Map<String, String> map) {
        // 设置参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        // 编码
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        // 取得HttpPost对象
        HttpPost httpPost = new HttpPost(url);
        // 防止被当成攻击添加的
        httpPost.setHeader("User-Agent", userAgent);
        // 参数放入Entity
        httpPost.setEntity(formEntity);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            // 执行post请求
            response = httpclient.execute(httpPost);
            // 得到entity
            HttpEntity entity = response.getEntity();
            // 得到字符串
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    public static String post(String url, String message)  {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        StringEntity entity = new StringEntity(message, "UTF-8");
        entity.setContentType("application/json;charset=UTF-8");
        httppost.setEntity(entity);
        httppost.addHeader("Accept", "application/json");
        httppost.addHeader("ContentType", "application/json;charset=UTF-8");
        CloseableHttpResponse response =null;
        String retMsg =null;
        try {
            response = httpclient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (HttpURLConnection.HTTP_OK == code) {
                retMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.info("返回消息：{}", retMsg);

                return retMsg;
            }
        } catch (Exception e) {
            // TODO 请针对不同异常做相应的处理
            log.error(e.getMessage(), e);
        } finally {
            try {
                response.close();
            }catch (Exception e){
                log.error(e.getMessage(), e);
                return retMsg;
            }

        }
        return null;
    }

    /**
     *
     * @param url
     * @param param
     * @param header
     * @return
     */
    public static String PostWithHeader (String url, Map<String, String> param, Map<String, String> header) {
        // 设置参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        // 编码
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        // 取得HttpPost对象
        HttpPost httpPost = new HttpPost(url);
        // 防止被当成攻击添加的
        httpPost.setHeader("User-Agent", userAgent);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        // 参数放入Entity
        httpPost.setEntity(formEntity);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            // 执行post请求
            response = httpclient.execute(httpPost);
            // 得到entity
            HttpEntity entity = response.getEntity();
            // 得到字符串
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * @param @param reqUrl
     * @param @param params
     * @param @throws Exception
     * @Description: http get 请求共用方法
     * @author clive
     */
    public static String getWithHeader (String reqUrl, Map<String, String> params, Map<String, String> header) {
        String result = "";
        try {
            String target = "";
            String pars = buildUrl(params);
            if (StringUtils.isEmpty(pars)) {
                target = reqUrl;
            } else {
                target = reqUrl + "?" + buildUrl(params);
            }
            log.info("请求：" + target);
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(5 * 1000);// 设置连接主机超时时间
            urlConn.setReadTimeout(5 * 1000);  //设置从主机读取数据超时
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Cache-Control", "no-cache");
            urlConn.setRequestProperty("Content-Type", "MimeTypeUtils.TEXT_HTML_VALUE");
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            urlConn.setRequestProperty("Accept-Encoding", "gzip");
            for (Map.Entry<String, String> entry : header.entrySet()) {
                urlConn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
//        result = streamToString(urlConn.getInputStream());
                result = getStringFromGZIP(urlConn.getInputStream());
                log.info("Get方式请求成功，result--->" + result);
            } else {
                log.info("Get方式请求失败");
            }
            // 关闭连接
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
