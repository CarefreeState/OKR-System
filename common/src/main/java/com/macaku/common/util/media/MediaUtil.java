package com.macaku.common.util.media;

import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.media.config.StaticMapperConfig;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 马拉圈
 * Date: 2023-10-27
 * Time: 0:00
 */
@Slf4j
public class MediaUtil {

    private static final String SUFFIX = "png";

    // 获取UUID
    public static String getUUID_32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     *
     */
    public static String getUniqueFileName() {
        //拼接
        return String.format("%s.%s", getUUID_32(), SUFFIX);
    }

    public static String saveImage(byte[] imageData) {
        String savePath = StaticMapperConfig.ROOT + StaticMapperConfig.MAP_ROOT;
        String fileName = getUniqueFileName();
        String filePath = savePath + fileName;
        String mapPath = StaticMapperConfig.MAP_ROOT + fileName;
        File directory = new File(savePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(filePath);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }
        try ( OutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(imageData);
            outputStream.flush();
            log.info("图片保存成功 {}", savePath);
            return mapPath;
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    private static boolean isUrlAccessible(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            return responseCode == 200; // 如果状态码为 200，则返回 true，表示可以访问
        } catch (IOException e) {
            return false; // 发生异常时，返回 false，表示不可访问
        }
    }

    public static InputStream getFileInputStream(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    public static boolean isImage(String url) {
        if(!isUrlAccessible(url)) {
            return false;
        }
        try {
            InputStream inputStream = getFileInputStream(url);
            if (Objects.isNull(inputStream)) {
                return false;
            }
            Image img = ImageIO.read(inputStream);
            return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
        } catch (Exception e) {
            return false;
        }
    }

}
