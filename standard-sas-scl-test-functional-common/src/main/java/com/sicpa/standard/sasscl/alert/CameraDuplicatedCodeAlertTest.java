package com.sicpa.standard.sasscl.alert;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class CameraDuplicatedCodeAlertTest extends AbstractFunctionnalTest {

	public void test() {

		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);
		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodes();
		checkApplicationStatusCONNECTED();
		checkWarningMessage(MessageEventKey.Alert.DUPLICATED_CODE);

		exit();
	}

	public void generateCameraCodes() {
		camera.fireGoodCode("0001");
		camera.fireGoodCode("0001");
		camera.fireGoodCode("0002");
		camera.fireGoodCode("0001");
		camera.fireGoodCode("0001");
		camera.fireGoodCode("0001");
	}

	@Override
	public void init() {
		super.init();
		GlobalBean config = BeanProvider.getBean(BeansName.GLOBAL_CONFIG);
		config.getAlertModel().getDuplicatedCodeModel().setThreshold(3);
	}

}
