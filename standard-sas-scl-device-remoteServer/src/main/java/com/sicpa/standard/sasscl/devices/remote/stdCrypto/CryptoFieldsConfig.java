package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataContent;
import com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider;

public class CryptoFieldsConfig implements ICryptoFieldsConfig {

	protected FileSequenceStorageProvider fileSequenceStorageProvider;

	public Map<String, Long> getFields(final IEncoder encoder) {
		Map<String, Long> res = new HashMap<String, Long>();
		// it is now the crypto itself that take care of the sequence
		// if (encoder instanceof StdCryptoEncoderWrapper) {
		// res.put("sequence", ((StdCryptoEncoderWrapper) encoder).getSequence());
		// } else {
		// throw new IllegalArgumentException("encoder have to be an instanceof StdCryptoEncoderWrapper currently is:"
		// + encoder.getClass());
		// }
		return res;
	}

	public DecodedCameraCode getFields(final IBSicpadataContent authenResult) {
		DecodedCameraCode decoded = new DecodedCameraCode();
		if (authenResult != null) {
			decoded.setAuthenticated(authenResult.isValid());
			if (authenResult.isValid()) {
				if (authenResult.getFields().get("batchId") != null) {
					decoded.setBatchId(authenResult.getFields().get("batchId"));
				}
				if (authenResult.getFields().get("codeType") != null) {
					decoded.setCodeType(new CodeType(authenResult.getFields().get("codeType")));
				}
				if (authenResult.getFields().get("sequence") != null) {
					decoded.setSequence(authenResult.getFields().get("sequence"));
				}
			}
		}
		return decoded;
	}

}
