package com.sicpa.tt018.scl.monitoring.mbean;

import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.monitoring.mbean.scl.SclApp;

//--------------------------------------------------------------------------- //
/**
 * Class AlbaniaSclApp extends SclApp.
 * 
 * Overriding Monitoring Bean from standard. Used to avoid null sku references.
 * 
 * @author FAspert
 */
public class AlbaniaSclApp extends SclApp {

	@Override
	public String getSKU() {
		final ProductionParameters param = stats.getProductionParameters();
		if (param != null && stats.getProductionParameters().getSku() != null) {
			return stats.getProductionParameters().getSku().toString();
		} else {
			return "";
		}
	}

}
