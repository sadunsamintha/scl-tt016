package com.sicpa.tt018.scl.remoteServer;

import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.std.common.api.activation.exception.ActivationException;

public interface IPackageSender {
	void sendPackage(PackagedProducts products) throws ActivationException;
}
