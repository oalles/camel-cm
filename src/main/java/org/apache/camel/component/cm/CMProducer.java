package org.apache.camel.component.cm;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.component.cm.client.ResponseProcessor;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.cm.client.SMSResponse;
import org.apache.camel.component.cm.exceptions.MessagingException;
import org.apache.camel.component.cm.exceptions.PayloadException;
import org.apache.camel.component.cm.exceptions.ProviderHostUnavailbleException;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMProducer extends DefaultProducer {

	private static final Logger LOG = LoggerFactory.getLogger(CMProducer.class);

	private final CMSender sender;
	private final ResponseProcessor responseProcessor;

	public CMProducer(CMEndpoint endpoint, CMSender sender,
			ResponseProcessor responseProcessor) {
		super(endpoint);
		this.sender = sender;
		this.responseProcessor = responseProcessor;
	}

	public void process(final Exchange exchange) {

		try {

			// Vamos a dar salida a CM.

			// El Body puede ser un CMMessage o una colleccion de hasta
			// 1000CMMessages.

			Object body = exchange.getIn().getBody();

			// Provider response
			SMSResponse cmResponse = null;
			if (body instanceof SMSResponse) {
				// Throws MessagingException
				cmResponse = sender.send((SMSMessage) body);
			} else {
			}

			//
			if (responseProcessor != null && cmResponse != null) {
				responseProcessor.processResponse(cmResponse);
			}

			// TODO: El envio con exito debería devolver un ID? del message? o
			// esta ya en el CMMessage

			// En caso de exito hay que asociar este envio por ejemplo a un
			// receptor.

			// TODO: Tengo en mi NotificacionesDB. idUsurario | idSMS | status
			// completado via WEBHOOK.

			// set the message ID for further processing
			// exchange.getIn().setHeader(MailConstants.MAIL_MESSAGE_ID,
			// mimeMessage.getMessageID());
		} catch (TypeConversionException e) {
			// Body hast to be an instance of CMMessage
			exchange.setException(new PayloadException(
					"Check in message body - Has to be an instance of CMMessage"));
		} catch (MessagingException e) {
			exchange.setException(e);
		}
	}

	protected void doStart() throws Exception {
		// log at debug level for singletons, for prototype scoped log at trace
		// level to not spam logs
		CMConfiguration configuration = getConfiguration();
		if (configuration.isTestConnectionOnStartup()) {
			// TODO: How to test
			throw new ProviderHostUnavailbleException();
		}

		String defaultSender = configuration.getDefaultFrom();
		if (defaultSender == null || defaultSender.isEmpty()) {
			// TODO: Obtener Default Sender de la cuenta del usuario asociado al
			// TOKEN.
			// Si no estuviera configurado
		}

		// Continuar configuracion de forma habitual.
		super.doStart();

	}

	@Override
	public CMEndpoint getEndpoint() {
		return (CMEndpoint) super.getEndpoint();
	}

	public CMConfiguration getConfiguration() {
		return getEndpoint().getConfiguration();
	}
}
