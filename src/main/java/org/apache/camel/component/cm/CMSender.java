package org.apache.camel.component.cm;

import org.apache.camel.component.cm.client.CMResponse;
import org.apache.camel.component.cm.exceptions.MessagingException;

/**
 * Sends a message to CM endpoints
 *
 */
public interface CMSender {

	/**
	 * Sends a CMMessage to CM Endpoints.
	 * 
	 */
	CMResponse send(CMMessage cmMessage) throws MessagingException;

}
