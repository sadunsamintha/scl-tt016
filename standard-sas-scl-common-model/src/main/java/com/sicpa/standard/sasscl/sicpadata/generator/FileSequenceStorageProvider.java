package com.sicpa.standard.sasscl.sicpadata.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;

public class FileSequenceStorageProvider extends
		com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider {

	private static final Logger logger= LoggerFactory.getLogger(FileSequenceStorageProvider.class);

	public FileSequenceStorageProvider(String modelPath) {
		super(modelPath);
	}

	@Override
	public synchronized void storeSequence(Long sicpadataGeneratorId, long sequence) throws ServiceProviderException {
		logger.info("saving encoder id:{} seq:{}", sicpadataGeneratorId, sequence);
		super.storeSequence(sicpadataGeneratorId, sequence);
	}
}
