package com.sicpa.standard.sasscl.devices.remote.impl.sicpadata;

import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoEncoderWrapper;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;

public class SicpaDataGeneratorWrapper extends StdCryptoEncoderWrapper {

	private static final long serialVersionUID = 1L;

	private SicpadataGeneratorDto generator;

	public SicpaDataGeneratorWrapper(SicpadataGeneratorDto sdgen, int year, long subsystemId,
			ICryptoFieldsConfig cryptoFieldsConfig) {
		super(sdgen.getBatchId(), sdgen.getId().intValue(), (IBSicpadataGenerator) sdgen.getSicpadataGeneratorObject(),
				year, subsystemId, cryptoFieldsConfig, sdgen.getCodeType().getId().intValue());
		generator = sdgen;
	}

	public SicpadataGeneratorDto getGenerator() {
		return generator;
	}

}
