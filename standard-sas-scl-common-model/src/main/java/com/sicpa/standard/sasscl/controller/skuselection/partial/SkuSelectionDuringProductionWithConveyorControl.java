package com.sicpa.standard.sasscl.controller.skuselection.partial;

public class SkuSelectionDuringProductionWithConveyorControl extends AbstractSkuSelectionDuringProduction {

	@Override
	public boolean isStartAutomaticWhenReady() {
		return false;
	}

}
