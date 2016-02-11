package org.apache.camel.component.cm;

import org.apache.camel.Exchange;
import org.apache.camel.component.cm.client.ResponseProcessor;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.cm.client.SMSResponse;
import org.apache.camel.component.cm.exceptions.PayloadException;
import org.apache.camel.component.cm.exceptions.ProviderHostUnavailbleException;
import org.apache.camel.impl.DefaultProducer;

public class CMProducer extends DefaultProducer {

	private final CMSender sender;
	private final ResponseProcessor responseProcessor;

	public CMProducer(CMEndpoint endpoint, CMSender sender, ResponseProcessor responseProcessor) {
		super(endpoint);
		this.sender = sender;
		this.responseProcessor = responseProcessor;
	}

	public void process(final Exchange exchange) {

		try {

			// TODO: 1 CMMessage to 1000CMMessages ?

			SMSMessage smsMessage = exchange.getIn().getBody(SMSMessage.class);
			if (smsMessage == null)
				throw new ClassCastException();

			if (smsMessage.getDynamicFrom() == null || smsMessage.getDynamicFrom().isEmpty())
				smsMessage.setDynamicFrom(getConfiguration().getDefaultFrom());

			// throws MessagingException
			SMSResponse cmResponse = sender.send(smsMessage);

			if (responseProcessor != null && cmResponse != null) {

				responseProcessor.processResponse(cmResponse);

				// TODO: set the message ID for further processing
				// exchange.getIn().setHeader(MailConstants.MAIL_MESSAGE_ID,
				// mimeMessage.getMessageID());
			}

		} catch (ClassCastException e) {
			String m = "Check in message body - Has to be an instance of SMSMessage";
			log.error(m, e);
			exchange.setException(new PayloadException(m));
		} catch (RuntimeException e) {
			log.error("Cannot send the message ", e);
			// Body hast to be an instance of SMSMessage
			exchange.setException(new PayloadException(e));
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

		String defaultSender = configuration.getDefaultFrom();
		if (defaultSender == null || defaultSender.isEmpty()) {
			// TODO: Default Sender set in the account? Do anything?
		}

		// keep starting
		super.doStart();

		log.debug("CMProducer started");
	}

	@Override
	public CMEndpoint getEndpoint() {
		return (CMEndpoint) super.getEndpoint();
	}

	public CMConfiguration getConfiguration() {
		return getEndpoint().getConfiguration();
	}
}
