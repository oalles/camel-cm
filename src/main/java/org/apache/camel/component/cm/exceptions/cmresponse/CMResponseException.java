package org.apache.camel.component.cm.exceptions.cmresponse;

import org.apache.camel.component.cm.exceptions.CMDirectException;

/**
 * Excepciones en la respuestas que nos ofrece CMDirect
 * 
 *
 */
public class CMResponseException extends CMDirectException {

	public CMResponseException() {
		// TODO Auto-generated constructor stub
	}

	public CMResponseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CMResponseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public CMResponseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CMResponseException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
