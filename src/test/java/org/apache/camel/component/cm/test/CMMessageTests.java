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

import org.apache.camel.component.cm.CMConstants;
import org.apache.camel.component.cm.CMMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ValidatorConfiguration.class })
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
// @DisableJmx(false)
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CMMessageTests extends AbstractJUnit4SpringContextTests {

    @Before
    public void beforeTest() throws Exception {
    }

    // @After
    // public void afterTest() {

    /*
     * GSM0338
     */

    @Test
    public void testGSM338AndLTMAX_GSM_MESSAGE_LENGTH() throws Exception {

        // 0338 and less than 160 char -> 1 part

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < CMConstants.MAX_GSM_MESSAGE_LENGTH; index++) {
            message.append("a");
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 1);
        Assert.isTrue(!cmMessage.isUnicode());
    }

    @Test
    public void testGSM338AndEQMAX_GSM_MESSAGE_LENGTH() throws Exception {
        // 0338 and length is exactly 160 -> 1 part

        StringBuffer message = new StringBuffer();
        for (int index = 0; (index < CMConstants.MAX_GSM_MESSAGE_LENGTH); index++) {
            message.append("a");
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 1);
        Assert.isTrue(!cmMessage.isUnicode());
    }

    @Test
    public void testGSM338AndGTMAX_GSM_MESSAGE_LENGTH() throws Exception {

        // 0338 and length is exactly 161 -> 2 part

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < CMConstants.MAX_GSM_MESSAGE_LENGTH + 1; index++) {
            message.append("a");
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 2);
        Assert.isTrue(!cmMessage.isUnicode());
    }

    @Test
    public void testGSM338AndLT2MAX_GSM_MESSAGE_LENGTH() throws Exception {

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (2 * CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART - 1); index++) {
            message.append("a");
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 2);
        Assert.isTrue(!cmMessage.isUnicode());
    }

    @Test
    public void testGSM338AndEQ2MAX_GSM_MESSAGE_LENGTH() throws Exception {

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (2 * CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART); index++) {
            message.append("a");
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 2);
        Assert.isTrue(!cmMessage.isUnicode());
    }

    @Test
    public void testGSM338AndGT2MAX_GSM_MESSAGE_LENGTH() throws Exception {

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (2 * CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART + 1); index++) {
            message.append("a");
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 3);
        Assert.isTrue(!cmMessage.isUnicode());
    }

    @Test
    public void testGSM338AndEQ8MAX_GSM_MESSAGE_LENGTH() throws Exception {

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (8 * CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART); index++) {
            message.append("a");
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 8);
        Assert.isTrue(!cmMessage.isUnicode());
    }

    @Test
    public void testGSM338AndGT8MAX_GSM_MESSAGE_LENGTH() throws Exception {

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (8 * CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART + 1); index++) {
            message.append("a");
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 8);
        Assert.isTrue(!cmMessage.isUnicode());
    }

    /*
     * Unicode Messages
     */

    @Test
    public void testUnicodeAndLTMAX_GSM_MESSAGE_LENGTH() throws Exception {

        String ch = "\uF400";

        // 0338 and less than 160 char -> 1 part

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < CMConstants.MAX_UNICODE_MESSAGE_LENGTH; index++) {
            message.append(ch);
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 1);
        Assert.isTrue(cmMessage.isUnicode());
    }

    @Test
    public void testUnicodeAndEQMAX_GSM_MESSAGE_LENGTH() throws Exception {
        // 0338 and length is exactly 160 -> 1 part

        String ch = "\uF400";

        StringBuffer message = new StringBuffer();
        for (int index = 0; (index < CMConstants.MAX_UNICODE_MESSAGE_LENGTH); index++) {
            message.append(ch);
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 1);
        Assert.isTrue(cmMessage.isUnicode());
    }

    @Test
    public void testUnicodeAndGTMAX_GSM_MESSAGE_LENGTH() throws Exception {

        // 0338 and length is exactly 161 -> 2 part

        String ch = "\uF400";

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < CMConstants.MAX_UNICODE_MESSAGE_LENGTH + 1; index++) {
            message.append(ch);
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 2);
        Assert.isTrue(cmMessage.isUnicode());
    }

    @Test
    public void testUnicodeAndLT2MAX_GSM_MESSAGE_LENGTH() throws Exception {

        String ch = "\uF400";

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (2 * CMConstants.MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART - 1); index++) {
            message.append(ch);
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 2);
        Assert.isTrue(cmMessage.isUnicode());
    }

    @Test
    public void testUnicodeAndEQ2MAX_GSM_MESSAGE_LENGTH() throws Exception {

        String ch = "\uF400";

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (2 * CMConstants.MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART); index++) {
            message.append(ch);
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 2);
        Assert.isTrue(cmMessage.isUnicode());
    }

    @Test
    public void testUnicodeAndGT2MAX_GSM_MESSAGE_LENGTH() throws Exception {

        String ch = "\uF400";

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (2 * CMConstants.MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART + 1); index++) {
            message.append(ch);
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 3);
        Assert.isTrue(cmMessage.isUnicode());
    }

    @Test
    public void testUnicodeAndEQ8MAX_GSM_MESSAGE_LENGTH() throws Exception {

        String ch = "\uF400";

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (8 * CMConstants.MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART); index++) {
            message.append(ch);
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 8);
        Assert.isTrue(cmMessage.isUnicode());
    }

    @Test
    public void testUnicodeAndGT8MAX_GSM_MESSAGE_LENGTH() throws Exception {

        String ch = "\uF400";

        StringBuffer message = new StringBuffer();
        for (int index = 0; index < (8 * CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART + 1); index++) {
            message.append(ch);
        }

        String phoneNumber = "+34600000000";

        final CMMessage cmMessage = new CMMessage(phoneNumber, message.toString());
        cmMessage.setUnicodeAndMultipart(CMConstants.DEFAULT_MULTIPARTS);

        Assert.isTrue(cmMessage.getMultiparts() == 8);
        Assert.isTrue(cmMessage.isUnicode());
    }

}
