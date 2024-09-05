package com.macaku.common.util.web;


import cn.hutool.http.HttpRequest;
import com.macaku.common.exception.GlobalServiceException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

public class HttpUtil {

    private final static String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    public static String getFormBody(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            return "";
        }
        try {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String keyVale = String.format("%s=%s", entry.getKey(), URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                builder.append(keyVale);
                builder.append("&");
            }
            if (StringUtils.hasLength(builder)) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static String getQueryString(Map<String, Object> map) {
        String formBody = getFormBody(map);
        if (StringUtils.hasLength(formBody)) {
            return "?" + formBody;
        } else {
            return "";
        }
    }

    public static String doGet(String httpUrl) {
        return doGet(httpUrl, null);
    }

    public static String doGet(String httpUrl, Map<String, Object> map) {
        // 有queryString的就加
        httpUrl += HttpUtil.getQueryString(map);
        return HttpRequest.get(httpUrl)
                .execute()
                .body();
    }

    public static String doPostFrom(String httpUrl, Map<String, Object> map) {
        return HttpRequest.post(httpUrl)
                .form(map)
                .execute()
                .body();
    }

    public static String doPostJsonString(String httpUrl, String json) {
        return HttpRequest.post(httpUrl)
                .body(json, JSON_CONTENT_TYPE)
                .execute()
                .body();

    }

    public static byte[] doPostJsonBytes(String httpUrl, String json) {
        return HttpRequest.post(httpUrl)
                .body(json, JSON_CONTENT_TYPE)
                .execute()
                .bodyBytes();
    }

    public static String doPostJsonBase64(String url, String json) {
        return Base64.encodeBase64String(doPostJsonBytes(url, json));
    }


    public static InputStream getFileInputStream(String fileUrl) throws IOException {
        return HttpRequest.get(fileUrl)
                .execute()
                .bodyStream();
    }

}

