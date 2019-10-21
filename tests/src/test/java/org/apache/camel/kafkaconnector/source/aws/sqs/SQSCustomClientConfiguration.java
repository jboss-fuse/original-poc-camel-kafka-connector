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

package org.apache.camel.kafkaconnector.source.aws.sqs;

import java.util.Map;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.kafkaconnector.AWSConfigs;
import org.apache.camel.kafkaconnector.utils.CamelStartupHelper;
import org.apache.camel.spi.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQSCustomClientConfiguration implements CamelStartupHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SQSCustomClientConfiguration.class);

    private String amazonHost;
    private String region;
    private String accessKey;
    private String secretKey;


    private class TestAWSCredentialsProvider implements AWSCredentialsProvider {
        @Override
        public AWSCredentials getCredentials() {
            return new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return accessKey;
                }

                @Override
                public String getAWSSecretKey() {
                    return secretKey;
                }
            };
        }

        @Override
        public void refresh() {

        }
    }

    @Override
    public void init(Map<String, String> props) {
        amazonHost = props.get(AWSConfigs.AMAZON_AWS_HOST);
        region = Regions.valueOf(props.get("camel.component.aws-sqs.configuration.region")).getName();

        accessKey = props.get("camel.component.aws-sqs.configuration.access-key");
        secretKey = props.get("camel.component.aws-sqs.configuration.secret-key");

        LOG.info("Loaded configuration for Test SQS client with amazon host set to {} and region set to {}",
                amazonHost, region);
    }

    @Override
    public void onStart(CamelContext context) {
        LOG.debug("Running test client startup setup");
        Registry registry = context.getRegistry();

        AmazonSQS amazonSQSClient = getSQSClient();
        registry.bind("amazonSQSClient", AmazonSQS.class, amazonSQSClient);
    }

    private AmazonSQS getSQSClient() {
        AmazonSQSClientBuilder clientBuilder = AmazonSQSClientBuilder
                .standard();

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);

        clientBuilder
                .withClientConfiguration(clientConfiguration)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonHost, region))
                .withCredentials(new TestAWSCredentialsProvider());

        return clientBuilder.build();
    }
}
