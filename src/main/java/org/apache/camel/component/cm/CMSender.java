package org.apache.camel.component.cm;

import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.cm.client.SMSResponse;
import org.apache.camel.component.cm.exceptions.MessagingException;

/**
 * Send a message to CM
 *
 */
public interface CMSender {

	/**
	 * Send SMSMessage
	 * 
	 */
	SMSResponse send(SMSMessage smsMessage) throws MessagingException;

}
