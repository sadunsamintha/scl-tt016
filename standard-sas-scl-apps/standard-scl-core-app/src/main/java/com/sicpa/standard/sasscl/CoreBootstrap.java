package com.sicpa.standard.sasscl;


public class CoreBootstrap extends Bootstrap {

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		
		// CustoBuilder.Devices.Plc.addPlcParameterVariableBoolean("cabinet.mygroup", ".plc.param.newBoolVar");
	}

}
