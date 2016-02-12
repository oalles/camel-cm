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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.camel.BeanInject;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.cm.exceptions.InvalidURLException;
import org.apache.camel.component.cm.exceptions.InvalidUriEndpointException;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.camel.util.URISupport;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link CMEndpoint}s.
 */
public class CMComponent extends UriEndpointComponent {

	@BeanInject
	private Validator validator;

	private static final Logger LOG = LoggerFactory.getLogger(CMComponent.class);

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
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

		String url = CMConstants.DEFAULT_SCHEME + remaining;
		if (!UrlValidator.getInstance().isValid(url)) {
			String errorMessage = String
					.format("HOST provided: %s seem to be invalid. Remember SCHEME has to be excluded.", url);
			Exception t = new InvalidURLException(errorMessage);
			LOG.error(errorMessage, t);
			throw t;
		}

		LOG.debug("Creating endpoint uri=[{}], path=[{}], parameters=[{}]",
				new Object[] { URISupport.sanitizeUri(uri), URISupport.sanitizePath(remaining), parameters });

		// Set configuration based on uri parameters
		CMConfiguration config = new CMConfiguration();
		setProperties(config, parameters);

		// Validate configuration
		for (ConstraintViolation<CMConfiguration> cv : validator.validate(config)) {
			String msg = String.format("Invalid value for %s: %s", cv.getPropertyPath().toString(), cv.getMessage());
			LOG.error(msg);
			throw new InvalidUriEndpointException(msg);
		}

		// Component is an Endpoint factory. So far, just one Endpoint type.
		// Endpoint construction and configuration.

		CMEndpoint endpoint = new CMEndpoint(uri, this);
		endpoint.setConfiguration(config);
		endpoint.setHost(remaining);

		return endpoint;
	}
}
