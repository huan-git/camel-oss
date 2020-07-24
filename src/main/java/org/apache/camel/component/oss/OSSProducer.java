package org.apache.camel.component.oss;

import com.aliyun.oss.model.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.WrappedFile;
import org.apache.camel.support.DefaultProducer;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: camel-oss
 * @description: this is the description of the OSSProducer class
 * @author: ahuan
 * @version: 2020-07-23 10:10
 **/
public class OSSProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(OSSProducer.class);

    private transient String s3ProducerToString;

    public OSSProducer(OSSEndpoint ossEndpoint) {
        super(ossEndpoint);
    }

    @Override
    public OSSEndpoint getEndpoint() {
        return (OSSEndpoint) super.getEndpoint();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
            if (getConfiguration().isMultiPartUpload()) {
                processMultiPart(exchange);
            } else {
                processSingleOp(exchange);
            }
    }
    public void processMultiPart(final Exchange exchange) throws Exception {
        File filePayload = null;
        Object obj = exchange.getIn().getMandatoryBody();
        // Need to check if the message body is WrappedFile
        if (obj instanceof WrappedFile) {
            obj = ((WrappedFile<?>)obj).getFile();
        }
        if (obj instanceof File) {
            filePayload = (File)obj;
        } else {
            throw new IllegalArgumentException("oss: MultiPart upload requires a File input.");
        }

        final String keyName = determineKey(exchange);
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(getConfiguration().getBucketName(), getConfiguration().getBucketName()+"/"+getConfiguration().getFileName());
        InitiateMultipartUploadResult upresult = getEndpoint().getOssClient().initiateMultipartUpload(request);
        String uploadId = upresult.getUploadId();
        List<PartETag> partETags = new ArrayList<PartETag>();
        // 计算文件有多少个分片。
        final long partSize = 1 * 1024 * 1024L;   // 1MB
        final File sampleFile = filePayload;
        long fileLength = sampleFile.length();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }
        CompleteMultipartUploadResult completeMultipartUploadResult = null;
        try {
            for (int i = 1; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream instream = new FileInputStream(sampleFile);
                // 跳过已经上传的分片。
                instream.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(getConfiguration().getBucketName());
                uploadPartRequest.setKey(getConfiguration().getBucketName()+"/"+getConfiguration().getFileName());
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
                uploadPartRequest.setPartNumber( i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = getEndpoint().getOssClient().uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());
            }
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(getConfiguration().getBucketName(), getConfiguration().getBucketName()+"/"+getConfiguration().getFileName(), uploadId, partETags);

             completeMultipartUploadResult = getEndpoint().getOssClient().completeMultipartUpload(completeMultipartUploadRequest);

        } catch (Exception e) {
            getEndpoint().getOssClient()
                    .abortMultipartUpload( new AbortMultipartUploadRequest(getConfiguration().getBucketName(), getConfiguration().getBucketName()+"/"+getConfiguration().getFileName(), uploadId));
            throw e;
        }

        Message message = getMessageForResponse(exchange);
        message.setHeader(OSSConstants.E_TAG, completeMultipartUploadResult.getETag());
        if (completeMultipartUploadResult.getVersionId() != null) {
            message.setHeader(OSSConstants.VERSION_ID, completeMultipartUploadResult.getVersionId() );
        }

        if (getConfiguration().isDeleteAfterWrite()) {
            FileUtil.deleteFile(filePayload);
        }
    }

    public void processSingleOp(final Exchange exchange) throws Exception {
        File filePayload = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        Object obj = exchange.getIn().getMandatoryBody();
        //PutObjectRequest.Builder putObjectRequest = PutObjectRequest.builder();
        // Need to check if the message body is WrappedFile
        if (obj instanceof WrappedFile) {
            obj = ((WrappedFile<?>)obj).getFile();
        }
        if (obj instanceof File) {
            filePayload = (File)obj;
            is = new FileInputStream(filePayload);
        } else {
            throw new Exception("上传的不是文件");
        }
        final String bucketName = determineBucketName(exchange);
        final String key = determineKey(exchange);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key+"/"+filePayload.getName(), filePayload);
        PutObjectResult putObjectResult = getEndpoint().getOssClient().putObject(putObjectRequest);
           Message message = getMessageForResponse(exchange);
        message.setHeader(OSSConstants.E_TAG, putObjectResult.getETag());
        message.setBody(putObjectResult.getETag());
        message.setHeader(OSSConstants.KEY ,key+"/"+filePayload.getName());
        if (putObjectResult.getVersionId() != null) {
            message.setHeader(OSSConstants.VERSION_ID, putObjectResult.getVersionId());
        }
        IOHelper.close(is);
        if (getConfiguration().isDeleteAfterWrite() && filePayload != null) {
            FileUtil.deleteFile(filePayload);
        }
    }


    private String determineBucketName(final Exchange exchange) {
        String bucketName = exchange.getIn().getHeader(OSSConstants.BUCKET_NAME, String.class);

        if (ObjectHelper.isEmpty(bucketName)) {
            bucketName = getConfiguration().getBucketName();
            LOG.trace("OSS Bucket name header is missing, using default one [{}]", bucketName);
        }

        if (bucketName == null) {
            throw new IllegalArgumentException("AWS S3 Bucket name header is missing or not configured.");
        }

        return bucketName;
    }

    private String determineKey(final Exchange exchange) {
        String key = exchange.getIn().getHeader(OSSConstants.KEY, String.class);
        if (ObjectHelper.isEmpty(key)) {
            key = getConfiguration().getKeyName();
        }
        if (key == null) {
            throw new IllegalArgumentException("OSS Key header missing.");
        }
        return key;
    }

    protected OSSConfiguration getConfiguration() {
        return getEndpoint().getConfiguration();
    }

    public static Message getMessageForResponse(final Exchange exchange) {
        return exchange.getMessage();
    }

}