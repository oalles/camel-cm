package org.apache.camel.component.cm.exceptions;

/**
 * Invalid Resource URL - CM Hosts
 *
 */
public class InvalidURLException extends CMValidationException {

	public InvalidURLException() {
	}

	public InvalidURLException(String message) {
		super(message);
	}

	public InvalidURLException(Throwable cause) {
		super(cause);
	}

	public InvalidURLException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidURLException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
