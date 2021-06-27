package com.sicpa.tt085.scl;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addPlcVariable;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_CONFIG_MAPPING;

import java.util.HashMap;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt085.model.TT085ProductionMode;
import com.sicpa.tt085.model.provider.CountryProvider;

public class TT085Bootstrap extends Bootstrap implements CountryProvider {

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		addrefeedExportCodingProductionMode();
	}

	@Override
	protected void restorePreviousSelectedProductionParams() {
		ProductionParameters previous = storage.getSelectedProductionParameters();
		if (productionParametersValidator.validate(previous)) {
			productionParameters.setBarcode(previous.getBarcode());
			productionParameters.setSku(previous.getSku());
			productionParameters.setProductionMode(previous.getProductionMode());
			CustoBuilder.addPropertyToClass(ProductionParameters.class, country);
			productionParameters.setProperty(country, previous.getProperty(country));
			restoreStatistics();
			EventBusService.post(new ProductionParametersEvent(previous));
		}
	}

	private void addrefeedExportCodingProductionMode() {
		IProductionConfigMapping mapping = BeanProvider.getBean(PRODUCTION_CONFIG_MAPPING);
		mapping.put(TT085ProductionMode.REFEED_EXPORT_CODING, "refeedExportCoding");
		mapping.put(TT085ProductionMode.REFEED_EXPORT_CODING_CORRECTION, "refeedExportCodingCorrection");
	}

	public static void addWiperPlcVariable() {
		addPlcVariable("PARAM_LINE_INHIBIT_WIPER", PlcUtils.LINE_PRM + "bInhibitWiper", PlcUtils.PLC_TYPE.B,
				new HashMap<String, String>() {
					{
						put("lineGrp", "wiper");
					}
				});
		addPlcVariable("PARAM_LINE_WIPER_DISTANCE", PlcUtils.LINE_PRM + "nWiperDistance", PlcUtils.PLC_TYPE.D,
				new HashMap<String, String>() {
					{
						put("lineGrp", "wiper");
					}
				});
		addPlcVariable("PARAM_LINE_WIPER_DISTANCE_TYPE", PlcUtils.LINE_PRM + "bWiperDistanceType", PlcUtils.PLC_TYPE.B,
				new HashMap<String, String>());

		addPlcVariable("PARAM_LINE_WIPER_LENGTH", PlcUtils.LINE_PRM + "nWiperLength", PlcUtils.PLC_TYPE.D,
				new HashMap<String, String>() {
					{
						put("lineGrp", "wiper");
					}
				});
		addPlcVariable("PARAM_LINE_WIPER_LENGTH_TYPE", PlcUtils.LINE_PRM + "bWiperLengthType", PlcUtils.PLC_TYPE.B,
				new HashMap<String, String>());

		addPlcVariable("PARAM_LINE_WIPER_RATIOENCODERMOTOR", PlcUtils.LINE_PRM + "nRatioEncoderMotor",
				PlcUtils.PLC_TYPE.I, new HashMap<String, String>() {
					{
						put("lineGrp", "wiper");
					}
				});
	}

}
