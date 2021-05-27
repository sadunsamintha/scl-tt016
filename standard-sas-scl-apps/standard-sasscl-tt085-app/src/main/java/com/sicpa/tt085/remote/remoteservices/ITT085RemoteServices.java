package com.sicpa.tt085.remote.remoteservices;

import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrAuthenticatedProductsResultDto;
import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrCountedProductsResultDto;
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.IRemoteServices;
import com.sicpa.std.common.api.activation.exception.ActivationException;

public interface ITT085RemoteServices extends IRemoteServices {

	void registerActivationProducts(TrAuthenticatedProductsResultDto data) throws ActivationException;

	void registerCountedProducts(TrCountedProductsResultDto trCountedProductsResultDto) throws ActivationException;

}
