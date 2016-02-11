package org.apache.camel.component.cm.test;

import org.apache.camel.component.cm.client.SMSMessage;

/**
 * 
 * Regular interface used to proxy a producer sending to the Route out to CM Direct
 * 
 * {@link http://camel.apache.org/using-camelproxy.html} 
 */
public interface CMProxy {
	public void send(SMSMessage smsMessage);
}
