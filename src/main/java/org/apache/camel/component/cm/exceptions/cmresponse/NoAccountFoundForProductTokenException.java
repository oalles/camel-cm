package org.apache.camel.component.cm.exceptions.cmresponse;

/**
 * No account found for the provided product token.
 *
 * @author Omar
 *
 */
public class NoAccountFoundForProductTokenException
        extends CMResponseException {

    public NoAccountFoundForProductTokenException() {
    }

    public NoAccountFoundForProductTokenException(String message) {
        super(message);
    }

    public NoAccountFoundForProductTokenException(Throwable cause) {
        super(cause);
    }

    public NoAccountFoundForProductTokenException(String message,
            Throwable cause) {
        super(message, cause);
    }

    public NoAccountFoundForProductTokenException(String message,
            Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
