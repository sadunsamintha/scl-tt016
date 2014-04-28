package com.sicpa.standard.sasscl.devices.remote.impl;

import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.std.common.api.activation.exception.ActivationException;

public interface IPackageSender {
	void sendPackage(PackagedProducts products) throws ActivationException;
}
