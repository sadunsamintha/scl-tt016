package com.sicpa.tt016.remote.stdCrypto;

import static com.sicpa.tt016.remote.impl.sicpadata.TT016SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;
import static com.sicpa.tt016.remote.impl.sicpadata.TT016SicpaDataGeneratorWrapper.EXPORT_PREFIX;

import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;

public class TT016EncoderNoEncryptionSimulatorExport extends EncoderNoEncryptionSimulator {
	private static final long serialVersionUID = 1L;
	
	private ProductionParameters productionParameters;
	
	public TT016EncoderNoEncryptionSimulatorExport(long batchid, int id, int min, int max, int year, long subsystemId, int codeTypeId, ProductionParameters productionParameters) {
        super(batchid, id, min, max, year, subsystemId, codeTypeId);
        this.productionParameters = productionParameters;
    }

    public TT016EncoderNoEncryptionSimulatorExport(long batchid, int id, int min, int max, int year, int codeTypeId) {
        super(batchid, id, min, max, year, codeTypeId);
    }

    @Override
    public String getEncryptedCode() throws CryptographyException {
        if (this.sequence > this.max) {
            throw new EncoderEmptyException();
        } else {
            updateDateOfUse();
            
            return String.valueOf(this.sequence) + BLOCK_SEPARATOR + EXPORT_PREFIX + String.format("%06d", this.sequence++);
        }
    }
    
    public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
