package org.apache.camel.component.cm.client;

import javax.validation.constraints.Size;

import org.apache.camel.component.cm.validation.constraints.E164;

import com.sun.istack.NotNull;

/**
 * Inmutable.
 * 
 * The message instance provided by the client.
 */
public class SMSMessage {

	/**
	 * Required.
	 */
	@NotNull
	private final String message;
	/**
	 * Required MSISDN. E164 value starting with +. (so, don't need to set the
	 * country in parameter if my phone number begins with "+".)
	 */
	@E164
	private final String phoneNumber;

	@Size(min = 1, max = 11)
	private final String dynamicFrom;

	/**
	 * Restrictions: 1 - 32 alphanumeric characters and reference will not work
	 * for demo accounts.
	 */
	@Size(min = 1, max = 32)
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
