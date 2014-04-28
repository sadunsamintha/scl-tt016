package com.sicpa.standard.sasscl.business.postPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;

/**
 * @author DIelsch
 */
public class PostPackage implements IPostPackage {

	private static final Logger logger = LoggerFactory.getLogger(PostPackage.class);

	protected ProductionParameters productionParameters;

	// map device postpackage behavior, to be able to retrieve easily the behavior related to the device
	protected final Map<IStartableDevice, IPostPackageBehavior> postPackageByModule = new HashMap<IStartableDevice, IPostPackageBehavior>();

	protected ProductionConfigProvider productionConfigProvider;

	public PostPackage() {
	}

	@Override
	public void reset() {
		postPackageByModule.clear();
	}

	/**
	 * called by the coding module to notify which codes have been sent to the printer
	 */
	@Override
	public void provideCode(final List<String> codes, Object requestor) {
		if (!isEnabled()) {
			return;
		}
		IPostPackageBehavior behavior = getModule(requestor);
		if (behavior != null) {
			behavior.addCodes(codes);
		} else {
			logger.error("no postpackage behavior found for {}", requestor);
		}
	}

	protected IPostPackageBehavior getModule(final Object key) {
		return postPackageByModule.get(key);
	}

	@Override
	public List<Product> handleBadCode(Code code, Object receiver) {
		return getModule(receiver).handleBadCode(code);
	}

	@Override
	public List<Product> handleGoodCode(Code code, Object receiver) {
		return getModule(receiver).handleGoodCode(code);
	}

	@Override
	public boolean isEnabled() {
		return productionConfigProvider.get().getPrinterConfigs() != null
				&& !productionConfigProvider.get().getPrinterConfigs().isEmpty();
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Override
	public void registerModule(IPostPackageBehavior behavior, List<IStartableDevice> relatedDevices) {
		synchronized (postPackageByModule) {
			for (IStartableDevice key : relatedDevices) {
				postPackageByModule.put(key, behavior);
			}
		}
	}

	@Override
	public List<Product> notifyProductionStopped() {
		List<Product> res = new ArrayList<Product>();

		for (IPostPackageBehavior behavior : postPackageByModule.values()) {
			res.addAll(behavior.notifyProductionStopped());
		}

		return res;
	}

	public void setProductionConfigProvider(ProductionConfigProvider productionConfigProvider) {
		this.productionConfigProvider = productionConfigProvider;
	}
}
