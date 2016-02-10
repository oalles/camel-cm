package org.apache.camel.component.cm.test;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class }, loader = CamelSpringDelegatingTestContextLoader.class)
// @MockEndpoints
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(false)
public class ATestClass extends AbstractJUnit4SpringContextTests {

	// dependency: camel-spring-javaconfig

	private static Logger LOG = LoggerFactory.getLogger(ATestClass.class);

	@Autowired
	private CamelContext camelContext;

	@Produce(uri = "direct:sms")
	private ProducerTemplate producerTemplate;

	@EndpointInject(uri = "mock:test")
	private MockEndpoint mock;

	// private StopWatch stopWatch = new StopWatch(getClass().getSimpleName());

	@Before
	public void beforeTest() throws Exception {
		mock.reset();
	}

	@After
	public void afterTest() {

		// Stop all routes
		for (Route route : camelContext.getRoutes()) {
			try {
				camelContext.stopRoute(route.getId());
			} catch (Exception e) {
				LOG.error("Exception trying to stop de routes", e);
			}
		}
	}

	@DirtiesContext
	@Test
	public void aSimpleProducer() throws Exception {

		mock.expectedMessageCount(1);

		// Body
		SMSMessage smsMessage = new SMSMessage(null, "Hello CM", "+34600000000", null);
		producerTemplate.sendBody(smsMessage);

		mock.assertIsSatisfied();
	}
}
