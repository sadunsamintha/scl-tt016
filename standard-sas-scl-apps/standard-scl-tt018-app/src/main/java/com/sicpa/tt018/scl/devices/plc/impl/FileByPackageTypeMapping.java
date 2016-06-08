package com.sicpa.tt018.scl.devices.plc.impl;

import static com.sicpa.tt018.interfaces.scl.master.constant.ProductPackagings.BOTTLE;
import static com.sicpa.tt018.interfaces.scl.master.constant.ProductPackagings.CAN;
import static com.sicpa.tt018.interfaces.scl.master.constant.ProductPackagings.OTHER;
import static com.sicpa.tt018.scl.remoteServer.adapter.AlbaniaRemoteServerAdapter.ALLOWED_PRODUCT_BOTTLE;
import static com.sicpa.tt018.scl.remoteServer.adapter.AlbaniaRemoteServerAdapter.ALLOWED_PRODUCT_CAN;
import static com.sicpa.tt018.scl.remoteServer.adapter.AlbaniaRemoteServerAdapter.ALLOWED_PRODUCT_CAN_BOTTLE;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt018.scl.utils.AlbaniaUtilities;

public class FileByPackageTypeMapping {

	private int allowedProductPackage;

	private final Map<Integer, String> fileByPackageType = new HashMap<>();

	public FileByPackageTypeMapping() {
		fileByPackageType.put(BOTTLE.getId(), "bottle.properties");
		fileByPackageType.put(CAN.getId(), "can.properties");
		fileByPackageType.put(OTHER.getId(), "other.properties");
	}

	public String getFileName(ProductionParameters productionParameters) {

		int productPackage = getProductPackage(productionParameters);
		return fileByPackageType.get(productPackage);
	}

	private int getProductPackage(ProductionParameters productionParameters) {

		if (allowedProductPackage == ALLOWED_PRODUCT_BOTTLE) {
			return BOTTLE.getId();
		}

		if (allowedProductPackage == ALLOWED_PRODUCT_CAN) {
			return CAN.getId();
		}

		if (allowedProductPackage == ALLOWED_PRODUCT_CAN_BOTTLE) {
			int currentPackageType = AlbaniaUtilities.getProductPackage(productionParameters);
			if (currentPackageType == BOTTLE.getId() || currentPackageType == CAN.getId()) {
				return currentPackageType;
			}
		}

		return OTHER.getId();
	}

	public void setAllowedProductPackage(int allowedProductPackage) {
		this.allowedProductPackage = allowedProductPackage;
	}
}
