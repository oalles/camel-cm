package org.apache.camel.component.cm.exceptions;

/**
 * The body has to be an instance of CMMessage. 
 *
 */
public class InvalidPayloadException extends CMValidationException {

	public InvalidPayloadException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidPayloadException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidPayloadException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidPayloadException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidPayloadException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
