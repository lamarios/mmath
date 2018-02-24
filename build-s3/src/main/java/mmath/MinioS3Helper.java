package mmath;

import com.amazonaws.services.s3.model.PutObjectResult;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

import java.io.File;
import java.io.InputStream;

public class MinioS3Helper extends S3Helper {
    public MinioS3Helper(String awsAccess, String awsSecret, String awsBucket, String awsRegion, String awsEndpoint) {
        super(awsAccess, awsSecret, awsBucket, awsRegion, awsEndpoint);
    }

    private MinioClient getClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(awsEndpoint, awsAccess, awsSecret);
    }

    @Override
    protected void createBucketIfNeeded(String bucket) throws Exception {
        MinioClient client = getClient();

        if (!client.bucketExists(bucket)) {
            client.makeBucket(bucket);
        }
    }

    @Override
    public PutObjectResult uploadFile(String key, File file) throws Exception {
        MinioClient client = getClient();
        client.putObject(awsBucket, key, file.getAbsolutePath());
        return null;
    }

    @Override
    public InputStream getFile(String key) throws Exception {
        MinioClient client = getClient();
        return client.getObject(awsBucket, key);
    }
}
