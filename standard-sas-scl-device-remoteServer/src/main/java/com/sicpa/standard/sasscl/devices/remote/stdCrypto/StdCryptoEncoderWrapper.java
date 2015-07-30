package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.AbstractEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Encoder that delegates the encode process to standard crypto business encoder
 */
public class StdCryptoEncoderWrapper extends AbstractEncoder {

	private static final Logger logger = LoggerFactory.getLogger(StdCryptoEncoderWrapper.class);

	private static final long serialVersionUID = 1L;

	protected IBSicpadataGenerator encoder;

	public StdCryptoEncoderWrapper(final long batchid, final int id, final IBSicpadataGenerator encoder,
								   final int year, final long subsystemId, int codeTypeId) {
		super(batchid, id, year, subsystemId, codeTypeId);
		this.encoder = encoder;
		encoder.setId(Long.valueOf(id));
	}

	@Override
	@Deprecated
	public final String getEncryptedCode() throws CryptographyException {
		throw new CryptographyException("Deprecated");
	}

	@Override
	public synchronized List<String> getEncryptedCodes(long numberCodes) throws CryptographyException {
		try {

			updateDateOfUse();

			long numberCodesToGenerate = Math.min(getRemainingCodes(), numberCodes);
			if (numberCodesToGenerate == 0) {
				throw new EncoderEmptyException();
			}
			List<String> code = encoder.generate((int) numberCodesToGenerate);

			return code;

		} catch (SicpadataException e) {
			logger.error("Failed to generate code.", e);
			throw new CryptographyException(e, "Failed to generate encrypted code");
		}
	}

	@Override
	public synchronized boolean isEncoderEmpty() {
		return getRemainingCodes() <= 0;
	}

	public synchronized long getRemainingCodes() {
		try {
			return encoder.getRemainingCapacity();
		} catch (SicpadataException e) {
			logger.error("", e);
			return -1;
		}
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		encoder.setId(Long.valueOf(id));
	}
}
