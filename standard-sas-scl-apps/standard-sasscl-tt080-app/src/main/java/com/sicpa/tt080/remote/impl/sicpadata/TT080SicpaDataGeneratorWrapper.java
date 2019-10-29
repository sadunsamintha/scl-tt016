package com.sicpa.tt080.remote.impl.sicpadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.SicpaDataGeneratorWrapper;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import com.sicpa.std.common.core.coding.business.generator.scl.SCLCodesFactory;

@Slf4j
public class TT080SicpaDataGeneratorWrapper extends SicpaDataGeneratorWrapper {

	public final static String PRINTER_SPACE_REPRESENTATION = ">-<" ;
  public static final int NUMBER_OF_BLOCKS =  2;

    public TT080SicpaDataGeneratorWrapper(final SicpadataGeneratorDto sdgen, final int year, final long subsystemId, final ICryptoFieldsConfig cryptoFieldsConfig) {
        super(sdgen, year, subsystemId, cryptoFieldsConfig);
    }

    /**
	 *
	 * Get a number of encrypted code and return it as a list of String composed like the following :
	 * 
	 * SEQUENCE>-<HRC
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
    public synchronized List<String> getEncryptedCodes(final long numberCodes) throws CryptographyException {
        try {

          final List<String> codes = new ArrayList<>();
          updateDateOfUse();

          long numberCodesToGenerate = Math.min(getRemainingCodes(), numberCodes);
          if (numberCodesToGenerate == 0) {
            throw new EncoderEmptyException();
          }

          final List<SCLCodesFactory.SCLCode> sclCodes = SCLCodesFactory.generateSCLCodeForPrinting(getGenerator(),
              new Object[]{new HashMap<String, Long>()}, (int) numberCodesToGenerate);
            

          for(SCLCodesFactory.SCLCode c : sclCodes) {
              codes.add(c.getCodeForPrinting(SCLCodesFactory.SCLCode.SERIAL_NUMBER_KEY) + PRINTER_SPACE_REPRESENTATION + c.getCodeForPrinting(SCLCodesFactory.SCLCode.HRD_KEY));
          }

          setSequence(getSequence() + codes.size());
          return codes;

        }catch (SicpadataException e) {
          log.error("Failed to generate code.", e);
          throw new CryptographyException(e, "Failed to generate encrypted code");
        }
    }
}
