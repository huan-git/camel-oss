package org.apache.camel.component.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.camel.*;
import org.apache.camel.component.oss.client.impl.OSSClientImpl;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.support.ScheduledPollEndpoint;
import org.apache.camel.support.SynchronizationAdapter;
import org.apache.camel.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

@UriEndpoint(firstVersion = "3.2.0", scheme = "oss", title = "OSS Storage Service", syntax = "oss://bucketName", category = {Category.CLOUD, Category.FILE})
public class OSSEndpoint extends ScheduledPollEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(OSSEndpoint.class);

    private OSS ossClient;
    @UriParam
    private final OSSConfiguration configuration;
    @UriParam(label = "consumer", defaultValue = "10")
    private int maxMessagesPerPoll = 10;
    @UriParam(label = "consumer", defaultValue = "60")
    private int maxConnections = 50 + maxMessagesPerPoll;

    public OSSEndpoint(OSSComponent component, String uri, OSSConfiguration configuration) {
        super(uri, component);
        this.configuration = configuration;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OSSProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new OSSConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public Exchange createExchange(OSSObject obj) {
        return createExchange(getExchangePattern(), obj);
    }

    public Exchange createExchange(ExchangePattern pattern, OSSObject obj) {
        LOG.info("Getting object with key [{}] from bucket [{}]...", obj.getKey(), obj.getBucketName());

        ObjectMetadata objectMetadata = obj.getObjectMetadata();

        LOG.info("Got object [{}]", obj);

        Exchange exchange = super.createExchange(pattern);
        Message message = exchange.getIn();

        if (configuration.isIncludeBody()) {
            message.setBody(obj.getObjectContent());
        } else {
            message.setBody(null);
        }
        message.setHeader(Exchange.FILE_NAME, obj.getKey().substring(obj.getKey().lastIndexOf("/") + 1));

		message.setHeader(OSSConstants.KEY, obj.getKey());
		message.setHeader(OSSConstants.BUCKET_NAME, obj.getBucketName());
		message.setHeader(OSSConstants.E_TAG, objectMetadata.getETag());
		message.setHeader(OSSConstants.LAST_MODIFIED, objectMetadata.getLastModified());
		message.setHeader(OSSConstants.VERSION_ID, objectMetadata.getVersionId());
		message.setHeader(OSSConstants.CONTENT_TYPE, objectMetadata.getContentType());
		message.setHeader(OSSConstants.CONTENT_MD5, objectMetadata.getContentMD5());
		message.setHeader(OSSConstants.CONTENT_LENGTH, objectMetadata.getContentLength());
		message.setHeader(OSSConstants.CONTENT_ENCODING, objectMetadata.getContentEncoding());
		message.setHeader(OSSConstants.CONTENT_DISPOSITION, objectMetadata.getContentDisposition());
		message.setHeader(OSSConstants.CACHE_CONTROL, objectMetadata.getCacheControl());
		message.setHeader(OSSConstants.OSS_HEADERS, objectMetadata.getRawMetadata());
		message.setHeader(OSSConstants.SERVER_SIDE_ENCRYPTION, objectMetadata.getServerSideEncryption());
		message.setHeader(OSSConstants.USER_METADATA, objectMetadata.getUserMetadata());
        try {
            message.setHeader(OSSConstants.EXPIRATION_TIME, objectMetadata.getExpirationTime());
        } catch (ParseException e) {
          LOG.error(" setting EXPIRATION_TIME is failed");
        }
        message.setHeader(OSSConstants.STORAGE_CLASS, objectMetadata.getObjectStorageClass());

        /**
         * If includeBody != true, it is safe to close the object here. If
         * includeBody == true, the caller is responsible for closing the stream
         * and object once the body has been fully consumed. As of 2.17, the
         * consumer does not close the stream or object on commit.
         */
        if (!configuration.isIncludeBody()) {
            IOHelper.close(obj);
        } else {
            if (configuration.isAutocloseBody()) {
                exchange.adapt(ExtendedExchange.class).addOnCompletion(new SynchronizationAdapter() {
                    @Override
                    public void onDone(Exchange exchange) {
                        IOHelper.close(obj);
                    }
                });
            }
        }

        return exchange;
    }

    public OSSConfiguration getConfiguration() {
        return configuration;
    }

    public int getMaxMessagesPerPoll() {
        return maxMessagesPerPoll;
    }

    public void setMaxMessagesPerPoll(int maxMessagesPerPoll) {
        this.maxMessagesPerPoll = maxMessagesPerPoll;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public OSS getOssClient() {
        return ossClient;
    }

    public void setOssClient(OSS ossClient) {
        this.ossClient = ossClient;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        ossClient = new OSSClientImpl(configuration).getOSSClient();
        String fileName = getConfiguration().getFileName();

        if (fileName != null) {
            LOG.info("File name [{}] requested, so skipping bucket check...", fileName);
            return;
        }

        String bucketName = getConfiguration().getBucketName();
		LOG.info("Querying whether bucket [{}] already exists...", bucketName);

        String prefix = getConfiguration().getPrefix();

        try {
            ossClient.listObjects(new ListObjectsRequest(bucketName, prefix, null, null, maxMessagesPerPoll));
            LOG.info("Bucket [{}] already exists", bucketName);
            return;
        } catch (ServiceException ase) {
            /* NO_SUCH_BUCKET means the bucket doesn't exist */
            LOG.error("Bucket [{}] doesn't exist yet", bucketName);
            if (OSSErrorCode.NO_SUCH_BUCKET.equals(ase.getErrorCode())) {
                if (getConfiguration().isAutoCreateBucket()) {
                    // creates the new bucket because it doesn't exist yet
                    CreateBucketRequest createBucketRequest = new CreateBucketRequest(getConfiguration().getBucketName());

                    LOG.info("Creating bucket [{}] in locationConstraint [{}] with request [{}]...", configuration.getBucketName(), configuration.getLocationConstraint(), createBucketRequest);

                    ossClient.createBucket(createBucketRequest);

                    LOG.info("Bucket created");
                }
            } else {
                throw ase;
            }
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (ossClient != null) {
            ossClient.shutdown();
        }
        LOG.info("is stoped.....");
    }
}