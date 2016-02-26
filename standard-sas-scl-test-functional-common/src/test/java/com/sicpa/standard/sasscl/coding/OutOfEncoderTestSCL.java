package com.sicpa.standard.sasscl.coding;

import org.mockito.Mockito;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.business.coding.ICoding;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class OutOfEncoderTestSCL extends AbstractFunctionnalTest {

	ICoding coding;

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}

	public void test() {
		init();
		coding = BeanProvider.getBean(BeansName.CODING);
		setProductionParameter(1, 1, ProductionMode.STANDARD);
		checkApplicationStatusCONNECTED();
		startProduction();
		checkApplicationStatusRUNNING();
		// first time it will empty all encoder
		coding.askCodes(5000000, Mockito.mock(ICodeReceiver.class));
		// next time encoder empty exception
		coding.askCodes(5000000, Mockito.mock(ICodeReceiver.class));
		runAllTasks();

		checkApplicationStatusCONNECTED();
		checkWarningMessage(MessageEventKey.Coding.ERROR_NO_ENCODERS_IN_STORAGE);
		exit();
	}

	@Override
	public void init() {
		PropertyPlaceholderResources.addProperties(BeansName.REMOTE_SERVER_SIMULATOR, RemoteSimu.class.getName());
		super.init();
	}

	public static class RemoteSimu extends RemoteServerSimulator {
		public RemoteSimu(RemoteServerSimulatorModel model) throws RemoteServerException {
			super(model);
			model.setRequestNumberOfCodes(2000);
		}
	}
}
