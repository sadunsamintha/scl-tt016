package com.sicpa.standard.sasscl.business.coding.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public class EncoderSubsystemIdValidator implements IEncoderValidator {

	private static final Logger logger = LoggerFactory.getLogger(EncoderSubsystemIdValidator.class);

	protected GlobalBean globalBean;
	public static final String quarantineFilePrefix = "WRONG_LINE_ID";

	@Override
	public EncoderValidatorResult isEncoderValid(IEncoder encoder) {
		// discard current encoder, the line id is incorrect
		if (globalBean.getSubsystemId() != encoder.getSubsystemId()) {
			logger.error("discarding encoder, it has an incorrect subsystemid {}", globalBean.getSubsystemId());
			return EncoderValidatorResult.createInvalidResult("WRONG_SUBSYSTEM_ID");
		}
		return EncoderValidatorResult.createValidResult();
	}

	public void setGlobalBean(GlobalBean globalBean) {
		this.globalBean = globalBean;
	}

}
