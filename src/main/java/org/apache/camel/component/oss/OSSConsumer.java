package org.apache.camel.component.oss;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;

/**
 * @program: camel-oss
 * @description: this is the description of the OSSConsumer class
 * @author: ahuan
 * @version: 2020-07-23 10:11
 **/
public class OSSConsumer implements Consumer {

    public OSSConsumer(OSSEndpoint ossEndpoint, Processor processor) {
    }

    public Processor getProcessor() {
        return null;
    }

    public Endpoint getEndpoint() {
        return null;
    }

    public void start() {

    }

    public void stop() {

    }
}