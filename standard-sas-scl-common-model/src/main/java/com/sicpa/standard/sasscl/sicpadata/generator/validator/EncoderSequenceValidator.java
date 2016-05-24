package com.sicpa.standard.sasscl.sicpadata.generator.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.sicpadata.generator.AbstractEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.standard.sicpadata.spi.manager.StaticServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.sequencestorage.ISequenceStorageSpi;

public class EncoderSequenceValidator implements IEncoderSequenceValidator {

	private static final Logger logger = LoggerFactory.getLogger(EncoderSequenceValidator.class);

	private String currentEncoderFolder;
	private boolean enabled;

	private final Collection<AbstractEncoder> loadEncoder() {
		Collection<AbstractEncoder> res = new ArrayList<>();

		File[] files = new File(currentEncoderFolder).listFiles();
		if (files == null) {
			return res;
		}

		for (File f : files) {
			try {
				IEncoder e = (IEncoder) ConfigUtils.load(f);
				if (e instanceof AbstractEncoder) {
					res.add((AbstractEncoder) e);
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return res;
	}

	@Override
	public void validateAndFixSequence() {
		if (!enabled) {
			return;
		}
		try {
			ISequenceStorageSpi fileSequenceStorageProvider = getStorage();

			for (AbstractEncoder encoder : loadEncoder()) {
				long encoderId = encoder.getId();
				long sequenceFromEncoder = encoder.getSequence();
				Long sequenceFromFile = fileSequenceStorageProvider.loadSequence(encoderId);
				if (isDiscrepancyDetected(sequenceFromFile, sequenceFromEncoder)) {
					// Properties file holding encoders sequences has been deleted, or sequence in properties is
					// incorrect
					logger.warn("Encoder Discrepancy detected for encoder id " + encoderId
							+ ", Stored sequence {}, Restoring sequence {}", sequenceFromFile, sequenceFromEncoder);
					fileSequenceStorageProvider.storeSequence(encoder.getId(), encoder.getSequence());
				}
			}
		} catch (ServiceProviderException e) {
			logger.error("Failed to retrieve Encoder Sequence", e);
		} catch (Exception f) {
			logger.error("Failed to cross check encoders sequences", f);
		}
	}

	private ISequenceStorageSpi getStorage() throws ServiceProviderException {
		return StaticServiceProviderManager.getInstance().getServiceProvider("provider.service.storage.sequence",
				ISequenceStorageSpi.class);
	}

	private boolean isDiscrepancyDetected(Long sequenceFromFile, long sequenceFromEncoder) {
		if (sequenceFromFile == null && sequenceFromEncoder > 0) {
			return true;
		}
		return sequenceFromEncoder != sequenceFromFile;
	}

	public void setCurrentEncoderFolder(String currentEncoderFolder) {
		this.currentEncoderFolder = currentEncoderFolder;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
