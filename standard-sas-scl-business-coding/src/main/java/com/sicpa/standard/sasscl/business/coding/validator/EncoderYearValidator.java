package com.sicpa.standard.sasscl.business.coding.validator;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public class EncoderYearValidator implements IEncoderValidator {

	private static final Logger logger = LoggerFactory.getLogger(EncoderYearValidator.class);

	@Override
	public EncoderValidatorResult isEncoderValid(final IEncoder encoder) {
		if (!isYearOk(encoder)) {
			logger.error("discarding encoder, it has an incorrect year {}", encoder.getYear());
			return EncoderValidatorResult.createInvalidResult("WRONG_YEAR");
		}
		return EncoderValidatorResult.createValidResult();
	}

	protected boolean isYearOk(final IEncoder encoder) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (encoder != null && encoder.getYear() != year) {
			return false;
		}
		return true;
	}
}
