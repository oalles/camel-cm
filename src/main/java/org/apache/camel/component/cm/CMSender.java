package org.apache.camel.component.cm;

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
    void send(CMMessage cmMessage) throws MessagingException;

}
