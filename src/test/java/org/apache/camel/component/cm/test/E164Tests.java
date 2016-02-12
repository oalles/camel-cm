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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.Assert;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class }, loader = CamelSpringDelegatingTestContextLoader.class)
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
// @DisableJmx(false)
// @MockEndpoints
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class E164Tests extends AbstractJUnit4SpringContextTests {

	// dependency: camel-spring-javaconfig

	private Logger LOG = LoggerFactory.getLogger(E164Tests.class);

	@Autowired
	private Validator validator;

	private PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();

	@Before
	public void beforeTest() throws Exception {
	}

	@Test
	public void testE164IsValid() throws Exception {

		String phoneNumber = "+34600000000";
		SMSMessage m = new SMSMessage("Hello world!", phoneNumber);

		Set<ConstraintViolation<SMSMessage>> constraintViolations = validator.validate(m);
		Assert.isTrue(0 == constraintViolations.size());
	}
	
	@Test
	public void testNoPlusSignIsInvalid() throws Exception {

		String phoneNumber = "34600000000";
		SMSMessage m = new SMSMessage("Hello world!", phoneNumber);

		Set<ConstraintViolation<SMSMessage>> constraintViolations = validator.validate(m);
		Assert.isTrue(1 == constraintViolations.size());
	}
	
	@Test
	public void testNumberWithPlusSignIsInvalid() throws Exception {

		String phoneNumber = "+34 600 00 00 00";
		SMSMessage m = new SMSMessage("Hello world!", phoneNumber);

		Set<ConstraintViolation<SMSMessage>> constraintViolations = validator.validate(m);
		Assert.isTrue(1 == constraintViolations.size());
	}
}
