package org.apache.camel.component.cm;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.camel.Exchange;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.cm.exceptions.InvalidPayloadException;
import org.apache.camel.component.cm.exceptions.ProviderHostUnavailableException;
import org.apache.camel.impl.DefaultProducer;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * is the exchange processor.
 *
 * Sends a validated sms message to CM Endpoints.
 */
public class CMProducer extends DefaultProducer {

    private Validator validator;

    /**
     * sends a valid message to CM endpoints.
     */
    private final CMSender sender;

    public CMProducer(CMEndpoint endpoint, CMSender sender) {
        super(endpoint);
        this.sender = sender;
    }

    /**
     * Producer is a exchange processor.
     *
     * This process is built in several steps.
     *
     * 1. Validate message receive from client 2. Send validated message to CM
     * endpoints. 3. Process response from CM endpoints.
     *
     */
    public void process(final Exchange exchange) {

        try {

            // TODO: 1 CMMessage to 1000CMMessages ? Will depend on CMSender
            // implementation? Can i choose CMSender impl via factory?

            // Immutable message receive from clients.
            SMSMessage smsMessage = exchange.getIn().getBody(SMSMessage.class);
            if (smsMessage == null) {
                throw new ClassCastException();
            }

            // Validate configuration
            for (ConstraintViolation<SMSMessage> cv : getValidator()
                    .validate(smsMessage)) {
                String msg = String.format("Invalid value for %s: %s",
                        cv.getPropertyPath().toString(), cv.getMessage());
                log.error(msg);
                throw new InvalidPayloadException(msg);
            }

            // We have a valid SMSMessage instance, lets extend to CMMessage
            // This is the instance we will use to build the XML document to be
            // sent to CM SMS GW.
            CMMessage cmMessage = new CMMessage(smsMessage.getPhoneNumber(),
                    smsMessage.getMessage());

            if (smsMessage.getDynamicFrom() == null
                    || smsMessage.getDynamicFrom().isEmpty()) {
                cmMessage.setDynamicSender(getConfiguration().getDefaultFrom());
            }

            // Can be null
            cmMessage.setIdAsString(smsMessage.getIdAsString());

            // Unicode and multipart
            setUnicodeAndMultipart(cmMessage);

            // 2. Send a validated sms message to CM endpoints
            // throws MessagingException
            sender.send(cmMessage);

            log.info("The request was accepted");
        } catch (ClassCastException e) {
            String m = "Check in message body - Has to be an instance of SMSMessage";
            log.error(m, e);
            exchange.setException(new InvalidPayloadException(m));
        } catch (RuntimeException e) {
            log.error("Cannot send the message ", e);
            // Body hast to be an instance of SMSMessage
            exchange.setException(new InvalidPayloadException(e));
        }
    }

    @Override
    protected void doStart() throws Exception {

        // log at debug level for singletons, for prototype scoped log at trace
        // level to not spam logs

        log.debug("Starting CMProducer");

        CMConfiguration configuration = getConfiguration();

        if (configuration.isTestConnectionOnStartup()) {
            try {
                log.debug("Checking connection - {}", getEndpoint().getCMUrl());
                HttpClientBuilder.create().build()
                        .execute(new HttpHead(getEndpoint().getCMUrl()));
                log.info("Connection to {}: OK", getEndpoint().getCMUrl());
            } catch (Exception e) {
                throw new ProviderHostUnavailableException(e);
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

    private boolean isGsm0338Encodeable(String message) {
        return message.matches(CMConstants.GSM_CHARACTERS_REGEX);
    }

    private void setUnicodeAndMultipart(CMMessage message) {

        final int defaultMaxNumberOfParts = getConfiguration()
                .getDefaultMaxNumberOfParts();

        // Set UNICODE and MULTIPART
        String msg = message.getMessage();
        if (isGsm0338Encodeable(msg)) {

            // Not Unicode is Multipart?
            if (msg.length() > CMConstants.MAX_GSM_MESSAGE_LENGTH) {

                // Multiparts. 153 caracteres max per part
                int parts = msg.length()
                        % CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART;

                message.setMultiparts((parts > defaultMaxNumberOfParts)
                        ? defaultMaxNumberOfParts : parts);
            } // Otherwise multipart = 1
        } else {
            // Unicode Message
            message.setUnicode(true);

            if (msg.length() > CMConstants.MAX_UNICODE_MESSAGE_LENGTH) {

                // Multiparts. 67 caracteres max per part
                int parts = msg.length()
                        % CMConstants.MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART;

                message.setMultiparts((parts > defaultMaxNumberOfParts)
                        ? defaultMaxNumberOfParts : parts);
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
