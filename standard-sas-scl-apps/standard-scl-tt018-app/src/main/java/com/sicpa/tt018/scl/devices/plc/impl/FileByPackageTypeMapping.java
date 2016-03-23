package com.sicpa.tt018.scl.devices.plc.impl;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductPackagings;
import com.sicpa.tt018.scl.utils.AlbaniaUtilities;

public class FileByPackageTypeMapping {

	private static final int BOTTLE_ID = 1;
	private static final int CAN_ID = 2;
	private static final int OTHER_ID = 3;

	private final Map<Integer, String> fileByPackageType = new HashMap<>();

	public FileByPackageTypeMapping() {
		fileByPackageType.put(BOTTLE_ID, "bottle.properties");
		fileByPackageType.put(CAN_ID, "can.properties");
		fileByPackageType.put(OTHER_ID, "other.properties");
	}

	public String getFileName(ProductionParameters productionParameters) {

		int productPackage = getProductPackage(productionParameters);
		return fileByPackageType.get(productPackage);
	}

	private int getProductPackage(ProductionParameters productionParameters) {
		int currentPackageType = AlbaniaUtilities.getProductPackage(productionParameters);
		if (currentPackageType == ProductPackagings.BOTTLE.getId()
				|| currentPackageType == ProductPackagings.CAN.getId()) {
			return currentPackageType;
		} else {
			return ProductPackagings.OTHER.getId();
		}
	}

}
