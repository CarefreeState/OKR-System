package com.macaku.common.util.media;

import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.common.web.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public static String getFilePath(String mapPath) {
        return StaticMapperConfig.ROOT + mapPath;
    }

    /**
     * 输入流转字节流
     */
    public static byte[] inputStreamToByte(InputStream in) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int ch;
        while ((ch = in.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, ch);
        }
        byte data[] = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return data;
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
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            outputStream.write(imageData);
            outputStream.flush();
            log.info("图片保存成功 {}", filePath);
            return mapPath;
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static void deleteFile(String mapPath) {
        String filePath = getFilePath(mapPath);
        File file = new File(filePath);
        if (!file.exists()) {
            file.delete();
        }
    }

    public static boolean isImage(String url) {
        try (InputStream inputStream = HttpUtil.getFileInputStream(url)) {
            if (Objects.isNull(inputStream)) {
                return false;
            }
            Image img = ImageIO.read(inputStream);
            return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isImage(InputStream inputStream) {
        try {
            if (Objects.isNull(inputStream)) {
                return false;
            }
            Image img = ImageIO.read(inputStream);
            return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isImage(byte[] bytes) {
        if (Objects.isNull(bytes)) {
            return false;
        }
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            Image img = ImageIO.read(inputStream);
            return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
        } catch (Exception e) {
            return false;
        }
    }

}
