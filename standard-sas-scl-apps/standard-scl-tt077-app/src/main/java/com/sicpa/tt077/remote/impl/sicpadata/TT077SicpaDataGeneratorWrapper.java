package com.sicpa.tt077.remote.impl.sicpadata;

import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.SicpaDataGeneratorWrapper;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import com.sicpa.std.common.core.coding.business.generator.scl.SCLCodesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class TT077SicpaDataGeneratorWrapper extends SicpaDataGeneratorWrapper {

	
	public final static String BLOCK_SEPARATOR = ">-<" ;


    private static final Logger logger = LoggerFactory.getLogger(TT077SicpaDataGeneratorWrapper.class);

    public TT077SicpaDataGeneratorWrapper(SicpadataGeneratorDto sdgen, int year, long subsystemId, ICryptoFieldsConfig cryptoFieldsConfig) {
        super(sdgen, year, subsystemId, cryptoFieldsConfig);
    }

    /**
	 *
	 * Get a number of encrypted code and return it as a list of String composed like the following :
	 * 
	 * SERIAL_NUMBER>-<HRD_CODE
	 *
	 * If the encoder does not have enough code, all the available codes will be returned
	 *
	 * @param numberCodes
	 *            number of encrypted code to request
	 *
	 *
	 * @return list of encrypted code and their respective HRC
	 *
	 * @throws CryptographyException
	 *             thrown exception if the encoder is empty
	 */
    @Override
    public synchronized List<String> getEncryptedCodes(long numberCodes) throws CryptographyException {
        try {
        	List<String> codes = new ArrayList<>();
            updateDateOfUse();

            long numberCodesToGenerate = Math.min(getRemainingCodes(), numberCodes);
            if (numberCodesToGenerate == 0) {
                throw new EncoderEmptyException();
            }

            List<SCLCodesFactory.SCLCode> sclCodes = SCLCodesFactory.generateSCLCodeForPrinting(getGenerator(),
                    new Object[]{new HashMap<String, Long>()}, (int) numberCodesToGenerate);
            

            for(SCLCodesFactory.SCLCode c : sclCodes) {
                codes.add(c.getCodeForPrinting(SCLCodesFactory.SCLCode.SERIAL_NUMBER_KEY) + BLOCK_SEPARATOR + c.getCodeForPrinting(SCLCodesFactory.SCLCode.HRD_KEY));
            }

            setSequence(getSequence() + codes.size());

            return codes;

      } catch (SicpadataException e) {
            logger.error("Failed to generate code.", e);
            throw new CryptographyException(e, "Failed to generate encrypted code");
        }
    }
}
