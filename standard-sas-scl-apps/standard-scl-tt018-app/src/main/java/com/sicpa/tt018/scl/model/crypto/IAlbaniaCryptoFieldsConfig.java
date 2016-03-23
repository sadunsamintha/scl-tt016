package com.sicpa.tt018.scl.model.crypto;

import java.util.Map;

import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataContent;

public interface IAlbaniaCryptoFieldsConfig extends ICryptoFieldsConfig {

	Map<String, Long> getFields(final IEncoder encoder);
}
