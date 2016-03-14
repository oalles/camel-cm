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
package org.apache.camel.component.cm.test;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

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

    private final PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();

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
        final SMSMessage smsMessage = new SMSMessage("Hello CM", pnu.format(pnu.getExampleNumber("ES"), PhoneNumberFormat.E164));
        cmProxy.send(smsMessage);

        mock.assertIsSatisfied();

        camelContext.stopRoute(TestConfiguration.SIMPLE_ROUTE_ID);
    }
}
