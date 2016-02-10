package org.apache.camel.component.cm.client;

/**
 * Inmutable
 */
public class SMSMessage {

	/**
	 * Required
	 */
	private final String message;
	/**
	 * Required. MSISDN value
	 */
	private final String phoneNumber;

	private String dynamicFrom;
	private String idAsString;

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

	public void setDynamicFrom(String from) {
		this.dynamicFrom = from;
	}

	public void setIdAsString(String idAsString) {
		this.idAsString = idAsString;
	}
}
