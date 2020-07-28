package Test;

import com.aliyun.oss.model.ObjectListing;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.oss.OSSComponent;
import org.apache.camel.component.oss.OSSConstants;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelTest3 {
    public static void main(String[] args) throws Exception {
        CamelContext camelContext = new DefaultCamelContext();

        camelContext.addComponent("oss",new OSSComponent());
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                System.out.println("1111");
                from("oss:ahuan?prefix=test&moveAfterRead=true&deleteAfterRead=false&destinationBucket=test2&accessKeyId=LTAI4FpCK5xEYnMrJvFWa5iZ&accessKeySecret=82cACiCF4VLdrIMmAHHtWqn6KVUr8s&endpoint=oss-cn-beijing.aliyuncs.com")
                        .to("file:D:\\temp");
                System.out.println("2222s");

            }
        });
        camelContext.start();
        ConsumerTemplate consumerTemplate = camelContext.createConsumerTemplate();
        Thread.sleep(10000);
        camelContext.stop();
    }
}
