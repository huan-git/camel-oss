package org.apache.camel.component.oss;

import com.aliyun.oss.OSS;
import org.apache.camel.Processor;
import org.apache.camel.support.ScheduledBatchPollingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * @program: camel-oss
 * @description: this is the description of the OSSConsumer class
 * @author: ahuan
 * @version: 2020-07-23 10:11
 **/
public class OSSConsumer extends ScheduledBatchPollingConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(OSSConsumer.class);

    public OSSConsumer(OSSEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    public int processBatch(Queue<Object> exchanges) throws Exception {
        return 0;
    }

    @Override
    protected int poll() throws Exception {
        return 0;
    }
    protected OSSConfiguration getConfiguration() {
        return getEndpoint().getConfiguration();
    }

    protected OSS getOSSClient() {
        return getEndpoint().getOssClient();
    }

    @Override
    public OSSEndpoint getEndpoint() {
        return (OSSEndpoint)super.getEndpoint();
    }

}