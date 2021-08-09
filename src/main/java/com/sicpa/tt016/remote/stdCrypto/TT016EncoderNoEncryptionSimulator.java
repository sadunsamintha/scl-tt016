package com.sicpa.tt016.remote.stdCrypto;

import static com.sicpa.tt016.remote.impl.sicpadata.TT016SicpaDataGeneratorWrapper.EXPORT_PREFIX;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;

public class TT016EncoderNoEncryptionSimulator extends EncoderNoEncryptionSimulator {
	private static final long serialVersionUID = 1L;
	
	public TT016EncoderNoEncryptionSimulator(long batchid, int id, int min, int max, int year, long subsystemId, int codeTypeId) {
        super(batchid, id, min, max, year, subsystemId, codeTypeId);
    }

    public TT016EncoderNoEncryptionSimulator(long batchid, int id, int min, int max, int year, int codeTypeId) {
        super(batchid, id, min, max, year, codeTypeId);
    }

    @Override
    public Pair<String, String> getEncryptedCodePair(ProductionParameters productionParameters) throws CryptographyException {
        if (this.sequence > this.max) {
            throw new EncoderEmptyException();
        } else {
            updateDateOfUse();
            
            if (productionParameters.getProductionMode().equals(ProductionMode.EXPORT)) {
            	return new Pair<String, String>(String.valueOf(this.sequence), EXPORT_PREFIX + String.format("%06d", this.sequence++));
            } else {
            	return new Pair<String, String>(String.valueOf(this.sequence), String.format("%08d", this.sequence++));
            }
        }
    }
}
