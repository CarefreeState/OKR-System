package com.macaku.common.util.media;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.common.web.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 马拉圈
 * Date: 2023-10-27
 * Time: 0:00
 */
@Slf4j
public class MediaUtil {

    public static final String SUFFIX = "png";

    public static final String UTF_8 = "UTF-8";

    // 获取UUID
    public static String getUUID_32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getUniqueImageName() {
        //拼接
        return String.format("%s.%s", getUUID_32(), SUFFIX);
    }

    public static String getLocalFilePath(String mapPath) {
        return StaticMapperConfig.ROOT + mapPath;
    }

    public static String getLocalFileName(String mapPath) {
        return mapPath.substring(mapPath.lastIndexOf("/") + 1);
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

    public static byte[] getCustomColorQRCodeByteArray(String url, int width, int height) throws WriterException, IOException {
        // 配置生成二维码的参数
        Map<EncodeHintType, String> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.CHARACTER_SET, UTF_8);
        // 生成二维码矩阵
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height, hintMap);
        // 创建二维码图片
        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        qrImage.createGraphics();
        // 将二维码矩阵渲染到图片上
        Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        graphics.dispose();
        // 将二维码图片转换为输入流
        // 将BufferedImage转换为字节数组
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(qrImage, SUFFIX, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static InputStream getCustomColorQRCodeInputStream(String url, int width, int height) throws WriterException, IOException {
        return new ByteArrayInputStream(getCustomColorQRCodeByteArray(url, width, height));
    }

}
