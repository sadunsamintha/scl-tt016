package com.sicpa.standard.sasscl.custom;

import static com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources.addProperties;
import static com.sicpa.standard.sasscl.ioc.BeansName.ACTIVATION;
import static com.sicpa.standard.sasscl.ioc.BeansName.CODING;
import static com.sicpa.standard.sasscl.ioc.BeansName.GLOBAL_CONFIG;
import static com.sicpa.standard.sasscl.ioc.BeansName.POST_PACKAGE;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRINTER;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRINTER_SIMULATOR;
import static com.sicpa.standard.sasscl.ioc.BeansName.SCHEDULING_REMOTE_SERVER_JOB;

import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.sasscl.business.activation.impl.ActivationWithPostPackage;
import com.sicpa.standard.sasscl.business.coding.ICoding;
import com.sicpa.standard.sasscl.business.coding.impl.Coding;
import com.sicpa.standard.sasscl.business.postPackage.PostPackage;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.scheduling.RemoteServerScheduledJobsSCL;
import com.sicpa.standard.sasscl.devices.printer.impl.PrinterAdaptor;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterSimulatorConfig;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.utils.printer.PrinterSimulatorThatProvidesCodes;

public class CustomisationTestSCL extends CustomisationTest {

	@Override
	public SpringConfig getSpringConfig() {
		SpringConfig config = new SpringConfigSCL();
		config.put(SpringConfig.OFFLINE_COUNTING, "spring/offlineCounting.xml");
		config.put(SpringConfigSCL.POST_PACKAGE, "spring/postPackage.xml");
		return config;
	}

	protected void checkBeans() {
		super.checkBeans();

		checkabean(CODING, ICoding.class);
		// checkabean(PRINTER, IPrinterAdaptor.class);
		// checkabean(PRINTER_SIMULATOR, IPrinterController.class);
		checkabean(SCHEDULING_REMOTE_SERVER_JOB, RemoteServerScheduledJobsSCL.class);

	}

	@Override
	protected void initCustomClasses() {
		super.initCustomClasses();
		addProperties(ACTIVATION, activationWithPostpackageCustom.class.getName());
		addProperties(CODING, CodingCustom.class.getName());
		addProperties(POST_PACKAGE, PostPackageCustom.class.getName());
		addProperties(PRINTER, PrinterCustom.class.getName());
		addProperties(PRINTER_SIMULATOR, PrinterSimuCustom.class.getName());
		addProperties(SCHEDULING_REMOTE_SERVER_JOB, RemoteServerScheduledJobsSCLCustom.class.getName());
	}

	// ----------------------------------------

	public static class RemoteServerScheduledJobsSCLCustom extends RemoteServerScheduledJobsSCL implements ICustom {

		public RemoteServerScheduledJobsSCLCustom(IStorage storage, IRemoteServer remoteServer,
				SkuListProvider productionParametersProvider, AuthenticatorProvider authenticatorProvider) {
			super(storage, remoteServer, productionParametersProvider, authenticatorProvider);
		}

	}

	public static class PrinterSimuCustom extends PrinterSimulatorThatProvidesCodes implements ICustom {

		public PrinterSimuCustom() {
			super();
		}

		public PrinterSimuCustom(PrinterSimulatorConfig config) {
			super(config);
		}

		public PrinterSimuCustom(String configFile) {
			super(configFile);
		}

	}

	public static class PrinterCustom extends PrinterAdaptor implements ICustom {
		public PrinterCustom() {
			super();
		}

		public PrinterCustom(IPrinterController controller) {
			super();
			setController(controller);
		}
	}

	public static class PostPackageCustom extends PostPackage implements ICustom {

	}

	public static class CodingCustom extends Coding implements ICustom {
		public CodingCustom() {
			super();
		}

		public CodingCustom(IStorage storage) {
			super(storage);
		}
	}

	public static class activationWithPostpackageCustom extends ActivationWithPostPackage implements ICustom {

		public activationWithPostpackageCustom() {
			super();
		}
	}
}
