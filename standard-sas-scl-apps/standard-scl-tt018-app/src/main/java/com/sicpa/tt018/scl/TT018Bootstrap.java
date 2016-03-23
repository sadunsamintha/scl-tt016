package com.sicpa.tt018.scl;

import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_CONFIG_MAPPING;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductionMode.MAINTENANCE;
import static com.sicpa.standard.sasscl.model.ProductionMode.STANDARD;
import static com.sicpa.tt018.scl.business.activation.constants.AlbaniaSCLActivationMessages.EXCEPTION_CODE_IN_SOFT_DRINK;
import static com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode.SOFT_DRINK;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils.PLC_TYPE;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.tt018.scl.model.AlbaniaProductStatus;
import com.sicpa.tt018.scl.model.productionParameters.AlbaniaPermission;

public class TT018Bootstrap extends Bootstrap {

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		messageCusto();
		addSoftDrinkProductionMode();
		statisticsCusto();
		setProductionModePermission();
	}

	private void setProductionModePermission() {
		CustoBuilder.setProductionModePermission(STANDARD, SasSclPermission.PRODUCTION_MODE_STANDARD);
		CustoBuilder.setProductionModePermission(MAINTENANCE, SasSclPermission.PRODUCTION_MODE_MAINTENANCE);
		CustoBuilder.setProductionModePermission(EXPORT, SasSclPermission.PRODUCTION_MODE_STANDARD);
		CustoBuilder.setProductionModePermission(SOFT_DRINK, AlbaniaPermission.PRODUCTION_MODE_STANDARD);
	}

	private void messageCusto() {
		CustoBuilder.addMessage(EXCEPTION_CODE_IN_SOFT_DRINK, "[ACT_05]", ERROR);
	}

	private void statisticsCusto() {
		CustoBuilder.addToStatisticsMapper(AlbaniaProductStatus.SENT_TO_PRINTER_BLOB, StatisticsKey.GOOD);
		CustoBuilder.addToStatisticsMapper(AlbaniaProductStatus.SOFT_DRINK, StatisticsKey.GOOD);
	}

	public static void addCustoPlcVariable() {

		Map<String, Object> options = new HashMap<>();
		options.put("lineGrp", "system");

		CustoBuilder.addPlcVariable("PARAM_LINE_SENSOR_TYPE", ".com.stLine[#1].stParameters.nPackageType", PLC_TYPE.S,
				options);
	}

	public static void addSoftDrinkProductionMode() {
		IProductionConfigMapping mapping = BeanProvider.getBean(PRODUCTION_CONFIG_MAPPING);
		mapping.put(SOFT_DRINK, "softdrink");
	}

}
