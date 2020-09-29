package com.leyou.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.config.UploadProperties;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UploadProperties uploadProperties;

    //支持的文件类型
    //private static final List<String> ALLOW_TYPES = Arrays.asList("image/png", "image/jpeg", "image/bmp");

    public String upload(MultipartFile file) {
        try {
            // 1.图片信息校验
            // 1.1 校验文件类型
            String contentType = file.getContentType();
            //if (!ALLOW_TYPES.contains(contentType)) {
            if (!uploadProperties.getAllowTypes().contains(contentType)) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            // 1.2 校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            // 2. 保存图片
            // 上传到FastDFS
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            // 2.1 生成保存目录
//            File dir = new File("F:\\heima\\leyou\\upload");
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            // 2.2 保存图片
//            file.transferTo(new File(dir, file.getOriginalFilename()));
            // 2.3 拼接图片地址
//            String url = "http://image.leyou.com/upload/" + storePath.getFullPath();
            String url = uploadProperties.getBaseUrl() + storePath.getFullPath();
            return url;
        } catch (Exception e) {
            log.error("上传文件失败!", e);
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
    }

}
