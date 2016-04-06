package com.sicpa.standard.sasscl.devices.remote.datasender;

import com.sicpa.standard.sasscl.model.ProductStatus;

public interface IPackageSenderGlobal extends IPackageSender {

	void addToActivatedPackager(ProductStatus status);

	void addToCounterPackager(ProductStatus status);
}
