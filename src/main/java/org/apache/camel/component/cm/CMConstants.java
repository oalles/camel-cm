package org.apache.camel.component.cm;

public interface CMConstants {

    public static String DEFAULT_SCHEME = "https://";

    public static final int MAX_UNICODE_MESSAGE_LENGTH = 70;
    public static final int MAX_GSM_MESSAGE_LENGTH = 160;
    public static final int MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART = 67;
    public static final int MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART = 153;

    // status code 200 - Error substrings - checkk it contains.
    public static final String ERROR_UNKNOWN = "Unknown error";
    public static final String ERROR_NO_ACCOUNT = "No account found";
    public static final String ERROR_INSUFICIENT_BALANCE = "Insufficient balance";
    public static final String ERROR_UNROUTABLE_MESSAGE = "Message is unroutable";
    public static final String ERROR_INVALID_PRODUCT_TOKEN = "Invalid product token";
}
