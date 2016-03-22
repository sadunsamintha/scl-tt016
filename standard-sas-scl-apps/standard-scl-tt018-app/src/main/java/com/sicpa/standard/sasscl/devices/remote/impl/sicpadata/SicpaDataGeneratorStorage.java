package com.sicpa.standard.sasscl.devices.remote.impl.sicpadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.sicpadata.generator.FileSequenceStorageProvider;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import com.sicpa.std.server.util.transfer.failover.sdgen.spi.IncomingSDGenStorageSpi;

public class SicpaDataGeneratorStorage implements IncomingSDGenStorageSpi {

	private static final Logger logger = LoggerFactory.getLogger(SicpaDataGeneratorStorage.class);

	protected IStorage storage;
	protected ICryptoFieldsConfig cryptoFieldsConfig;
	protected SubsystemIdProvider subsystemIdProvider;
	protected FileSequenceStorageProvider fileSequenceStorageProvider;

	@Override
	public void storePendingSDGen(SicpadataGeneratorDto generator) {
		int year = generator.getYear();
		IEncoder encoder = new SicpaDataGeneratorWrapper(generator, year, subsystemIdProvider.get(), cryptoFieldsConfig);
		encoder.setOnClientDate(new Date());
		storage.saveEncoders(year, encoder);

		try {
			fileSequenceStorageProvider.storeSequence(generator.getId(), generator.getLastUsedSeq());
		} catch (ServiceProviderException e) {
			logger.error("", e);
		}
	}

	@Override
	public void erasePendingSDGen(Long sdGenId) {
		storage.removePendingEncoder(sdGenId.intValue());
	}

	@Override
	public void confirmSDGen(Long sdGenId) {
		storage.confirmEncoder(sdGenId.intValue());
	}

	@Override
	public List<SicpadataGeneratorDto> getPendingGenerators() {
		return convertEncoder(storage.getPendingEncoders());
	}

	protected List<SicpadataGeneratorDto> convertEncoder(List<IEncoder> encoders) {
		List<SicpadataGeneratorDto> list = new ArrayList<SicpadataGeneratorDto>();
		for (IEncoder encoder : storage.getPendingEncoders()) {
			if (encoder instanceof SicpaDataGeneratorWrapper) {
				list.add(((SicpaDataGeneratorWrapper) encoder).getGenerator());
			}
		}
		return list;
	}

	public IStorage getStorage() {
		return storage;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public ICryptoFieldsConfig getCryptoFieldsConfig() {
		return cryptoFieldsConfig;
	}

	public void setCryptoFieldsConfig(ICryptoFieldsConfig cryptoFieldsConfig) {
		this.cryptoFieldsConfig = cryptoFieldsConfig;
	}

	public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
		this.subsystemIdProvider = subsystemIdProvider;
	}

	public void setFileSequenceStorageProvider(FileSequenceStorageProvider fileSequenceStorageProvider) {
		this.fileSequenceStorageProvider = fileSequenceStorageProvider;
	}
}
