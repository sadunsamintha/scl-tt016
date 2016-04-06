package com.sicpa.standard.sasscl.devices.remote.datasender;

import com.sicpa.standard.sasscl.model.PackagedProducts;

public interface IPackageSender {
	void sendPackage(PackagedProducts products) throws DataRegisteringException;
}
