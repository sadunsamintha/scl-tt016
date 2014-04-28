package com.sicpa.standard.sasscl.wizard.configaction;

import com.sicpa.standard.sasscl.wizard.context.ProjectContext;

public class AddBarcodeScanningSystemAction implements IConfigAction {

	@Override
	public void configureProject() throws ConfigurationException {

		try {

			ProjectContext.addDependency("com.sicpa.standard.sasscl", "device-brs", ProjectContext.getVersion());
			ProjectContext.addDependency("com.sicpa.standard.sasscl", "business-skucheck", ProjectContext.getVersion());

			ProjectContext.addSpringConfigItem("\"brs\"", "brs.xml");
			ProjectContext.addSpringConfigItem("\"brsPlc\"", "brsPlc.xml");
			ProjectContext.addSpringConfigItem("\"brsPlcNotifications\"", "brsPlcNotifications.xml");
			ProjectContext.addSpringConfigItem("\"brsPlcSimulatorNotifications\"", "brsPlcSimulatorNotifications.xml");
			ProjectContext.addSpringConfigItem("\"brsPlcParameters\"", "brsPlcParameters.xml");
			ProjectContext.addSpringConfigItem("\"brsPlcRequests\"", "brsPlcRequests.xml");
			ProjectContext.addSpringConfigItem("\"brsPlcVariablesDescriptors\"", "brsPlcVariablesDescriptors.xml");
			ProjectContext.addSpringConfigItem("\"brsPlcVariablesMapping\"", "brsPlcVariablesMapping.xml");
			ProjectContext.addSpringConfigItem("\"skuCheck\"", "skuCheck.xml");
			ProjectContext.addSpringConfigItem("\"skuCheckView\"", "skuCheckView.xml");

		} catch (Exception e) {
			throw new ConfigurationException(e);
		}

	}

}
