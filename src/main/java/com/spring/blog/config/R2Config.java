package com.spring.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class R2Config {

    @Bean
    public S3Presigner s3Presigner(
            @Value("${r2.accessKeyId}") String accessKeyId,
            @Value("${r2.secretAccessKey}") String secretAccessKey,
            @Value("${r2.endpoint}") String endpoint,
            @Value("${r2.region}") String region    ) {

        var creds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .build();
    }
}
