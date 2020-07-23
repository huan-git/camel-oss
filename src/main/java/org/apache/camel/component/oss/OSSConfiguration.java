package org.apache.camel.component.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.comm.Protocol;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.apache.camel.util.ObjectHelper;

/**
 * @program: camel-oss
 * @description: this is the description of the OSSConfiguration class
 * @author: ahuan
 * @version: 2020-07-23 09:54
 **/

@UriParams
public class OSSConfiguration {

    private String bucketName;
    @UriParam
    private OSS client;
    @UriParam(label = "security", secret = true)
    private String accessKey;
    @UriParam(label = "security", secret = true)
    private String secretKey;

    @UriParam(label = "consumer")
    private String fileName;
    @UriParam(label = "consumer")
    private String prefix;
    @UriParam(label = "consumer")
    private String delimiter;
    @UriParam
    private String region;
    @UriParam(label = "consumer", defaultValue = "true")
    private boolean deleteAfterRead = true;
    @UriParam(label = "producer")
    private boolean deleteAfterWrite;
    @UriParam(label = "producer")
    private boolean multiPartUpload;
    @UriParam(label = "producer", defaultValue = "" + 25 * 1024 * 1024)
    private long partSize = 25 * 1024 * 1024;
    @UriParam
    private String policy;
    @UriParam(label = "producer")
    private String storageClass;
    @UriParam(label = "producer")
    private String serverSideEncryption;

    @UriParam(enums = "HTTP,HTTPS", defaultValue = "HTTPS")
    private Protocol proxyProtocol = Protocol.HTTPS;
    @UriParam
    private String proxyHost;
    @UriParam
    private Integer proxyPort;
    @UriParam(label = "consumer", defaultValue = "true")
    private boolean includeBody = true;
    @UriParam
    private boolean pathStyleAccess;
    @UriParam(label = "producer", enums = "copyObject,deleteBucket,listBuckets,downloadLink")
    private OSSOperations operation;
    @UriParam(label = "consumer,advanced", defaultValue = "true")
    private boolean autocloseBody = true;
    @UriParam(label = "common,advanced", defaultValue = "false")
    private boolean useEncryption;
    @UriParam(label = "common, advanced", defaultValue = "false")
    private boolean chunkedEncodingDisabled;
    @UriParam(label = "common, advanced", defaultValue = "false")
    private boolean accelerateModeEnabled;
    @UriParam(label = "common, advanced", defaultValue = "false")
    private boolean dualstackEnabled;
    @UriParam(label = "common, advanced", defaultValue = "false")
    private boolean payloadSigningEnabled;
    @UriParam(label = "common, advanced", defaultValue = "false")
    private boolean forceGlobalBucketAccessEnabled;
    @UriParam(label = "common", defaultValue = "true")
    private boolean autoCreateBucket = true;
    @UriParam(label = "producer,advanced", defaultValue = "false")
    private boolean useAwsKMS;
    @UriParam(label = "producer,advanced")
    private String awsKMSKeyId;
    @UriParam(defaultValue = "false")
    private boolean useIAMCredentials;
    @UriParam(label = "producer")
    private String keyName;

    public long getPartSize() {
        return partSize;
    }

    /**
     * Setup the partSize which is used in multi part upload, the default size
     * is 25M.
     */
    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    public boolean isMultiPartUpload() {
        return multiPartUpload;
    }

    /**
     * If it is true, camel will upload the file with multi part format, the
     * part size is decided by the option of `partSize`
     */
    public void setMultiPartUpload(boolean multiPartUpload) {
        this.multiPartUpload = multiPartUpload;
    }

    public String getAccessKey() {
        return accessKey;
    }

