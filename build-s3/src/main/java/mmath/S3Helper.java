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
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

import java.io.File;
import java.io.InputStream;

public abstract class S3Helper {
     protected final String awsAccess, awsSecret, awsBucket, awsRegion, awsEndpoint;

    public S3Helper(String awsAccess, String awsSecret, String awsBucket, String awsRegion, String awsEndpoint) {
        this.awsAccess = awsAccess;
        this.awsSecret = awsSecret;
        this.awsBucket = awsBucket;
        this.awsRegion = awsRegion;
        this.awsEndpoint = awsEndpoint;


        try {
            createBucketIfNeeded(this.awsBucket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void createBucketIfNeeded(String bucket) throws  Exception;



    /**
     * Uploads a single file to the bucket
     *
     * @param key  the key of the file
     * @param file the file to upload
     */
    public abstract PutObjectResult uploadFile(String key, File file) throws Exception;

    /**
     * Gets a single file as a stream
     *
     * @param key the key of the file
     * @return the stream of the file
     */
    public abstract  InputStream getFile(String key) throws Exception;
}
