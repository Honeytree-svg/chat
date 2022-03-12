package com.sz.song;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class InterTest {
    public static void main(String[] args) {
        CloseableHttpClient httpClient = SslUtil.SslHttpClientBuild();
        String  url = "http://music.163.com/api/search/get/web?csrf_token=hlpretag=&hlposttag=&s=yoasobi&type=1&offset=0&total=true&limit=20";
        HttpPost post = new HttpPost(url);

        try {
            post.addHeader("Content-type", "application/json; charset=utf-8");
            post.setHeader("Accept", "application/json");
            HttpResponse httpResponse = httpClient.execute(post);

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
}
