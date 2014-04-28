package com.sicpa.standard.sasscl.business.postPackage;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.hardware.ProductionDevicesCreatedEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.IImplementationProvider;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;

public class PostPackageAutoRegistering {

	protected IPostPackage postPackage;

	protected ProductionConfigProvider productionConfigProvider;

	protected Collection<IStartableDevice> devices;

	protected IImplementationProvider implementationProvider;

	@Subscribe
	public void handleDevicesCreated(ProductionDevicesCreatedEvent evt) {
		devices = evt.getDevices();
	}

	@Subscribe
	public void handleApplicationStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(ApplicationFlowState.STT_CONNECTING)) {
			registerAllModules();
		}
	}

	protected void registerAllModules() {
		if (!postPackage.isEnabled()) {
			return;
		}
		Map<String, IStartableDevice> mapDevices = new HashMap<String, IStartableDevice>();

		for (IStartableDevice dev : devices) {
			mapDevices.put(dev.getName(), dev);
		}

		for (PrinterConfig pc : productionConfigProvider.get().getPrinterConfigs()) {
			IStartableDevice printer = mapDevices.get(pc.getId());
			IStartableDevice camera = mapDevices.get(pc.getValidatedBy());
			registerAModule(camera, printer, pc);
		}
	}

	protected void registerAModule(IStartableDevice camera, IStartableDevice printer, PrinterConfig pc) {
		IPostPackageBehavior packageBehavior = createPostPackageBehavior();
		packageBehavior.setAssosiatedCamera(pc.getValidatedBy());
		postPackage.registerModule(packageBehavior, Arrays.asList(camera, printer));
	}

	protected IPostPackageBehavior createPostPackageBehavior() {
		return (IPostPackageBehavior) implementationProvider.getImplementation("postPackageBehavior");
	}

	public void setPostPackage(IPostPackage postPackage) {
		this.postPackage = postPackage;
	}

	public void setImplementationProvider(IImplementationProvider implementationProvider) {
		this.implementationProvider = implementationProvider;
	}

	public void setProductionConfigProvider(ProductionConfigProvider productionConfigProvder) {
		this.productionConfigProvider = productionConfigProvder;
	}
}
