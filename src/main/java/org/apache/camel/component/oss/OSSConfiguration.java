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

    public Boolean isDeleteAfterWrite() {
        return deleteAfterWrite;
    }

    public void setIsDeleteAfterWrite(Boolean deleteAfterWrite) {
        this.deleteAfterWrite = deleteAfterWrite;
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
}