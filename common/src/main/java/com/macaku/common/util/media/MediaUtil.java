package com.macaku.common.util.media;

import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.common.web.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

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

    public static String getUniqueImageName() {
        //拼接
        return String.format("%s.%s", getUUID_32(), SUFFIX);
    }

    public static String getFilePath(String mapPath) {
        return StaticMapperConfig.ROOT + mapPath;
    }

    public static void tryCreateFile(String savePath, String filePath) {
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
        String fileName = getUniqueImageName();
        String filePath = savePath + fileName;
        String mapPath = StaticMapperConfig.MAP_ROOT + fileName;
        saveFile(savePath, filePath, imageData);
        log.info("图片保存成功 {}", filePath);
        return mapPath;
    }

    public static String saveImage(byte[] imageData, String extraPath) {
        if(!StringUtils.hasText(extraPath)) {
            return saveImage(imageData);
        }
        String mapBasePath = StaticMapperConfig.MAP_ROOT + extraPath;
        String savePath = StaticMapperConfig.ROOT + mapBasePath;
        String fileName = getUniqueImageName();
        String filePath = savePath + fileName;
        String mapPath = mapBasePath + fileName;
        saveFile(savePath, filePath, imageData);
        log.info("图片保存成功 {}", filePath);
        return mapPath;
    }

    public static void saveFile(String savePath, String filePath, String url) {
        MediaUtil.tryCreateFile(savePath, filePath);
        try(InputStream inputStream = HttpUtil.getFileInputStream(url);
            OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            byte[] data  = MediaUtil.inputStreamToByte(inputStream);
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static void saveFile(String savePath, String filePath, byte[] data) {
        MediaUtil.tryCreateFile(savePath, filePath);
        try(OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            log.warn("删除文件 {}", path);
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

    public static boolean isImage(String url) {
        try (InputStream inputStream = HttpUtil.getFileInputStream(url)) {
            return isImage(inputStream);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isImage(byte[] bytes) {
        if (Objects.isNull(bytes)) {
            return false;
        }
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return isImage(inputStream);
        } catch (Exception e) {
            return false;
        }
    }

}
