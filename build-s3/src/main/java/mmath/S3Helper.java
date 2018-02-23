package mmath;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.AwsEnvVarOverrideRegionProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.File;

public class S3Helper {
    private final String awsAccess, awsSecret, awsBucket, awsRegion, awsEndpoint;

    public S3Helper(String awsAccess, String awsSecret, String awsBucket, String awsRegion, String awsEndpoint) {
        this.awsAccess = awsAccess;
        this.awsSecret = awsSecret;
        this.awsBucket = awsBucket;
        this.awsRegion = awsRegion;
        this.awsEndpoint = awsEndpoint;
    }


    public AmazonS3 getAWSClient() {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccess, awsSecret);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride("AWSS3V4SignerType");

        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    /**
     * Uploads a single file to the bucket
     *
     * @param key  the key of the file
     * @param file the file to upload
     */
    public PutObjectResult uploadFile(String key, File file) {
        return getAWSClient().putObject(awsBucket, key, file);
    }

    /**
     * Gets a single file as a stream
     * @param key the key of the file
     * @return the stream of the file
     */
    public S3ObjectInputStream getFile(String key) {
        return getAWSClient().getObject(awsBucket, key).getObjectContent();
    }
}
