package org.apache.camel.component.oss;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriParam;
import org.apache.camel.support.ScheduledPollEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class OSSEndpoint extends ScheduledPollEndpoint {
	private static final Logger LOG = LoggerFactory.getLogger(OSSEndpoint.class);

	@UriParam
	private final OSSConfiguration configuration;
	@UriParam(label = "consumer", defaultValue = "10")
	private int maxMessagesPerPoll = 10;
	@UriParam(label = "consumer", defaultValue = "60")
	private int maxConnections = 50 + maxMessagesPerPoll;

	public OSSEndpoint(OSSComponent component, String uri, OSSConfiguration configuration) {
		super(uri, component);
		this.configuration=configuration;
	}
 
	@Override
	public Producer createProducer() throws Exception {
		return new OSSProducer(this);
	}
 
	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return new OSSConsumer(this, processor);
	}
 
	@Override
	public boolean isSingleton() {
		return false;
	}
	
	public Exchange createExchange(File file) {
		//TODO
		return null;
	}
}