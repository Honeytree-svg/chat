package com.sz.song;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class InterfaceRequest {

    public static void main(String[] args) {

        //拼接多参数
//        Map params = new HashMap();
//        params.put("s", "唯一");
//        params.put("type", "1");
//        params.put("offset", "0");
//        params.put("totoal", "true");
//        params.put("limit", "20");

        String url="";

        //url = "http://music.163.com/api/search/get/web?csrf_token=hlpretag=&hlposttag=&"+urlencode(params);
        HttpClient client = HttpClients.createDefault();
        //默认post请求
        HttpPost post = new HttpPost(url);

//        try {
//            post.addHeader("Content-type", "application/json; charset=utf-8");
//            post.setHeader("Accept", "application/json");
//            HttpResponse httpResponse = client.execute(post);
//
//            HttpEntity entity = httpResponse.getEntity();
//            System.err.println("状态:" + httpResponse.getStatusLine());
//            //System.err.println("参数:" + EntityUtils.toString(entity));
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        url = "https://api.imjad.cn/cloudmusic/?type=song&id=33497051";
        post.setURI(URI.create(url));

        try {
            post.addHeader("Content-type", "application/json; charset=utf-8");
            post.setHeader("Accept", "application/json");
            System.err.println("状态:dsadsadasda");
            HttpResponse httpResponse = client.execute(post);
            System.err.println("aaaaaaaaaaa");

            HttpEntity entity = httpResponse.getEntity();
            System.err.println("状态:" + httpResponse.getStatusLine());
            System.err.println("参数:" + EntityUtils.toString(entity));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public static String urlencode(Map<String,Object> data) {
        //将map里的参数变成像 showapi_appid=###&showapi_sign=###&的样子
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            sb.append(i.getKey()).append("=").append(i.getValue()).append("&");
        }
        return sb.toString();
    }
}
