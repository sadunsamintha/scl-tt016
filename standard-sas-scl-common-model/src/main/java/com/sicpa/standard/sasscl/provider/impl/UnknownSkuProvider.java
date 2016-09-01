package com.sicpa.standard.sasscl.provider.impl;

import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;

public class UnknownSkuProvider extends AbstractProvider<SKU> {

	private SKU UNKNOWN_SKU_DOMESTIC;
	private SKU UNKNOWN_SKU_EXPORT;

	private ProductionParameters productionParameters;

	public UnknownSkuProvider(int unknownSkuIdDomestic, int unknownSkuIdExport) {
		super("unknownSku");

		UNKNOWN_SKU_DOMESTIC = new SKU(unknownSkuIdDomestic, "Unknown");
		UNKNOWN_SKU_EXPORT = new SKU(unknownSkuIdExport, "Unknown");
	}

	@Override
	public SKU get() {
		return productionParameters.getProductionMode().equals(ProductionMode.EXPORT)
				? UNKNOWN_SKU_EXPORT : UNKNOWN_SKU_DOMESTIC;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setCodeType(CodeType codeType) {
		UNKNOWN_SKU_DOMESTIC.setCodeType(codeType);
		UNKNOWN_SKU_EXPORT.setCodeType(codeType);
	}
}