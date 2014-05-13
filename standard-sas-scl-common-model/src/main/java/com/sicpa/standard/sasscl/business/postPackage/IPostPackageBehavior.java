package com.sicpa.standard.sasscl.business.postPackage;

import java.util.List;

import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;

public interface IPostPackageBehavior {
	List<Product> handleGoodCode(final Code code);

	List<Product> handleBadCode(final Code code);

	void addCodes(List<String> codes);

	List<Product> notifyProductionStopped();

	void setAssosiatedCamera(String cameraName);

	void addExtendedCodes(List<ExtendedCode> codes);
}
