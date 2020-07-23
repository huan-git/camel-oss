package org.apache.camel.component.oss;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;

/**
 * @program: camel-oss
 * @description: this is the description of the OSSProducer class
 * @author: ahuan
 * @version: 2020-07-23 10:10
 **/
public class OSSProducer implements Producer {

    public OSSProducer(OSSEndpoint ossEndpoint) {
    }

    public Endpoint getEndpoint() {
        return null;
    }

    public boolean isSingleton() {
        return false;
    }

    public void process(Exchange exchange) throws Exception {

    }

    public void start() {

    }

    public void stop() {

    }
}