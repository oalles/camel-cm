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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.Assert;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class }, loader = CamelSpringDelegatingTestContextLoader.class)
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
// @DisableJmx(false)
// @MockEndpoints
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class E164Tests extends AbstractJUnit4SpringContextTests {

    // dependency: camel-spring-javaconfig

    @Autowired
    private Validator validator;

    private final PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();

    @Before
    public void beforeTest() throws Exception {
    }

    @Test
    public void testNullNumberIsValid() throws Exception {

        final String phoneNumber = null;
        final SMSMessage m = new SMSMessage("Hello world!", phoneNumber);

        final Set<ConstraintViolation<SMSMessage>> constraintViolations = validator.validate(m);
        Assert.isTrue(0 == constraintViolations.size());
    }

    @Test
    public void testE164IsValid() throws Exception {

        final String phoneNumber = "+34600000000";
        final SMSMessage m = new SMSMessage("Hello world!", phoneNumber);

        final Set<ConstraintViolation<SMSMessage>> constraintViolations = validator.validate(m);
        Assert.isTrue(0 == constraintViolations.size());
    }

    @Test
    public void testNoPlusSignedNumberIsInvalid() throws Exception {

        final String phoneNumber = "34600000000";
        final SMSMessage m = new SMSMessage("Hello world!", phoneNumber);

        final Set<ConstraintViolation<SMSMessage>> constraintViolations = validator.validate(m);
        Assert.isTrue(1 == constraintViolations.size());
    }

    @Test
    public void testNoPlusSignedNumberBut00IsInvalid() throws Exception {

        final String phoneNumber = new PhoneNumber().setCountryCodeSource(CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN).setNationalNumber(0034600000000).toString();
        final SMSMessage m = new SMSMessage("Hello world!", phoneNumber);

        final Set<ConstraintViolation<SMSMessage>> constraintViolations = validator.validate(m);
        Assert.isTrue(1 == constraintViolations.size());
    }

    @Test
    public void testNumberWithPlusSignIsInvalid() throws Exception {

        final String phoneNumber = "+34 600 00 00 00";
        final SMSMessage m = new SMSMessage("Hello world!", phoneNumber);

        final Set<ConstraintViolation<SMSMessage>> constraintViolations = validator.validate(m);
        Assert.isTrue(1 == constraintViolations.size());
    }
}
