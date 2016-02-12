package org.apache.camel.component.cm.client;

/**
 * Inmutable.
 * 
 * The message instance provided by the client.
 */
public class SMSMessage {

	/**
	 * Required
	 */
	private final String message;
	/**
	 * Required. MSISDN value starting with +. (so, don't need to set the
	 * country in parameter if my phone number begins with "+".)
	 */
	private final String phoneNumber;

	private final String dynamicFrom;
	private final String idAsString;

	public SMSMessage(String message, String phoneNumber) {
		this(null, message, phoneNumber, null);
	}

	public SMSMessage(String idAsString, String message, String phoneNumber, String dynamicFrom) {
		this.idAsString = idAsString;
		this.message = message;
		this.phoneNumber = phoneNumber;
		this.dynamicFrom = dynamicFrom;
	}

	public String getIdAsString() {
		return idAsString;
	}

	public String getMessage() {
		return message;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getDynamicFrom() {
		return dynamicFrom;
	}
}
