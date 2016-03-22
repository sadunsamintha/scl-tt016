package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataContent;
import com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider;

public class CryptoFieldsConfig implements ICryptoFieldsConfig {

	protected FileSequenceStorageProvider fileSequenceStorageProvider;

	public DecodedCameraCode getFields(final IBSicpadataContent authenResult) {
		DecodedCameraCode decoded = new DecodedCameraCode();
		if (authenResult != null) {
			decoded.setAuthenticated(authenResult.isValid());
			decoded.setMode(authenResult.getMode());
			;
			decoded.setVersion(authenResult.getVersion());
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
