/**
 * @program: camel-oss
 * @description: this is the description of the OSSComponent class
 * @author: ahuan
 * @version: 2020-07-23 09:54
 **/
package org.apache.camel.component.oss;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

@Component("oss")
public class OSSComponent extends DefaultComponent {

    @Metadata
    private OSSConfiguration configuration = new OSSConfiguration();

    public OSSComponent() {
        this(null);
    }

    public OSSComponent(CamelContext context) {
        super(context);

    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        if (remaining == null || remaining.trim().length() == 0) {
            throw new IllegalArgumentException("Bucket name must be specified.");
        }

        final OSSConfiguration configuration = new OSSConfiguration();
        configuration.setBucketName(remaining);
        OSSEndpoint endpoint = new OSSEndpoint( this, uri,configuration);
        setProperties(endpoint, parameters);

        if (configuration.getOssClient()== null && (configuration.getAccessKeyId() == null || configuration.getAccessKeySecret() == null)) {
            throw new IllegalArgumentException("useIAMCredentials is set to false, AmazonS3Client or accessKey and secretKey must be specified");
        }

        return endpoint;
    }

    public OSSConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(OSSConfiguration configuration) {
        this.configuration = configuration;
    }
}