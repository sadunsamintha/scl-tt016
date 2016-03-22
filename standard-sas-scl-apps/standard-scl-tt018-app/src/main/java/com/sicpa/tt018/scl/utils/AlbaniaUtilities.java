package com.sicpa.tt018.scl.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt018.scl.model.AlbaniaSKU;

public class AlbaniaUtilities {

	private static Logger logger = LoggerFactory.getLogger(AlbaniaUtilities.class);

	public static boolean isCurrentProductionBlobEnable(final ProductionParameters productionParameters) {
		return AlbaniaUtilities.isDomesticMode(productionParameters) && ((AlbaniaSKU) productionParameters.getSku()).isBlobEnabled();
	}

	public static int getProductPackage(final ProductionParameters productionParameter) {

		//in export mode there is no SKU
		if (productionParameter != null && productionParameter.getSku() != null) {
			return ((AlbaniaSKU) productionParameter.getSku()).getProductPackaging().getId();
		} else {
			return -1;
		}
	}

	public static boolean isEmpty(final List<?> list) {
		return list == null || list.size() == 0;
	}

	public static boolean isCountingMode(ProductionParameters productionParameter) {
		return !isDomesticMode(productionParameter);
	}

	public static boolean isDomesticMode(ProductionParameters productionParameter) {
		logger.debug("Is domestic mode = {} : Domestic = {}", productionParameter.getProductionMode(), ProductionMode.STANDARD.equals(productionParameter.getProductionMode()));

		return ProductionMode.STANDARD.equals(productionParameter.getProductionMode());
	}
}
