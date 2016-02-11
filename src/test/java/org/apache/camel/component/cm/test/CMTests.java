package org.apache.camel.component.cm.test;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class }, loader = CamelSpringDelegatingTestContextLoader.class)
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
// @DisableJmx(false)
// @MockEndpoints
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CMTests extends AbstractJUnit4SpringContextTests {

	// dependency: camel-spring-javaconfig

	@Autowired
	private CamelContext camelContext;

	@Produce(uri = "direct:sms")
	private CMProxy cmProxy;

	@EndpointInject(uri = "mock:test")
	private MockEndpoint mock;

	// private StopWatch stopWatch = new StopWatch(getClass().getSimpleName());

	@Before
	public void beforeTest() throws Exception {
		mock.reset();
	}

	// @After
	// public void afterTest() {

	// Stop all routes
	// for (Route route : camelContext.getRoutes()) {
	// try {
	// camelContext.stopRoute(route.getId());
	// } catch (Exception e) {
	// logger.error("Exception trying to stop de routes", e);
	// }
	// }
	// }

	// @DirtiesContext
	@Test
	public void testAsPartOfARoute() throws Exception {

		mock.expectedMessageCount(1);

		camelContext.startRoute(TestConfiguration.SIMPLE_ROUTE_ID);

		// Body
		SMSMessage smsMessage = new SMSMessage(null, "Hello CM", "+34600000000", null);
		cmProxy.send(smsMessage);

		mock.assertIsSatisfied();

		camelContext.stopRoute(TestConfiguration.SIMPLE_ROUTE_ID);
	}
}
