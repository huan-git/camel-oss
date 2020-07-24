package org.apache.camel.component.oss;

import com.aliyun.oss.OSS;
import org.apache.camel.*;
import org.apache.camel.component.oss.client.OSSClient;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.support.ScheduledPollEndpoint;
import org.apache.camel.support.SynchronizationAdapter;
import org.apache.camel.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@UriEndpoint(firstVersion = "3.2.0", scheme = "oss", title = "AWS 2 S3 Storage Service", syntax = "oss://bucketNameOrArn", category = {Category.CLOUD, Category.FILE})
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
		this.configuration=configuration;
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
	
	public Exchange createExchange(File file,String key) {
		Exchange exchange = super.createExchange(getExchangePattern());

		Message message = exchange.getIn();
		message.setBody(null);
		message.setHeader(OSSConstants.KEY, key);
		message.setHeader(OSSConstants.BUCKET_NAME, getConfiguration().getBucketName());
		/*message.setHeader(OSSConstants.E_TAG, s3Object.response().eTag());
		message.setHeader(OSSConstants.LAST_MODIFIED, s3Object.response().lastModified());
		message.setHeader(OSSConstants.VERSION_ID, s3Object.response().versionId());
		message.setHeader(OSSConstants.CONTENT_TYPE, s3Object.response().contentType());
		message.setHeader(OSSConstants.CONTENT_LENGTH, s3Object.response().contentLength());
		message.setHeader(OSSConstants.CONTENT_ENCODING, s3Object.response().contentEncoding());
		message.setHeader(OSSConstants.CONTENT_DISPOSITION, s3Object.response().contentDisposition());
		message.setHeader(OSSConstants.CACHE_CONTROL, s3Object.response().cacheControl());
		message.setHeader(OSSConstants.SERVER_SIDE_ENCRYPTION, s3Object.response().serverSideEncryption());
		message.setHeader(OSSConstants.EXPIRATION_TIME, s3Object.response().expiration());
		message.setHeader(OSSConstants.REPLICATION_STATUS, s3Object.response().replicationStatus());
		message.setHeader(OSSConstants.STORAGE_CLASS, s3Object.response().storageClass());*/
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
}