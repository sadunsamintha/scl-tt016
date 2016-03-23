package com.sicpa.tt018.scl.utils;

import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductionMode.MAINTENANCE;
import static com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode.SOFT_DRINK;
import static com.sicpa.tt018.scl.utils.AlbaniaCameraConstants.CAMERA_ERROR_BLOB_DETECTION_CODE;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt018.scl.model.AlbaniaSKU;

public class AlbaniaBlobUtils {

	private Boolean isBlobAlwaysEnable;
	private ProductionParameters productionParameters;

	public boolean isBlobDetected(final Code code) {
		return CAMERA_ERROR_BLOB_DETECTION_CODE.equals(code);
	}

	public boolean isBlobEnable() {
		ProductionMode mode = productionParameters.getProductionMode();
		if (mode.equals(EXPORT) || mode.equals(MAINTENANCE) || mode.equals(SOFT_DRINK)) {
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
