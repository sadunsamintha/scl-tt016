package com.sicpa.tt018.scl.model.crypto;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.tt018.scl.model.encoder.AlbaniaEncoderWrapper;
import com.sicpa.tt018.scl.model.encoder.constants.AlbaniaCryptoFieldsConfigConstants;

public class AlbaniaSCLCryptoFieldsConfig extends AlbaniaCryptoFieldsConfig {

	@Override
	public Map<String, Long> getFields(IEncoder encoder) {
		final Map<String, Long> res = new HashMap<String, Long>();
		if (encoder instanceof AlbaniaEncoderWrapper) {
			res.put(AlbaniaCryptoFieldsConfigConstants.SEQUENCE, ((AlbaniaEncoderWrapper) encoder).getCurrentIndex());
			return res;
		}
		// for simulator mode
		return super.getFields(encoder);
	}

}
