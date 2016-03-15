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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.camel.Exchange;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.cm.exceptions.HostUnavailableException;
import org.apache.camel.component.cm.exceptions.InvalidPayloadException;
import org.apache.camel.impl.DefaultProducer;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * is the exchange processor. Sends a validated sms message to CM Endpoints.
 */
public class CMProducer extends DefaultProducer {

    private Validator validator;

    /**
     * sends a valid message to CM endpoints.
     */
    private final CMSender sender;

    public CMProducer(final CMEndpoint endpoint, final CMSender sender) {
        super(endpoint);
        this.sender = sender;
    }

    /**
     * Producer is a exchange processor. This process is built in several steps. 1. Validate message receive from client 2. Send validated message to CM endpoints. 3. Process response from CM
     * endpoints.
     */
    @Override
    public void process(final Exchange exchange) {

        try {

            // TODO: 1 CMMessage to 1000CMMessages ? Will depend on CMSender
            // implementation? Can i choose CMSender impl via factory?

            // Immutable message receive from clients.
            final SMSMessage smsMessage = exchange.getIn().getBody(SMSMessage.class);
            if (smsMessage == null) {
                throw new NullPointerException();
            }

            // Validates Payload - SMSMessage
            final Set<ConstraintViolation<SMSMessage>> constraintViolations = getValidator().validate(smsMessage);
            if (constraintViolations.size() > 0) {
                final StringBuffer msg = new StringBuffer();
                for (final ConstraintViolation<SMSMessage> cv : constraintViolations) {
                    msg.append(String.format("- Invalid value for %s: %s", cv.getPropertyPath().toString(), cv.getMessage()));
                }
                throw new InvalidPayloadException(msg.toString());
            }

            // We have a valid (immutable) SMSMessage instance, lets extend to CMMessage
            // This is the instance we will use to build the XML document to be
            // sent to CM SMS GW.
            final CMMessage cmMessage = new CMMessage(smsMessage.getPhoneNumber(), smsMessage.getMessage());

            if (smsMessage.getDynamicFrom() == null || smsMessage.getDynamicFrom().isEmpty()) {
                cmMessage.setDynamicSender(getConfiguration().getDefaultFrom());
            }

            // Remember, this can be null.
            cmMessage.setIdAsString(smsMessage.getIdAsString());

            // Unicode and multipart
            setUnicodeAndMultipart(cmMessage);

            // 2. Send a validated sms message to CM endpoints
            // throws MessagingException for abnormal situations.
            sender.send(cmMessage);

            log.info("The request was accepted");
        } catch (final NullPointerException e) {
            // Body hast to be an instance of SMSMessage
            final String m = "Check in message body - Has to be an instance of SMSMessage";
            log.error(m, e);
            exchange.setException(new InvalidPayloadException(m));
        } catch (final RuntimeException e) {
            log.error("Cannot send the message ", e);
            exchange.setException(e);
        }
    }

    @Override
    protected void doStart() throws Exception {

        // log at debug level for singletons, for prototype scoped log at trace
        // level to not spam logs

        log.debug("Starting CMProducer");

        final CMConfiguration configuration = getConfiguration();

        if (configuration.isTestConnectionOnStartup()) {
            try {
                log.debug("Checking connection - {}", getEndpoint().getCMUrl());
                HttpClientBuilder.create().build().execute(new HttpHead(getEndpoint().getCMUrl()));
                log.info("Connection to {}: OK", getEndpoint().getCMUrl());
            } catch (final Exception e) {
                throw new HostUnavailableException(e);
            }
        }

        // keep starting
        super.doStart();

        log.info("CMProducer started");
    }

    @Override
    public CMEndpoint getEndpoint() {
        return (CMEndpoint) super.getEndpoint();
    }

    public CMConfiguration getConfiguration() {
        return getEndpoint().getConfiguration();
    }

    private void setUnicodeAndMultipart(final CMMessage message) {

        // Defaults to 8
        final int defaultMaxNumberOfParts = getConfiguration().getDefaultMaxNumberOfParts();

        // Set UNICODE and MULTIPART
        final String msg = message.getMessage();
        if (CMUtils.isGsm0338Encodeable(msg)) {

            // Not Unicode is Multipart?
            if (msg.length() > CMConstants.MAX_GSM_MESSAGE_LENGTH) {

                // Multiparts. 153 caracteres max per part
                final int parts = msg.length() % CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART;

                message.setMultiparts((parts > defaultMaxNumberOfParts) ? defaultMaxNumberOfParts : parts);
            } // Otherwise multipart = 1
        } else {
            // Unicode Message
            message.setUnicode(true);

            if (msg.length() > CMConstants.MAX_UNICODE_MESSAGE_LENGTH) {

                // Multiparts. 67 caracteres max per part
                final int parts = msg.length() % CMConstants.MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART;

                message.setMultiparts((parts > defaultMaxNumberOfParts) ? defaultMaxNumberOfParts : parts);
            } // Otherwise multipart = 1
        }
    }

    public Validator getValidator() {
        if (validator == null) {
            validator = getEndpoint().getComponent().getValidator();
        }
        return validator;
    }

}
