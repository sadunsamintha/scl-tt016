package com.sicpa.tt016.view.selection.stop;

public enum StopReason {

	PRODUCT_CHANGE("stopreason.view.buttons.productchange"),
	END_OF_PRODUCTION("stopreason.view.buttons.productionend"),
	PURGE_PRODUCTION("stopreason.view.buttons.productionpurge"),
	PREVENTIVE_MAINTENANCE("stopreason.view.buttons.preventivemaintenance"),
	CORRECTIVE_MAINTENANCE("stopreason.view.buttons.correctivemaintenance");

	private final String key;

	StopReason(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
