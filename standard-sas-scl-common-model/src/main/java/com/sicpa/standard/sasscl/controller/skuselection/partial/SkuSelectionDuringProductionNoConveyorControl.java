package com.sicpa.standard.sasscl.controller.skuselection.partial;

public class SkuSelectionDuringProductionNoConveyorControl extends AbstractSkuSelectionDuringProduction {

	@Override
	public boolean isStartAutomaticWhenReady() {
		return false;
	}

}
