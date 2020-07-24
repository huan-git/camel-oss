package org.apache.camel.component.oss;

import com.aliyun.oss.OSS;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

/**
 * @program: camel-oss
 * @description: this is the description of the OSSConfiguration class
 * @author: ahuan
 * @version: 2020-07-23 09:54
 **/
@UriParams
public class OSSConfiguration implements Cloneable {
    private String bucketName;
    @UriParam
    private OSS amazonS3Client;
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
    @UriParam(label = "consumer", defaultValue = "true")
    private boolean includeFolders = true;
    @UriParam
    private String region;
    @UriParam(label = "consumer", defaultValue = "true")
    private boolean deleteAfterRead = true;
    @UriParam(label = "consumer")
    private boolean moveAfterRead;
    @UriParam(label = "consumer")
    private String destinationBucket;
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
    @UriParam
    private String proxyHost;
    @UriParam
    private Integer proxyPort;
    @UriParam(label = "consumer", defaultValue = "true")
    private boolean includeBody = true;
    @UriParam(label = "consumer,advanced", defaultValue = "true")
    private boolean autocloseBody = true;
    @UriParam(label = "common", defaultValue = "true")
    private boolean autoCreateBucket = true;
    @UriParam(label = "producer,advanced", defaultValue = "false")
    private boolean useAwsKMS;
    @UriParam(label = "producer,advanced")
    private String awsKMSKeyId;
    @UriParam(label = "producer,advanced", defaultValue = "false")
    private boolean useCustomerKey;
    @UriParam(label = "common,advanced")
    private String customerKeyId;
    @UriParam(label = "common,advanced")
    private String customerKeyMD5;
    @UriParam(label = "common,advanced")
    private String customerAlgorithm;
    @UriParam(defaultValue = "false")
    private boolean useIAMCredentials;
    @UriParam(label = "producer")
    private String keyName;
    @UriParam(defaultValue = "false")
    private boolean trustAllCertificates;
    @UriParam
    private String uriEndpoint;
    @UriParam(defaultValue = "false")
    private boolean pojoRequest;
    @UriParam(label = "common", defaultValue = "true")
    private boolean autoDiscoverClient = true;

    public OSS getAmazonS3Client() {
        return amazonS3Client;
    }

    public void setAmazonS3Client(OSS amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

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

    /**
     * If it is true, the folders/directories will be consumed.
     * If it is false, they will be ignored, and Exchanges will not be created for those
     */
    public void setIncludeFolders(boolean includeFolders) {
        this.includeFolders = includeFolders;
    }

    public boolean isIncludeFolders() {
        return includeFolders;
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
     * the configuration will expect the lowercase name of the region (for
     * example ap-east-1) You'll need to use the name Region.EU_WEST_1.id()
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

    /**
     * Delete objects from S3 after they have been retrieved. The delete is only
     * performed if the Exchange is committed. If a rollback occurs, the object
     * is not deleted.
     * <p/>
     * If this option is false, then the same objects will be retrieve over and
     * over again on the polls. Therefore you need to use the Idempotent
     * Consumer EIP in the route to filter out duplicates. You can filter using
     *
     */
    public void setDeleteAfterRead(boolean deleteAfterRead) {
        this.deleteAfterRead = deleteAfterRead;
    }

    public boolean isMoveAfterRead() {
        return moveAfterRead;
    }

    /**
     * Move objects from S3 bucket to a different bucket after they have been retrieved. To accomplish the operation
     * the destinationBucket option must be set.
     * The copy bucket operation is only performed if the Exchange is committed. If a rollback occurs, the object
     * is not moved.
     */
    public void setMoveAfterRead(boolean moveAfterRead) {
        this.moveAfterRead = moveAfterRead;
    }

    public String getDestinationBucket() {
        return destinationBucket;
    }

    /**
     * Define the destination bucket where an object must be moved when moveAfterRead is set to true.
     */
    public void setDestinationBucket(String destinationBucket) {
        this.destinationBucket = destinationBucket;
    }

    public boolean isDeleteAfterWrite() {
        return deleteAfterWrite;
    }

    /**
     * Delete file object after the S3 file has been uploaded
     */
    public void setDeleteAfterWrite(boolean deleteAfterWrite) {
        this.deleteAfterWrite = deleteAfterWrite;
    }

    public String getPolicy() {
        return policy;
    }

    /**
     * The policy for this queue to set in the
     * `com.amazonaws.services.s3.AmazonS3#setBucketPolicy()` method.
     */
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getStorageClass() {
        return storageClass;
    }

    /**
     * The storage class to set in the
     * `com.amazonaws.services.s3.model.PutObjectRequest` request.
     */
    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }


    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * To define a proxy host when instantiating the SQS client
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    /**
     * Specify a proxy port to be used inside the client definition.
     */
    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }


