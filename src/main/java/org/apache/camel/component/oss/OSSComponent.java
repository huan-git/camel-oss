/**
 * @program: camel-oss
 * @description: this is the description of the OSSComponent class
 * @author: ahuan
 * @version: 2020-07-23 09:54
 **/
package org.apache.camel.component.oss;

import java.util.Map;
import java.util.Set;

import com.aliyun.oss.OSS;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

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
        if (remaining.startsWith("arn:")) {
            remaining = remaining.substring(remaining.lastIndexOf(':') + 1, remaining.length());
        }
        final OSSConfiguration configuration = this.configuration != null ? this.configuration.copy() : new OSSConfiguration();
        configuration.setBucketName(remaining);
        OSSEndpoint endpoint = new OSSEndpoint( this, uri,configuration);
        setProperties(endpoint, parameters);
        if (!configuration.isUseIAMCredentials() && configuration.getAmazonS3Client() == null && (configuration.getAccessKey() == null || configuration.getSecretKey() == null)) {
            throw new IllegalArgumentException("useIAMCredentials is set to false, AmazonS3Client or accessKey and secretKey must be specified");
        }

        return endpoint;
    }

    public OSSConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * The component configuration
     */
    public void setConfiguration(OSSConfiguration configuration) {
        this.configuration = configuration;
    }

    private void checkAndSetRegistryClient(OSSConfiguration configuration) {
        Set<OSS> clients = getCamelContext().getRegistry().findByType(OSS.class);
        if (clients.size() == 1) {
            configuration.setOSSClient(clients.stream().findFirst().get());
        }
    }
}