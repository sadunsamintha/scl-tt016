package com.sicpa.standard.sasscl.business.coding.validator;

import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public interface IEncoderValidator {

	EncoderValidatorResult isEncoderValid(IEncoder encoder);

}