    public boolean isAutocloseBody() {
        return autocloseBody;
    }

    /**
     * If this option is true and includeBody is true, then the S3Object.close()
     * method will be called on exchange completion. This option is strongly
     * related to includeBody option. In case of setting includeBody to true and
     * autocloseBody to false, it will be up to the caller to close the S3Object
     * stream. Setting autocloseBody to true, will close the S3Object stream
     * automatically.
     */
    public void setAutocloseBody(boolean autocloseBody) {
        this.autocloseBody = autocloseBody;
    }

    public boolean isUseAwsKMS() {
        return useAwsKMS;
    }

    /**
     * Define if KMS must be used or not
     */
    public void setUseAwsKMS(boolean useAwsKMS) {
        this.useAwsKMS = useAwsKMS;
    }

    public String getAwsKMSKeyId() {
        return awsKMSKeyId;
    }

    /**
     * Define the id of KMS key to use in case KMS is enabled
     */
    public void setAwsKMSKeyId(String awsKMSKeyId) {
        this.awsKMSKeyId = awsKMSKeyId;
    }

    public boolean isUseCustomerKey() {
        return useCustomerKey;
    }

    /**
     * Define if Customer Key must be used or not
     */
    public void setUseCustomerKey(boolean useCustomerKey) {
        this.useCustomerKey = useCustomerKey;
    }

    public String getCustomerKeyId() {
        return customerKeyId;
    }

    /**
     * Define the id of Customer key to use in case CustomerKey is enabled
     */
    public void setCustomerKeyId(String customerKeyId) {
        this.customerKeyId = customerKeyId;
    }



    public String getCustomerKeyMD5() {
        return customerKeyMD5;
    }

    /**
     * Define the MD5 of Customer key to use in case CustomerKey is enabled
     */
    public void setCustomerKeyMD5(String customerKeyMD5) {
        this.customerKeyMD5 = customerKeyMD5;
    }

    public String getCustomerAlgorithm() {
        return customerAlgorithm;
    }

    /**
     * Define the customer algorithm to use in case CustomerKey is enabled
     */
    public void setCustomerAlgorithm(String customerAlgorithm) {
        this.customerAlgorithm = customerAlgorithm;
    }

    /**
     * Set whether the S3 client should expect to load credentials on an EC2
     * instance or to expect static credentials to be passed in.
     */
    public void setUseIAMCredentials(Boolean useIAMCredentials) {
        this.useIAMCredentials = useIAMCredentials;
    }

    public Boolean isUseIAMCredentials() {
        return useIAMCredentials;
    }

    public boolean isAutoCreateBucket() {
        return autoCreateBucket;
    }

    /**
     * Setting the autocreation of the bucket
     */
    public void setAutoCreateBucket(boolean autoCreateBucket) {
        this.autoCreateBucket = autoCreateBucket;
    }

    public String getKeyName() {
        return keyName;
    }

    /**
     * Setting the key name for an element in the bucket through endpoint
     * parameter
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }




    public boolean isPojoRequest() {
        return pojoRequest;
    }

    /**
     * If we want to use a POJO request as body or not
     */
    public void setPojoRequest(boolean pojoRequest) {
        this.pojoRequest = pojoRequest;
    }

    public boolean isTrustAllCertificates() {
        return trustAllCertificates;
    }

    /**
     * If we want to trust all certificates in case of overriding the endpoint
     */
    public void setTrustAllCertificates(boolean trustAllCertificates) {
        this.trustAllCertificates = trustAllCertificates;
    }

    public boolean isAutoDiscoverClient() {
        return autoDiscoverClient;
    }

    /**
     * Setting the autoDiscoverClient mechanism, if true, the component will
     * look for a client instance in the registry automatically otherwise it
     * will skip that checking.
     */
    public void setAutoDiscoverClient(boolean autoDiscoverClient) {
        this.autoDiscoverClient = autoDiscoverClient;
    }

    public String getUriEndpoint() {
        return uriEndpoint;
    }

    public void setUriEndpoint(String uriEndpoint) {
        this.uriEndpoint = uriEndpoint;
    }

    public OSSConfiguration copy() {
        try {
            return (OSSConfiguration)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeCamelException(e);
        }
    }
}
