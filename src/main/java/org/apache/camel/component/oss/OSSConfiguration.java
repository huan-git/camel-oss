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
public class OSSConfiguration {

    @UriParam
    private OSS ossClient;
    @UriParam
    private String endpoint;
    @UriParam(label = "security", secret = true)
    private String accessKeyId;
    @UriParam(label = "security", secret = true)
    private String accessKeySecret;
    private String bucketName;
    @UriParam(label = "consumer")
    private String prefix;
    private String delimiter;
    @UriParam(label = "consumer")
    private Boolean includeBody=true;
    @UriParam(label = "consumer")
    private Boolean autocloseBody =true ;
    @UriParam(label = "consumer")
    private String fileName;
    @UriParam(label = "producer")
    private boolean multiPartUpload;
    @UriParam(label = "producer")
    private String keyName;
    @UriParam(label = "producer")
    private Boolean deleteAfterWrite=true;
    private Boolean  deleteAfterRead=true;
    private Boolean autoCreateBucket=true;
    private String locationConstraint;
    @UriParam(label = "producer", defaultValue = "" + 25 * 1024 * 1024)
    private long partSize = 25 * 1024 * 1024;
    @UriParam(label = "producer", enums = "copyObject,listObjects,deleteObject,deleteBucket,listBuckets,getObject,getObjectRange")
    private OSSOperations operation;

    public OSSOperations getOperation() {
        return operation;
    }

    public void setOperation(OSSOperations operation) {
        this.operation = operation;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public Boolean isAutocloseBody() {
        return autocloseBody;
    }

    public void setAutocloseBody(Boolean autocloseBody) {
        this.autocloseBody = autocloseBody;
    }

    public Boolean isIncludeBody() {
        return includeBody;
    }

    public void setIncludeBody(Boolean includeBody) {
        this.includeBody = includeBody;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isMultiPartUpload() {
        return multiPartUpload;
    }

    public void setMultiPartUpload(boolean multiPartUpload) {
        this.multiPartUpload = multiPartUpload;
    }

    public Boolean isDeleteAfterRead() {
        return deleteAfterRead;
    }

    public void setDeleteAfterRead(Boolean deleteAfterRead) {
        this.deleteAfterRead = deleteAfterRead;
    }

    public Boolean isDeleteAfterWrite() {
        return deleteAfterWrite;
    }

    public void setIsDeleteAfterWrite(Boolean deleteAfterWrite) {
        this.deleteAfterWrite = deleteAfterWrite;
    }

    public Boolean isAutoCreateBucket() {
        return autoCreateBucket;
    }

    public void setAutoCreateBucket(Boolean autoCreateBucket) {
        this.autoCreateBucket = autoCreateBucket;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getLocationConstraint() {
        return locationConstraint;
    }

    public void setLocationConstraint(String locationConstraint) {
        this.locationConstraint = locationConstraint;
    }

    public long getPartSize() {
        return partSize;
    }

    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    public OSS getOssClient() {
        return ossClient;
    }

    public void setOssClient(OSS ossClient) {
        this.ossClient = ossClient;
    }

    @Override
    public String toString() {
        return "OSSConfiguration{" +
                "ossClient=" + ossClient +
                ", endpoint='" + endpoint + '\'' +
                ", accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", prefix='" + prefix + '\'' +
                ", delimiter='" + delimiter + '\'' +
                ", includeBody=" + includeBody +
                ", autocloseBody=" + autocloseBody +
                ", fileName='" + fileName + '\'' +
                ", multiPartUpload=" + multiPartUpload +
                ", keyName='" + keyName + '\'' +
                ", deleteAfterWrite=" + deleteAfterWrite +
                ", deleteAfterRead=" + deleteAfterRead +
                ", autoCreateBucket=" + autoCreateBucket +
                ", locationConstraint='" + locationConstraint + '\'' +
                ", partSize=" + partSize +
                '}';
    }
}