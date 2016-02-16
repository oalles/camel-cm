package org.apache.camel.component.cm.exceptions.cmresponse;

/**
 * An unexpected error occurred. Check the provided values. Contact CM for
 * support.
 *
 */
public class UnknownErrorException extends CMResponseException {

    public UnknownErrorException() {
    }

    public UnknownErrorException(String message) {
        super(message);
    }

    public UnknownErrorException(Throwable cause) {
        super(cause);
    }

    public UnknownErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownErrorException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
