package com.sicpa.standard.sasscl.business.coding.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public class EncoderSubsystemIdValidator implements IEncoderValidator {

	private static final Logger logger = LoggerFactory.getLogger(EncoderSubsystemIdValidator.class);

	public static final String quarantineFilePrefix = "WRONG_LINE_ID";

	protected SubsystemIdProvider subsystemIdProvider;

	@Override
	public EncoderValidatorResult isEncoderValid(IEncoder encoder) {
		// discard current encoder, the line id is incorrect
		if (subsystemIdProvider.get() != encoder.getSubsystemId()) {
			logger.error("discarding encoder, it has an incorrect subsystemid {}", subsystemIdProvider.get());
			return EncoderValidatorResult.createInvalidResult("WRONG_SUBSYSTEM_ID");
		}
		return EncoderValidatorResult.createValidResult();
	}

	public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
		this.subsystemIdProvider = subsystemIdProvider;
	}

}
