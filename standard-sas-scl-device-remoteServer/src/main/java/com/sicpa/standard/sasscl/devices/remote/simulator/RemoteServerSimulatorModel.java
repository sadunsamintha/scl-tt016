package com.sicpa.standard.sasscl.devices.remote.simulator;

import com.sicpa.standard.sasscl.devices.remote.model.IRemoteServerModel;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;

public class RemoteServerSimulatorModel implements IRemoteServerModel {

	private ProductionParameterRootNode productionParameters;

	private int numberOfCodesByEncoder;
	private boolean useCrypto;

	public void setProductionParameters(final ProductionParameterRootNode productionParameters) {
		this.productionParameters = productionParameters;
	}

	public ProductionParameterRootNode getProductionParameters() {
		return this.productionParameters;
	}

	public void setNumberOfCodesByEncoder(int numberOfCodesByEncoder) {
		this.numberOfCodesByEncoder = numberOfCodesByEncoder;
	}

	public int getNumberOfCodesByEncoder() {
		return numberOfCodesByEncoder;
	}

	public void setUseCrypto(boolean useCrypto) {
		this.useCrypto = useCrypto;
	}

	public boolean isUseCrypto() {
		return useCrypto;
	}
}
