package com.ftpix.mmath.sherdog;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.ftpix.sherdogparser.PictureProcessor;
import com.ftpix.sherdogparser.models.Fighter;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PictureToS3 implements PictureProcessor {
    private final String awsAccess, awsSecret, awsBucket, awsRegion, awsEndpoint;
    private final AmazonS3 awsClient;

    public PictureToS3(String awsAccess, String awsSecret, String awsBucket, String awsRegion, String awsEndpoint) {
        this.awsAccess = awsAccess;
        this.awsSecret = awsSecret;
        this.awsBucket = awsBucket;
        this.awsRegion = awsRegion;
        this.awsEndpoint = awsEndpoint;

        AWSCredentials credentials = new BasicAWSCredentials(awsAccess, awsSecret);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride("AWSS3V4SignerType");


        awsClient = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }


    @Override
    public String process(String url, Fighter fighter) throws IOException {

        try (InputStream input = new URL(url).openStream()) {
            String key = DigestUtils.md5Hex(fighter.getSherdogUrl());
            Path tempFile = Files.createTempFile(key, "").toAbsolutePath();
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);

            awsClient.putObject(awsBucket, key, tempFile.toFile());
            return key;
        }
    }
}
