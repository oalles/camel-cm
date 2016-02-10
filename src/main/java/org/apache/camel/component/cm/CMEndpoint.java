/**
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
package org.apache.camel.component.cm;

import org.apache.camel.Consumer;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.api.management.ManagedAttribute;
import org.apache.camel.api.management.ManagedOperation;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedResource(description = "CM SMS Endpoint")
// @UriEndpoint(scheme = "cm", title = "CM DIRECT SMS", syntax = "cm:host",
// label = "sms provider", producerOnly = true)
public class CMEndpoint extends DefaultEndpoint {

	private static final Logger LOG = LoggerFactory.getLogger(CMEndpoint.class);

	// TODO: SYNC request to CM host. See ResponseProcessor.

	@UriPath
	@Metadata(description = "SMS Provider HOST with scheme", required = "true")
	private String host;

	/**
	 * data needed for exchange interaction
	 */
	private CMConfiguration configuration;

	/**
	 * Constructs a partially-initialized CMEndpoint instance. Useful when
	 * creating endpoints manually (e.g., as beans in Spring).
	 */
	// We are just going to allow fully initialized endpoint instances
	// public CMEndpoint() {
	// }

	/**
	 * Constructs a fully-initialized CMEndpoint instance. This is the preferred
	 * method of constructing an object from Java code (as opposed to Spring
	 * beans, etc.).
	 * 
	 * @param endpointUri
	 *            the full URI used to create this endpoint
	 * @param component
	 *            the component that created this endpoint
	 */
	public CMEndpoint(String uri, CMComponent component) {
		super(uri, component);

		// SYNC Request + CMResponse Processing.
		this.setExchangePattern(ExchangePattern.InOut);
		LOG.info("+ CM - Endpoint created.");
	}

	/**
	 * Provides a channel on which clients can send Messages to a CM Endpoint
	 */
	@Override
	public Producer createProducer() throws Exception {
		CMConfiguration config = getConfiguration();
		CMProducer producer = new CMProducer(this,
				new CMSenderOneMessageImpl(host, config.getProductToken(), config.getDefaultMaxNumberOfParts()),
				config.getResponseProcessor());
		return producer;
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {

		throw new RuntimeCamelException("So far, cannot consume from CM Endpoint: " + getEndpointUri());
	}

	public CMConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(CMConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	// @Override
	// public Exchange createExchange() {
	// return super.createExchange();
	// }

	@ManagedAttribute
	public String getHost() {
		return host;
	}

	@ManagedOperation(description = "Dynamically modified Service HOST")
	public void setHost(String host) {
		this.host = host;
	}
}
