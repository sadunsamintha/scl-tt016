package com.sicpa.tt018.scl;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addActionOnStartingProduction;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addMessage;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addPlcVariable;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addToStatisticsMapper;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.setProductionModePermission;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_CONFIG_MAPPING;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductionMode.MAINTENANCE;
import static com.sicpa.standard.sasscl.model.ProductionMode.STANDARD;
import static com.sicpa.tt018.scl.business.activation.constants.AlbaniaSCLActivationMessages.EXCEPTION_CODE_IN_SOFT_DRINK;
import static com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode.SOFT_DRINK;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.devices.plc.IPlcParamSender;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcRequest;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils.PLC_TYPE;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.tt018.scl.model.AlbaniaProductStatus;
import com.sicpa.tt018.scl.model.AlbaniaSKU;
import com.sicpa.tt018.scl.model.productionParameters.AlbaniaPermission;

public class TT018Bootstrap extends Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(TT018Bootstrap.class);

	private static final String EJECTION_TYPE_COMPLIANT = "1";
	private static final String EJECTION_TYPE_NON_COMPLIANT = "2";

	private IPlcParamSender plcParamSender;
	private String ejectionTypeVar;
	private PlcProvider plcProvider;

	@Override
	public void executeSpringInitTasks() {
		preProcessTask();
		super.executeSpringInitTasks();
		messageCusto();
		statisticsCusto();
		productionModePermission();
		sendEjectionTypeToPlcOnStarting();
	}

	private void productionModePermission() {
		setProductionModePermission(STANDARD, SasSclPermission.PRODUCTION_MODE_STANDARD);
		setProductionModePermission(MAINTENANCE, SasSclPermission.PRODUCTION_MODE_MAINTENANCE);
		setProductionModePermission(EXPORT, SasSclPermission.PRODUCTION_MODE_STANDARD);
		setProductionModePermission(SOFT_DRINK, AlbaniaPermission.PRODUCTION_MODE_SOFT_DRINK);
	}

	private void messageCusto() {
		addMessage(EXCEPTION_CODE_IN_SOFT_DRINK, "[ACT_05]", ERROR);
	}

	private void statisticsCusto() {
		addToStatisticsMapper(AlbaniaProductStatus.SENT_TO_PRINTER_BLOB, StatisticsKey.GOOD);
		addToStatisticsMapper(AlbaniaProductStatus.SOFT_DRINK, StatisticsKey.GOOD);
	}

	public static void addCustoPlcVariable() {
		Map<String, Object> options = new HashMap<>();
		options.put("lineGrp", "system");

		addPlcVariable("PARAM_LINE_SENSOR_TYPE", ".com.stLine[1].stParameters.nPackageType", PLC_TYPE.S, options);
	}

	private void preProcessTask() {
		addSoftDrinkProductionMode();
	}

	private void addSoftDrinkProductionMode() {
		IProductionConfigMapping mapping = BeanProvider.getBean(PRODUCTION_CONFIG_MAPPING);
		mapping.put(SOFT_DRINK, "softDrink");
	}

	private void sendEjectionTypeToPlcOnStarting() {
		addActionOnStartingProduction(() -> {
			try {
				sendEjectionTypeToPlc();
			} catch (PlcAdaptorException e) {
				logger.error("", e);
			}
		});
	}

	private void sendEjectionTypeToPlc() throws PlcAdaptorException {
		AlbaniaSKU sku = (AlbaniaSKU) productionParameters.getSku();
		if (sku == null) {
			return;
		}

		String ejectionType = sku.isBlobEnabled() ? EJECTION_TYPE_NON_COMPLIANT : EJECTION_TYPE_COMPLIANT;
		int line = 1;
		plcParamSender.sendToPlc(ejectionTypeVar, ejectionType, line);
		plcProvider.get().executeRequest(PlcRequest.RELOAD_PLC_PARAM);
	}

	public void setPlcParamSender(IPlcParamSender plcParamSender) {
		this.plcParamSender = plcParamSender;
	}

	public void setEjectionTypeVar(String ejectionTypeVar) {
		this.ejectionTypeVar = ejectionTypeVar;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

}
