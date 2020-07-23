package org.apache.camel.component.oss.client;

import com.aliyun.oss.OSS;

/**
 * @program: camel-oss
 * @description: this is the description of the OSSClient class
 * @author: ahuan
 * @version: 2020-07-23 10:17
 **/
public interface OSSClient {
    /**
     * 获取OSSclient
     * @return
     */
    OSS getOSSClient();
}