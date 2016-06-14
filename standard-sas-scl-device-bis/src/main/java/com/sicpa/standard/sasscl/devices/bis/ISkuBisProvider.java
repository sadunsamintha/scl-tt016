package com.sicpa.standard.sasscl.devices.bis;

import java.util.Collection;

import com.sicpa.standard.sasscl.model.SKU;

public interface ISkuBisProvider {

	Collection<SKU> getSkusToSendToBIS();

}
