package org.apache.camel.component.cm.test;

import java.util.UUID;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:/cm-smsgw.properties")
public class TestConfiguration extends SingleRouteCamelConfiguration implements EnvironmentAware {

	private static final String SIMPLE_ROUTE_ID = "simple-route";

	private Environment env;

	@Override
	public RouteBuilder route() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				final String host = env.getRequiredProperty("cm.url");
				// final String productTokenString =
				// env.getRequiredProperty("cm.product-token");
				final String productTokenString = UUID.randomUUID().toString();
				final String sender = env.getRequiredProperty("cm.default-sender");

				final StringBuffer cmUri = new StringBuffer("cm:" + host).append("?productToken=")
						.append(productTokenString);
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
				final Integer defaultMaxNumberOfParts = Integer
						.parseInt(env.getProperty("defaultMaxNumberOfParts", "8"));
				cmUri.append("&defaultMaxNumberOfParts=").append(defaultMaxNumberOfParts.toString());

				// Route definition
				from("direct:sms").setExchangePattern(ExchangePattern.InOnly).to(cmUri.toString()).to("mock:test")
						.routeId(SIMPLE_ROUTE_ID).autoStartup(true);

			}
		};
	}

	public void setEnvironment(Environment environment) {
		this.env = environment;
	}
}