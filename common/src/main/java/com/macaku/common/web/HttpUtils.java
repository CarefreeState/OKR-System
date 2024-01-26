package com.macaku.common.web;


import com.macaku.common.exception.GlobalServiceException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class HttpUtils {

    public static String getBody(HttpServletRequest request) {
        ServletInputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        try {
            inputStream = request.getInputStream();
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(b)) != -1) {
                builder.append(new String(b, 0, len));
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
        return builder.toString();
    }

    public static String doGet(String httpUrl) {
        return doGet(httpUrl, null);
    }


    public static String doGet(String httpUrl, Map<String, Object> map) {
        // 有queryString的就加
        httpUrl += HttpUtils.getQueryString(map);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // 封装输入流，并指定字符集
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                // 存放数据
                StringBuilder sbf = new StringBuilder();
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append(System.getProperty("line.separator"));
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();// 关闭远程连接
            } catch (IOException e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }
        return result;
    }

    public static String getFormBody(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : entrySet) {
            builder.append(entry.toString());
            builder.append("&");
        }
        String formBody = builder.toString();
        if (StringUtils.hasLength(formBody)) {
            formBody = formBody.substring(0, formBody.length() - 1);
        }
        return formBody;
    }

    public static String getQueryString(Map<String, Object> map) {
        String formBody = getFormBody(map);
        if (StringUtils.hasLength(formBody)) {
            return "?" + formBody;
        } else {
            return "";
        }
    }

    public static String doPostFrom(String httpUrl, Map<String, Object> map) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            outputStream = connection.getOutputStream();
            outputStream.write(getFormBody(map).getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // 对输入流对象进行包装:charset根据工作项目组的要求来设置
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder sbf = new StringBuilder();
                String temp;
                // 循环遍历一行一行读取数据
                while ((temp = bufferedReader.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append(System.getProperty("line.separator"));
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        } finally {
            try {
                bufferedReader.close();
                outputStream.close();
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }
        return result;
    }

    public static String doPostJsonString(String httpUrl, String json) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes());
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // 对输入流对象进行包装:charset根据工作项目组的要求来设置
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder sbf = new StringBuilder();
                String temp;
                // 循环遍历一行一行读取数据
                while ((temp = bufferedReader.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append(System.getProperty("line.separator"));
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        } finally {
            try {
                bufferedReader.close();
                outputStream.close();
                inputStream.close();
                connection.disconnect();
            }catch (IOException e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }
        return result;
    }

    public static byte[] doPostJsonBytes(String url, String json) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(15000)   //连接服务区主机超时时间
                .setConnectionRequestTimeout(60000) //连接请求超时时间
                .setSocketTimeout(60000).build(); //设置读取响应数据超时时间
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(new StringEntity(json, "UTF-8"));
        //添加头信息
        httpPost.addHeader("Content-type",  "application/json; charset=utf-8");
        String result = "";
        try {
            //发送请求
            httpResponse = httpClient.execute(httpPost);
            //从相应对象中获取返回内容
            HttpEntity entity = httpResponse.getEntity();
            InputStream content = entity.getContent();
            return InputStreamToByte(content);
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static String doPostJsonBase64(String url, String json) {
        return Base64.encodeBase64String(doPostJsonBytes(url, json));
    }
    /**
     * 输入流转字节流
     * */
    private static byte[] InputStreamToByte(InputStream in) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        byte[] buffer=new byte[1024];
        int ch;
        while ((ch = in.read(buffer)) != -1) {
            bytestream.write(buffer,0,ch);
        }
        byte data[] = bytestream.toByteArray();
        bytestream.close();
        return data;
    }
}

