package org.apache.camel.component.cm.client;

/**
 * Inmutable
 */
public class SMSMessage {

	private final String idAsString;
	/**
	 * Required
	 */
	private final String message;
	/**
	 * Required. MSISDN value
	 */
	private final String phoneNumber;
	private final String dynamicFrom;

	public SMSMessage(String idAsString, String message, String phoneNumber,
			String dynamicFrom) {
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