    /**
     * Amazon AWS Access Key
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Amazon AWS Secret Key
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }


    public OSS getOSSClient() {
        return client;
    }

    /**
     * Reference to a `com.amazonaws.services.s3.AmazonS3` in the registry.
     */
    public void setOSSClient(OSS client) {
        this.client = client;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * The prefix which is used in the
     * com.amazonaws.services.s3.model.ListObjectsRequest to only consume
     * objects we are interested in.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDelimiter() {
        return delimiter;
    }

    /**
     * The delimiter which is used in the
     * com.amazonaws.services.s3.model.ListObjectsRequest to only consume
     * objects we are interested in.
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getBucketName() {
        return bucketName;
    }

    /**
     * Name of the bucket. The bucket will be created if it doesn't already
     * exists.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * To get the object from the bucket with the given file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRegion() {
        return region;
    }

    /**
     * The region in which S3 client needs to work. When using this parameter,
     * the configuration will expect the capitalized name of the region (for
     * example AP_EAST_1) You'll need to use the name Regions.EU_WEST_1.name()
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * If it is true, the exchange body will be set to a stream to the contents
     * of the file. If false, the headers will be set with the S3 object
     * metadata, but the body will be null. This option is strongly related to
     * autocloseBody option. In case of setting includeBody to true and
     * autocloseBody to false, it will be up to the caller to close the S3Object
     * stream. Setting autocloseBody to true, will close the S3Object stream
     * automatically.
     */
    public void setIncludeBody(boolean includeBody) {
        this.includeBody = includeBody;
    }

    public boolean isIncludeBody() {
        return includeBody;
    }

    public boolean isDeleteAfterRead() {
        return deleteAfterRead;
    }


    public void setDeleteAfterRead(boolean deleteAfterRead) {
        this.deleteAfterRead = deleteAfterRead;
    }

    public boolean isDeleteAfterWrite() {
        return deleteAfterWrite;
    }


    public void setDeleteAfterWrite(boolean deleteAfterWrite) {
        this.deleteAfterWrite = deleteAfterWrite;
    }

    public String getPolicy() {
        return policy;
    }


    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public String getServerSideEncryption() {
        return serverSideEncryption;
    }


    public void setServerSideEncryption(String serverSideEncryption) {
        this.serverSideEncryption = serverSideEncryption;
    }

    public Protocol getProxyProtocol() {
        return proxyProtocol;
    }

    public void setProxyProtocol(Protocol proxyProtocol) {
        this.proxyProtocol = proxyProtocol;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setPathStyleAccess(final boolean pathStyleAccess) {
        this.pathStyleAccess = pathStyleAccess;
    }

    public boolean isPathStyleAccess() {
        return pathStyleAccess;
    }

    public OSSOperations getOperation() {
        return operation;
    }

    /**
     * The operation to do in case the user don't want to do only an upload
     */
    public void setOperation(OSSOperations operation) {
        this.operation = operation;
    }

    public boolean isAutocloseBody() {
        return autocloseBody;
    }

    public void setAutocloseBody(boolean autocloseBody) {
        this.autocloseBody = autocloseBody;
    }


    public boolean isUseEncryption() {
        return useEncryption;
    }

    public void setUseEncryption(boolean useEncryption) {
        this.useEncryption = useEncryption;
    }

    public boolean isUseAwsKMS() {
        return useAwsKMS;
    }

    public void setUseAwsKMS(boolean useAwsKMS) {
        this.useAwsKMS = useAwsKMS;
    }

    public String getAwsKMSKeyId() {
        return awsKMSKeyId;
    }

    public void setAwsKMSKeyId(String awsKMSKeyId) {
        this.awsKMSKeyId = awsKMSKeyId;
    }

    public boolean isChunkedEncodingDisabled() {
        return chunkedEncodingDisabled;
    }

    public void setChunkedEncodingDisabled(boolean chunkedEncodingDisabled) {
        this.chunkedEncodingDisabled = chunkedEncodingDisabled;
    }

    public boolean isAccelerateModeEnabled() {
        return accelerateModeEnabled;
    }

    public void setAccelerateModeEnabled(boolean accelerateModeEnabled) {
        this.accelerateModeEnabled = accelerateModeEnabled;
    }

    public boolean isDualstackEnabled() {
        return dualstackEnabled;
    }

    public void setDualstackEnabled(boolean dualstackEnabled) {
        this.dualstackEnabled = dualstackEnabled;
    }

    public boolean isPayloadSigningEnabled() {
        return payloadSigningEnabled;
    }

    public void setPayloadSigningEnabled(boolean payloadSigningEnabled) {
        this.payloadSigningEnabled = payloadSigningEnabled;
    }

    public boolean isForceGlobalBucketAccessEnabled() {
        return forceGlobalBucketAccessEnabled;
    }

    public void setForceGlobalBucketAccessEnabled(boolean forceGlobalBucketAccessEnabled) {
        this.forceGlobalBucketAccessEnabled = forceGlobalBucketAccessEnabled;
    }

    public void setUseIAMCredentials(Boolean useIAMCredentials) {
        this.useIAMCredentials = useIAMCredentials;
    }

    public Boolean isUseIAMCredentials() {
        return useIAMCredentials;
    }

    public boolean isAutoCreateBucket() {
        return autoCreateBucket;
    }

    public void setAutoCreateBucket(boolean autoCreateBucket) {
        this.autoCreateBucket = autoCreateBucket;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public boolean hasProxyConfiguration() {
        return ObjectHelper.isNotEmpty(getProxyHost()) && ObjectHelper.isNotEmpty(getProxyPort());
    }

    public OSSConfiguration copy() {
        try {
            return (OSSConfiguration)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeCamelException(e);
        }
    }
}