package Test;

import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.ObjectListing;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.oss.OSSComponent;
import org.apache.camel.component.oss.OSSConstants;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.List;

public class CamelTest2 {
    public static void main(String[] args) throws Exception {
        CamelContext camelContext = new DefaultCamelContext();

        camelContext.addComponent("oss",new OSSComponent());
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                System.out.println("1111");
                from("direct:start")
                        .process(new Processor() {
                                     @Override
                                     public void process(Exchange exchange) throws Exception {
                                         exchange.getIn().setHeader(OSSConstants.KEY, "camelKey");
                                     }
                                 }
                        )
                        .to("oss:ahuan?operation=listObjects&accessKeyId=LTAI4FpCK5xEYnMrJvFWa5iZ&accessKeySecret=82cACiCF4VLdrIMmAHHtWqn6KVUr8s&endpoint=oss-cn-beijing.aliyuncs.com")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Object header = exchange.getMessage().getHeader(OSSConstants.KEY);
                                //System.out.println(header.toString());
                            }
                        })
                        .to("seda:end");

                System.out.println("222");

            }
        });
        camelContext.start();
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start","1111");
        ConsumerTemplate consumerTemplate = camelContext.createConsumerTemplate();
        ObjectListing o = (ObjectListing) consumerTemplate.receive("seda:end").getMessage().getBody();
        o.getObjectSummaries().forEach(e->System.out.println(e.getKey()));
        Thread.sleep(10000);
        synchronized (CamelTest.class){
            CamelTest.class.wait();
        }
        camelContext.stop();
    }
}
