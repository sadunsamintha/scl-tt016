package com.sicpa.standard.sasscl.sicpadata;

import com.sicpa.standard.sicpadata.spi.keystorage.FileKeyStorageProvider;
import com.sicpa.standard.sicpadata.spi.manager.SimpleServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.password.UniquePasswordProvider;
import com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider;

public class CryptoServiceProviderManager extends SimpleServiceProviderManager {

	protected UniquePasswordProvider passwordProvider;

	public CryptoServiceProviderManager(UniquePasswordProvider passwordProvider,
			FileSequenceStorageProvider fileSequenceStorageProvider) {
		this.passwordProvider = passwordProvider;
		this.register(passwordProvider);
		this.register(new FileKeyStorageProvider("./internal/simulation/storage/keystore"));
		this.register(new VolatileSicpadataModelStorage());
		this.register(fileSequenceStorageProvider);
	}

	public void setPassword(String password) {
		passwordProvider.setPassword(password);
	}
}
