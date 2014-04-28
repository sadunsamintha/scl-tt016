package com.sicpa.standard.sasscl.devices.remote.mapping;

import com.sicpa.standard.sasscl.model.ProductStatus;

public interface IRemoteServerProductStatusMapping {

	int getRemoteServerProdutcStatus(ProductStatus productStatus);

	void add(ProductStatus status, int idOnRemote);

}
