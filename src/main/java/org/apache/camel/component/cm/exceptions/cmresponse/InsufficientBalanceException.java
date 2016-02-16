package org.apache.camel.component.cm.exceptions.cmresponse;

/**
 * You are out of trial messages. Order new messages via your dashboard.
 *
 */
public class InsufficientBalanceException extends CMResponseException {

    public InsufficientBalanceException() {
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(Throwable cause) {
        super(cause);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientBalanceException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
