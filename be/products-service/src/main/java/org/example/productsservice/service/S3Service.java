package org.example.productsservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {
    private final S3Client s3Client;
    
    @Value("${aws.bucket-name:kltn-nlu}")
    private String bucketName;
    
    @Value("${aws.region:ap-southeast-1}")
    private String region;
    
    @Value("${aws.access-key}")
    private String accessKey;
    
    @Value("${aws.secret-key}")
    private String secretKey;

    public S3Service() {
        // S3Client sẽ được khởi tạo sau khi các @Value được inject
        this.s3Client = null;
    }
    
    @PostConstruct
    public void initS3Client() {
        if (s3Client == null) {
            this.s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)
                    ))
                    .build();
        }
    }

    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
            try {
                // Tạo file tạm để upload
                java.nio.file.Path tempFile = Files.createTempFile("upload", file.getOriginalFilename());
                file.transferTo(tempFile);
                s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                        tempFile
                );
                Files.deleteIfExists(tempFile);
                String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
                urls.add(url);
            } catch (S3Exception e) {
                throw new IOException("Failed to upload file to S3: " + e.awsErrorDetails().errorMessage(), e);
            }
        }
        return urls;
    }
} 