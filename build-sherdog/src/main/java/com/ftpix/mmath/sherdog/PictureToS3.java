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
import mmath.S3Helper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PictureToS3 implements PictureProcessor {
    private final S3Helper s3Helper;
    private final static String DEFAULT_CHECKSUM = "90d653a88826d067a1fbfd064c26f85a";

    public PictureToS3(S3Helper s3Helper) {

        this.s3Helper = s3Helper;
    }


    @Override
    public String process(String url, Fighter fighter) throws IOException {

        try (InputStream input = new URL(url).openStream()) {
            String key = DigestUtils.md5Hex(fighter.getSherdogUrl())+".jpg";
            Path tempFile = Files.createTempFile(key, "").toAbsolutePath();
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);

            String checksum = DigestUtils.md5Hex(FileUtils.readFileToByteArray(tempFile.toFile()));
            if (!checksum.equalsIgnoreCase(DEFAULT_CHECKSUM)) {
                s3Helper.uploadFile(key, tempFile.toFile());
                return "/pictures/" + key;
            } else {
                return "/pictures/default.jpg";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "/pictures/default.jpg";
        }
    }
}