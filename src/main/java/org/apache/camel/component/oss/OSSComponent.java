/**
 * @program: camel-oss
 * @description: this is the description of the OSSComponent class
 * @author: ahuan
 * @version: 2020-07-23 09:54
 **/
package org.apache.camel.component.oss;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.oss.client.impl.OSSClientImpl;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

//@Component("oss")
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
        OSSEndpoint endpoint = new OSSEndpoint(this, uri, configuration);
        setProperties(endpoint, parameters);
        if (configuration.getOssClient() == null && (configuration.getAccessKeyId() == null || configuration.getAccessKeySecret() == null)) {
            throw new IllegalArgumentException("ossClient or accessKeyId and accessKeySecret must be specified");
        }

        return endpoint;
    }

    public OSSConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(OSSConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void setProperties(Endpoint endpoint, Map<String, Object> parameters) throws Exception {
        endpoint.configureProperties(parameters);
        OSSEndpoint ossEndpoint = (OSSEndpoint) endpoint;
        OSSConfiguration configuration = ossEndpoint.getConfiguration();
        Class<? extends OSSConfiguration> configurationClass = configuration.getClass();
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            Field declaredField = configurationClass.getDeclaredField(key);
            String filedType = declaredField.getType().getSimpleName();
            declaredField.setAccessible(true);
            if ("Boolean".equals(filedType)) {
                declaredField.set(configuration, Boolean.getBoolean((String) parameters.get(key)));
            } else if ("OSSOperations".equals(filedType)){
                declaredField.set(configuration,OSSOperations.valueOf(parameters.get(key).toString()));
            }else {
                declaredField.set(configuration, parameters.get(key));
            }
        }
        parameters.clear();
    }
}