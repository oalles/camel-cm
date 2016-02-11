package org.apache.camel.component.cm.test;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

@Configuration("cmConfig")
@PropertySource("classpath:/cm-smsgw.properties")
public class TestConfiguration extends SingleRouteCamelConfiguration {

	public static final String SIMPLE_ROUTE_ID = "simple-route";

	private String uri;

	@Override
	public RouteBuilder route() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				Assert.hasLength(uri);

				log.info("CM URI: {}", uri);

				// Route definition
				from("direct:sms").to(uri).to("mock:test").routeId(SIMPLE_ROUTE_ID).autoStartup(true);

			}
		};
	}

	/**
	 * Build the URI of the CM Component based on Environmental properties
	 */
	@Override
	public final void setApplicationContext(final ApplicationContext applicationContext) {
	
		super.setApplicationContext(applicationContext);

		Environment env = applicationContext.getEnvironment();

		final String host = env.getRequiredProperty("cm.url");
		final String productTokenString = env.getRequiredProperty("cm.product-token");
		final String sender = env.getRequiredProperty("cm.default-sender");

		final StringBuffer cmUri = new StringBuffer("cm:" + host).append("?productToken=").append(productTokenString);
		if (sender != null && !sender.isEmpty()) {
			cmUri.append("&defaultFrom=").append(sender);
		}

		// Is there a ResponseProcessor implementation available in the
		// registry?
		final String responseProcessor = env.getProperty("responseProcessor");
		if (responseProcessor != null && !responseProcessor.isEmpty()) {
			cmUri.append("&responseProcessor=").append(responseProcessor);
		}

		// Defaults to false
		final Boolean testConnectionOnStartup = Boolean
				.parseBoolean(env.getProperty("testConnectionOnStartup", "false"));
		if (testConnectionOnStartup != false) {
			cmUri.append("&testConnectionOnStartup=").append(testConnectionOnStartup.toString());
		}

		// Defaults to 8
		final Integer defaultMaxNumberOfParts = Integer.parseInt(env.getProperty("defaultMaxNumberOfParts", "8"));
		cmUri.append("&defaultMaxNumberOfParts=").append(defaultMaxNumberOfParts.toString());

		this.uri = cmUri.toString();
	}

	public String getUri() {
		return uri;
	}
}