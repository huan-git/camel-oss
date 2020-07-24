package org.apache.camel.component.oss.client.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.camel.component.oss.OSSConfiguration;
import org.apache.camel.component.oss.client.OSSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @program: camel-oss
 * @description: this is the description of the OSSClientImpl class
 * @author: ahuan
 * @version: 2020-07-23 10:21
 **/
public class OSSClientImpl implements OSSClient {
    private static final Logger LOG = LoggerFactory.getLogger(OSSClientImpl.class);
    private OSSConfiguration ossConfiguration;

    public OSSClientImpl(OSSConfiguration ossConfiguration) {
        LOG.trace("Creating an OSS manager using static credentials.");
        this.ossConfiguration = ossConfiguration;
    }

    @Override
    public OSS getOSSClient() {
        //TODO 参考阿里云oss文档
        String endpoint = ossConfiguration.getUriEndpoint();
        String accessKeyId = ossConfiguration.getAccessKey();
        String accessKeySecret = ossConfiguration.getSecretKey();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }
}