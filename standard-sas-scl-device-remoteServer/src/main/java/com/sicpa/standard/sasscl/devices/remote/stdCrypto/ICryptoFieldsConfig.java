package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import java.util.Map;

import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataContent;

public interface ICryptoFieldsConfig {
	Map<String, Long> getFields(final IEncoder encoder);

	DecodedCameraCode getFields(final IBSicpadataContent authenResult);
}
