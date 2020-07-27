package Test;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.oss.OSSComponent;
import org.apache.camel.component.oss.OSSConstants;
import org.apache.camel.component.oss.OSSEndpoint;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelTest {
    public static void main(String[] args) throws Exception {
        CamelContext camelContext = new DefaultCamelContext();

        camelContext.addComponent("oss",new OSSComponent());
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                System.out.println("1111");
                from("file:D:\\text?noop=true")
                        .process(new Processor() {
                                     @Override
                                     public void process(Exchange exchange) throws Exception {
                                         exchange.getIn().setHeader(OSSConstants.KEY, "camelKey");
                                     }
                                 }
                        )
                        .to("oss:ahuan?deleteAfterWrite=false&accessKeyId=LTAI4FpCK5xEYnMrJvFWa5iZ&accessKeySecret=82cACiCF4VLdrIMmAHHtWqn6KVUr8s&endpoint=oss-cn-beijing.aliyuncs.com")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Object header = exchange.getMessage().getHeader(OSSConstants.KEY);
                                System.out.println(header.toString());
                            }
                        })
                        .to("seda:end");

                System.out.println("222");

            }
        });
        camelContext.start();
        ConsumerTemplate consumerTemplate = camelContext.createConsumerTemplate();
        /*String o = consumerTemplate.receive("seda:end").getMessage().getHeader(OSSConstants.E_TAG.toString());
        System.out.println(o);*/
        Thread.sleep(10000);
        synchronized (CamelTest.class){
            CamelTest.class.wait();
        }
       camelContext.stop();
    }
}
