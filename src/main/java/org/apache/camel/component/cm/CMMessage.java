package org.apache.camel.component.cm;

/**
 * 
 * Valid message to serialized and sent to CM Endpoints.
 * 
 * Has to guarantee CM contracts.
 * 
 * If the message only uses GSM 7-bit characters, then 160 characters will fit
 * in 1 SMS part, and 153*n characters will fit in n SMS parts for n>1.
 * 
 * If the message contains other characters, then only 70 characters will fit in
 * 1 SMS part, and 67*n characters will fit in n SMS parts for n>1.
 * 
 * <br>
 * <br>
 * {@link https://dashboard.onlinesmsgateway.com/docs} <br>
 * {@link http://support.telerivet.com/customer/portal/articles/1957426-multipart-
 * unicode-sms-messages}
 *
 */

public class CMMessage {

	private String phoneNumber;
	private String message;

	/**
	 * Restrictions: 1 - 32 alphanumeric characters and reference will not work
	 * for demo accounts
	 * 
	 */
	// TODO: Allow using an ID generator?
	private String idAsString;
	private String dynamicSender;

	private boolean unicode = false;
	private int multipart = 1;

	public CMMessage(String phoneNumber, String message) {
		this.message = message;
		this.phoneNumber = phoneNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDynamicSender() {
		return dynamicSender;
	}

	public void setDynamicSender(String dynamicSender) {
		this.dynamicSender = dynamicSender;
	}

	public String getIdAsString() {
		return idAsString;
	}

	public void setIdAsString(String idAsString) {
		this.idAsString = idAsString;
	}

	public boolean isUnicode() {
		return unicode;
	}

	public void setUnicode(boolean unicode) {
		this.unicode = unicode;
	}

	public boolean isMultipart() {
		return multipart > 1;
	}

	public void setMultiparts(int multipart) {
		this.multipart = multipart;
	}

	public int getMultiparts() {
		return this.multipart;
	}
}
