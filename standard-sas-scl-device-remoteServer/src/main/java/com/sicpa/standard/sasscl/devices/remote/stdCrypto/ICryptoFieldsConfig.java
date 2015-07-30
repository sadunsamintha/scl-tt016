package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataContent;

public interface ICryptoFieldsConfig {
	DecodedCameraCode getFields(final IBSicpadataContent authenResult);
}
