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

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.cm.exceptions.InvalidURLException;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.camel.util.URISupport;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link CMEndpoint}s.
 */
public class CMComponent extends UriEndpointComponent {

	private static final Logger LOG = LoggerFactory
			.getLogger(CMComponent.class);

	public CMComponent() {
		super(CMEndpoint.class);
	}

	public CMComponent(CamelContext context) {
		super(context, CMEndpoint.class);
	}

	/**
	 * Endpoints factory
	 */
	@Override
	protected Endpoint createEndpoint(String uri, String remaining,
			Map<String, Object> parameters) throws Exception {

		// URISupport.sanitizePath(remaining) si muestra pwd.
		// BORRAME: Remaining puede ser usuario@host:port

		String url = CMConstants.DEFAULT_SCHEME + remaining;
		if (!UrlValidator.getInstance().isValid(url)) {
			String errorMessage = String
					.format("HOST provided: %s seem to be invalid. Remember SCHEME has to be excluded.",
							url);
			Exception t = new InvalidURLException(errorMessage);
			LOG.error(errorMessage, t);
			throw t;
		}

		LOG.trace(
				"Creating endpoint uri=[{}], path=[{}], parameters=[{}]",
				new Object[] { URISupport.sanitizeUri(uri),
						URISupport.sanitizePath(remaining), parameters });

		// Set configuration based on uri parameters
		CMConfiguration config = new CMConfiguration();
		setProperties(config, parameters);

		// LOG.debug("Looking up in CAMEL REGISTRY for bean referenced by: {}",
		// URISupport.sanitizePath(remaining));
		// Throws NoSuchBeanException -> Let it go
		// MongoClient mongoClient = CamelContextHelper.mandatoryLookup(
		// getCamelContext(), remaining, MongoClient.class);
		// config.setMongoClient(mongoClient);

		// Before the endpoint is built check configuration is valid
		// config.isValid();

		// Component is an Endpoint factory. So far, just one Endpoint type.
		// Endpoint construction and configuration.
		CMEndpoint endpoint = new CMEndpoint(uri, this);
		endpoint.setConfiguration(config);
		endpoint.setHost(remaining);

		return endpoint;
	}
}
