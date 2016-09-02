package com.sicpa.standard.sasscl.provider.impl;

import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.model.SKU;

public class UnknownSkuProvider extends AbstractProvider<SKU> {

	public static final int UNKNOWN_SKU_ID = -1;

	public UnknownSkuProvider() {
		super("unknownSku");
		set(new SKU(UNKNOWN_SKU_ID, "unknown"));
	}
	@Override
	public SKU get() {
		return super.get();
	}
}