package br.com.music.api.Services;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ImageService {

    private final MinioStorageService minioStorageService;
    private final MinioClient minioClient;
    private final String bucket;

    public ImageService(MinioStorageService minioStorageService, 
                       MinioClient minioClient,
                       @Value("${minio.bucket.name}") String bucket) {
        this.minioStorageService = minioStorageService;
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    public String upload(MultipartFile file) {
        return minioStorageService.uploadImage(file);
    }

    public String getBase64(String id) {
        try {
            return minioStorageService.getImage(id);
        } catch (Exception e) {
            return null;
        }
    }

    public ImageData download(String id) throws Exception {
        var stat = minioClient.statObject(
                StatObjectArgs.builder().bucket(bucket).object(id).build()
        );

        try (InputStream in = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(id).build());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            in.transferTo(baos);
            String contentType = stat.contentType();
            return new ImageData(baos.toByteArray(), contentType == null ? "application/octet-stream" : contentType);
        }
    }

    public record ImageData(byte[] data, String contentType) {}
}
