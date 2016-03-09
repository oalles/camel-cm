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

    public static final String GSM_CHARACTERS_REGEX = "^[A-Za-z0-9 \\r\\n@£$¥èéùìòÇØøÅå\u0394_\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039EÆæßÉ!\"#$%&amp;'()*+,\\-./:;&lt;=&gt;?¡ÄÖÑÜ§¿äöñüà^{}\\\\\\[~\\]|\u20AC]*$";
}
