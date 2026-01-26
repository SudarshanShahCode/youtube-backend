package com.youtube.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URISyntaxException;
import java.time.Duration;

@Slf4j
@Component
public class S3Utils {

    @Value("${app.s3.bucket}")
    private String bucket;

    private final S3Presigner presigner;
    private final S3Client s3;

    public S3Utils(S3Presigner presigner, S3Client s3) {
        this.presigner = presigner;
        this.s3 = s3;
    }

    public String presignUploadUrl(String key, String contentType) throws URISyntaxException {
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putReq)
                .signatureDuration(Duration.ofMinutes(10))
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        return presigned.url().toURI().toString();
    }

    public String presignGetUrl(String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofDays(1))
                .build();

        return presigner.presignGetObject(presignRequest)
                        .url()
                        .toString();
    }

    public boolean objectExists(String key) {
        try {
            s3.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
            return true;
        } catch (NoSuchKeyException e) {
            log.error("Video object not found for the key: {}", key);
            return false;
        }
    }
}
