/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.oss;

import java.util.Map;
import java.util.Set;

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

}