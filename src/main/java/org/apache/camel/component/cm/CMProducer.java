package org.apache.camel.component.cm;

import java.nio.charset.CharsetEncoder;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.camel.Exchange;
import org.apache.camel.component.cm.client.CMResponse;
import org.apache.camel.component.cm.client.ResponseProcessor;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.cm.exceptions.InvalidPayloadException;
import org.apache.camel.component.cm.exceptions.ProviderHostUnavailbleException;
import org.apache.camel.impl.DefaultProducer;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import net.freeutils.charset.CCGSMCharset;

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

	/**
	 * processes the response from CM Endpoints.
	 */
	private final ResponseProcessor responseProcessor;

	private CharsetEncoder encoder = CCGSMCharset.forName("CCGSM").newEncoder();
	private PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();

	public CMProducer(CMEndpoint endpoint, CMSender sender, ResponseProcessor responseProcessor) {
		super(endpoint);
		this.sender = sender;
		this.responseProcessor = responseProcessor;
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
			if (smsMessage == null)
				throw new ClassCastException();

			// Validate configuration
			for (ConstraintViolation<SMSMessage> cv : getValidator().validate(smsMessage)) {
				String msg = String.format("Invalid value for %s: %s", cv.getPropertyPath().toString(),
						cv.getMessage());
				log.error(msg);
				throw new InvalidPayloadException(msg);
			}

			// phone must begin with '+'. don't need to set the country in
			// parameter.
			PhoneNumber numberProto = pnu.parse(smsMessage.getPhoneNumber(), null);
			log.debug("Phone Number: {}", smsMessage.getPhoneNumber());
			log.debug("Country code: {}", numberProto.getCountryCode());
			log.debug("National Number: {}", numberProto.getNationalNumber());
			log.debug("E164 format: {}", pnu.format(numberProto, PhoneNumberFormat.E164));

			// We have a valid SMSMessage instance, lets extend to CMMessage
			// This is the instance we will use to build the XML document to be
			// sent to CM SMS GW.
			CMMessage cmMessage = new CMMessage(smsMessage.getPhoneNumber(), smsMessage.getMessage());

			if (smsMessage.getDynamicFrom() == null || smsMessage.getDynamicFrom().isEmpty())
				cmMessage.setDynamicSender(getConfiguration().getDefaultFrom());

			// Can be null
			cmMessage.setIdAsString(smsMessage.getIdAsString());

			// Unicode and multipart
			this.setUnicodeAndMultipart(cmMessage);

			// 2. Send a validated sms message to CM endpoints
			// throws MessagingException
			CMResponse cmResponse = sender.send(cmMessage);

			// 3. Process the response.
			if (responseProcessor != null && cmResponse != null) {
				responseProcessor.processResponse(cmResponse);

				// TODO: set the message ID for further processing
				// exchange.getIn().setHeader(MailConstants.MAIL_MESSAGE_ID,
				// mimeMessage.getMessageID());
			}

		} catch (ClassCastException e) {
			String m = "Check in message body - Has to be an instance of SMSMessage";
			log.error(m, e);
			exchange.setException(new InvalidPayloadException(m));
		} catch (NumberParseException e) {
			log.error("NumberParseException was thrown: " + e.toString());
			exchange.setException(new InvalidPayloadException(e));
		} catch (RuntimeException e) {
			log.error("Cannot send the message ", e);
			// Body hast to be an instance of SMSMessage
			exchange.setException(new InvalidPayloadException(e));
		}
	}

	protected void doStart() throws Exception {

		// log at debug level for singletons, for prototype scoped log at trace
		// level to not spam logs

		log.debug("Starting CMProducer");

		CMConfiguration configuration = getConfiguration();

		if (configuration.isTestConnectionOnStartup()) {
			// TODO: How to test? Does it make sense?
			throw new ProviderHostUnavailbleException();
		}

		// TODO: Entiendo que deberia fijar este valor donde haga la extension
		// de SMSMessage a CMMessage (To be serialized)
		// String defaultSender = configuration.getDefaultFrom();
		// if (defaultSender == null || defaultSender.isEmpty()) {
		// // TODO: Default Sender set in the account? Do anything?
		// }

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
		return encoder.canEncode(message);
	}

	private void setUnicodeAndMultipart(CMMessage message) {

		final int defaultMaxNumberOfParts = getConfiguration().getDefaultMaxNumberOfParts();

		// Set UNICODE and MULTIPART
		String msg = message.getMessage();
		if (isGsm0338Encodeable(msg)) {

			// Not Unicode is Multipart?
			if (msg.length() > CMConstants.MAX_GSM_MESSAGE_LENGTH) {

				// Multiparts. 153 caracteres max per part
				int parts = msg.length() % CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART;

				message.setMultiparts((parts > defaultMaxNumberOfParts) ? defaultMaxNumberOfParts : parts);
			} // Otherwise multipart = 1
		} else {
			// Unicode Message
			message.setUnicode(true);

			if (msg.length() > CMConstants.MAX_UNICODE_MESSAGE_LENGTH) {

				// Multiparts. 67 caracteres max per part
				int parts = msg.length() % CMConstants.MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART;

				message.setMultiparts((parts > defaultMaxNumberOfParts) ? defaultMaxNumberOfParts : parts);
			} // Otherwise multipart = 1
		}
	}

	public Validator getValidator() {
		if (validator == null)
			validator = getEndpoint().getComponent().getValidator();
		return validator;
	}
}
