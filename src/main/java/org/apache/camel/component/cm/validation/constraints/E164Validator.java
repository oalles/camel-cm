package org.apache.camel.component.cm.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource;

/**
 * Checks that a given character sequence (e.g. string) is a valid E164
 * formatted phonenumber.
 * 
 * https://www.cmtelecom.com/newsroom/how-to-format-international-telephone-
 * numbers https://github.com/googlei18n/libphonenumber
 */
public class E164Validator implements ConstraintValidator<E164, String> {

	private PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();

	@Override
	public void initialize(E164 constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if (value == null) {
			return true;
		}
		try {
			PhoneNumber parsingResult = pnu.parse(value, null);
			if (!parsingResult.getCountryCodeSource().equals(CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN))
				return false;
			if (!pnu.format(parsingResult, PhoneNumberFormat.E164).equals(value))
				return false;
			return true;
		} catch (Throwable t) {
			return false;
		}

		// CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN
		// log.debug("Phone Number: {}", value);
		// log.debug("Country code: {}", numberProto.getCountryCode());
		// log.debug("National Number: {}", numberProto.getNationalNumber());
		// log.debug("E164 format: {}", pnu.format(numberProto,
		// PhoneNumberFormat.E164));

	}

}
