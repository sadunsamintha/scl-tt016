package com.sicpa.tt016.remote.stdCrypto;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoEncoderWrapperSimulator;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;

import static com.sicpa.tt016.remote.impl.sicpadata.TT016SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;
import static com.sicpa.tt016.remote.impl.sicpadata.TT016SicpaDataGeneratorWrapper.EXPORT_PREFIX;

public class TT016CryptoEncoderWrapperSimulator extends StdCryptoEncoderWrapperSimulator {
    private static final long serialVersionUID = 1L;

    private long sequence = 0;
    private ProductionParameters productionParameters;

    public TT016CryptoEncoderWrapperSimulator(long batchid, int id, IBSicpadataGenerator encoder, int year, long subsystemId, long maxCode, ICryptoFieldsConfig cryptoFieldsConfig, int codeTypeId, ProductionParameters productionParameters) {
        super(batchid, id, encoder, year, subsystemId, maxCode, cryptoFieldsConfig, codeTypeId);
        this.productionParameters = productionParameters;
    }

    @Override
    public synchronized List<String> getEncryptedCodes(long numberCodes) throws CryptographyException {
        List<String> res = super.getEncryptedCodes(numberCodes);

        List<String> codes = new ArrayList<String>();

        for(String s : res) {
//        	if (productionParameters.getProductionMode().equals(ProductionMode.EXPORT)) {
//        		codes.add(s + BLOCK_SEPARATOR + EXPORT_PREFIX + String.format("%06d", String.valueOf(sequence++)));
//        	} else {
        		codes.add(s + BLOCK_SEPARATOR + String.format("%08d", String.valueOf(sequence++)));
//        	}
        }

        remainingCode -= res.size();
        return codes;
    }
    
    public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
