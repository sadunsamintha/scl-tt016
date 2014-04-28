package com.sicpa.standard.sasscl.business.postPackage;

import java.util.List;

import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;

/**
 * track the codes sent to the printer and the codes received by the camera
 * 
 * @author DIelsch
 * 
 */
public interface IPostPackage extends ICodeReceiver {

	List<Product> handleGoodCode(Code code, Object receiver);

	List<Product> handleBadCode(Code code, Object receiver);

	void registerModule(IPostPackageBehavior behavior, List<IStartableDevice> relatedDevices);

	boolean isEnabled();

	List<Product> notifyProductionStopped();
	
	void reset();

}
