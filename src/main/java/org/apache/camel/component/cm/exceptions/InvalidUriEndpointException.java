package org.apache.camel.component.cm.exceptions;

/**
 * CM Camel component s configuration is based on URI. This exception is raised
 * when the uri provided for configuration is wrong
 *
 */
public class InvalidUriEndpointException extends CMValidationException {

	public InvalidUriEndpointException() {
		super();
	}

	public InvalidUriEndpointException(String message) {
		super(message);
	}
}
