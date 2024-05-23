package com.sky.utils;

import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @description： minio工具类
 * @version：3.0
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinioUtil {
    @Autowired
    private MinioClient minioClient;

    @Value("sky")
    private String bucketName;
    /**
     * 上传单个文件到MinIO
     *
     * @param file MultipartFile 对象
     * @return 文件的访问路径
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFileName = generateUniqueFileName(fileExtension);
        String filePath = "http://localhost:9090/" + bucketName + "/" + newFileName;

        try (InputStream in = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFileName)
                    .stream(in, in.available(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取文件扩展名
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    // 生成唯一的文件名
    private String generateUniqueFileName(String fileExtension) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid + "." + fileExtension;
    }

    /**
     * 批量删除文件对象
     *
     * @param bucketName 存储bucket名称
     * @param objects    对象名称集合
     */
    public Iterable<Result<DeleteError>> removeObjects(String bucketName, List<String> objects) {
        List<DeleteObject> dos = objects.stream().map(e -> new DeleteObject(e)).collect(Collectors.toList());
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
        return results;
    }


}

