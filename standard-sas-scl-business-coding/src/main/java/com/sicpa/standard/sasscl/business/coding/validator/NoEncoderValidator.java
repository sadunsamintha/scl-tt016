package com.sicpa.standard.sasscl.business.coding.validator;

import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public class NoEncoderValidator implements IEncoderValidator {

	@Override
	public EncoderValidatorResult isEncoderValid(IEncoder encoder) {
		return EncoderValidatorResult.createValidResult();
	}

}
