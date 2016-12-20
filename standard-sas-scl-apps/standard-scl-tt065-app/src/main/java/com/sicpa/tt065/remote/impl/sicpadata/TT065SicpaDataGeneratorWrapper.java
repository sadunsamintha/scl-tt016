package com.sicpa.tt065.remote.impl.sicpadata;


import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.SicpaDataGeneratorWrapper;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.std.common.api.activation.visiblecode.SCLCodesFactory;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TT065SicpaDataGeneratorWrapper extends SicpaDataGeneratorWrapper {
    private static final long serialVersionUID = 1L;

    public final static String BLOCK_SEPARATOR = ">-<" ;


    private static final Logger logger = LoggerFactory.getLogger(TT065SicpaDataGeneratorWrapper.class);

    public TT065SicpaDataGeneratorWrapper(SicpadataGeneratorDto sdgen, int year, long subsystemId, ICryptoFieldsConfig cryptoFieldsConfig) {
        super(sdgen, year, subsystemId, cryptoFieldsConfig);
    }

    @Override
    public synchronized List<String> getEncryptedCodes(long numberCodes) throws CryptographyException {
        try {

            updateDateOfUse();

            long numberCodesToGenerate = Math.min(getRemainingCodes(), numberCodes);
            if (numberCodesToGenerate == 0) {
                throw new EncoderEmptyException();
            }

            List<SCLCodesFactory.SCLCode> sclCodes = SCLCodesFactory.generateSCLCodeForPrinting(getGenerator(),
                    new Object[]{new HashMap<String, Long>()}, (int) numberCodesToGenerate);
            List<String> codes = new ArrayList<>();

            for(SCLCodesFactory.SCLCode c : sclCodes) {
                codes.add(c.getCodeForPrinting(SCLCodesFactory.SCLCode.SERIAL_NUMBER_KEY) + BLOCK_SEPARATOR + c.getCodeForPrinting(SCLCodesFactory.SCLCode.HRC1_KEY));
            }

            return codes;

        } catch (SicpadataException e) {
            logger.error("Failed to generate code.", e);
            throw new CryptographyException(e, "Failed to generate encrypted code");
        }
    }
}
