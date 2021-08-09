package com.sicpa.tt016.remote.stdCrypto;

import static com.sicpa.tt016.remote.impl.sicpadata.TT016SicpaDataGeneratorWrapper.EXPORT_PREFIX;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoEncoderWrapperSimulator;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;

public class TT016CryptoEncoderWrapperSimulator extends StdCryptoEncoderWrapperSimulator {
    private static final long serialVersionUID = 1L;

    private long sequence = 0;

    public TT016CryptoEncoderWrapperSimulator(long batchid, int id, IBSicpadataGenerator encoder, int year, long subsystemId, long maxCode, ICryptoFieldsConfig cryptoFieldsConfig, int codeTypeId) {
        super(batchid, id, encoder, year, subsystemId, maxCode, cryptoFieldsConfig, codeTypeId);
    }

    @Override
    public synchronized List<Pair<String, String>> getEncryptedCodesPair(long numberCodes, ProductionParameters productionParameters) throws CryptographyException {
        List<Pair<String, String>> res = super.getEncryptedCodesPair(numberCodes);

        List<Pair<String, String>> codes = new ArrayList<>();

        for(Pair<String, String> s : res) {
        	if (productionParameters.getProductionMode().equals(ProductionMode.EXPORT)) {
        		codes.add(new Pair<String, String>(s.getValue1(), EXPORT_PREFIX + String.format("%06d", sequence++)));
        	} else {
        		codes.add(new Pair<String, String>(s.getValue1(), String.format("%08d", sequence++)));
        	}
        }

        remainingCode -= res.size();
        return codes;
    }
}
