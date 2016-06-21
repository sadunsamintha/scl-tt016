package com.sicpa.tt016.scl.camera;

import java.util.Collection;
import java.util.Collections;

import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.provider.ICameraJobParametersProvider;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type.ICameraJobParameter;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;

public class TT016CameraJobParametersProvider implements ICameraJobParametersProvider {

	private ProductionParameters productionParameters;

	@Override
	public Collection<ICameraJobParameter> getParameterList() {
		SKU sku = productionParameters.getSku();
		// sku.getProperty(brand)
		// based on sku set a parameter in the camera job
		return Collections.emptyList();
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
