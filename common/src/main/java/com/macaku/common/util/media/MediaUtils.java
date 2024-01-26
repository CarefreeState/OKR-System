package com.macaku.common.util.media;

import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.media.config.StaticMapperConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 马拉圈
 * Date: 2023-10-27
 * Time: 0:00
 */
@Slf4j
public class MediaUtils {

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
        return String.format("%s.%s",getUUID_32(), SUFFIX);
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

}
