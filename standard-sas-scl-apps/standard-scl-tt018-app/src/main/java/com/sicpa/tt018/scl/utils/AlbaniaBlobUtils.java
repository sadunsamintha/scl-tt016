package com.sicpa.tt018.scl.utils;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt018.scl.model.AlbaniaSKU;
import com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode;

public class AlbaniaBlobUtils {

	private Boolean isBlobAlwaysEnable;

	private ProductionParameters productionParameters;

	public boolean isBlobDetected(final Code code) {
		return AlbaniaCameraConstants.CAMERA_ERROR_BLOB_DETECTION_CODE.equals(code);
	}

	public boolean isBlobEnable() {
		if (productionParameters.getProductionMode().equals(ProductionMode.EXPORT) || productionParameters.getProductionMode().equals(ProductionMode.MAINTENANCE) || productionParameters.getProductionMode().equals(AlbaniaProductionMode.SOFT_DRINK)) {
			return false;
		}
		AlbaniaSKU currentSKU = (AlbaniaSKU) productionParameters.getSku();
		return isBlobAlwaysEnable || currentSKU.isBlobEnabled();
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setIsBlobAlwaysEnable(Boolean isBlobAlwaysEnable) {
		this.isBlobAlwaysEnable = isBlobAlwaysEnable;
	}

}
