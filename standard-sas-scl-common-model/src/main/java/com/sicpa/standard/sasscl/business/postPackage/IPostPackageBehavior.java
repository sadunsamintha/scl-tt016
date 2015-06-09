package com.sicpa.standard.sasscl.business.postPackage;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;

import java.util.List;

public interface IPostPackageBehavior {
	List<Product> handleGoodCode(final Code code);

	List<Product> handleBadCode(final Code code);

	void addCodes(List<String> codes);

	List<Product> notifyProductionStopped();

	void setAssosiatedCamera(String cameraName);
}
