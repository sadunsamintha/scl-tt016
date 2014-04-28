package com.sicpa.standard.sasscl.sicpadata;

import com.sicpa.standard.sicpadata.api.model.SicpadataModel;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.standard.sicpadata.spi.modelstorage.ISicpadataModelStorageSpi;

public class VolatileSicpadataModelStorage implements ISicpadataModelStorageSpi {
	private SicpadataModel sicpadataModel;
	
	@Override
	public String getServiceKey() {
		return SERVICE_KEY;
	}

	@Override
	public SicpadataModel loadModel() throws ServiceProviderException {
		return sicpadataModel;
	}

	@Override
	public void storeModel(SicpadataModel sicpadataModel) throws ServiceProviderException {
		 this.sicpadataModel = sicpadataModel;
	}

	@Override
	public String toString() {
		return "VOLATILE SICPADATA MODEL STORAGE";
	}
}
