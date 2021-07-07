package com.sicpa.tt016.scl;

import static com.sicpa.standard.gui.plaf.SicpaColor.GREEN_DARK;
import static com.sicpa.standard.gui.plaf.SicpaColor.RED;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_EXIT_APPLICATION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_RECOVERING_CONNECTION;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_EXIT;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_RECOVERING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STOPPING;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addActionOnConnectedApplicationState;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addActionOnExitApplicationState;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addMessage;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addPlcVariable;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreen;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreenTransitions;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addToStatisticsMapper;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.handleNewStatistic;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.setMessageType;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.setProductionModePermission;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.setStateNextPossibleStates;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_CONFIG_MAPPING;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.*;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.Activation.EXCEPTION_CODE_IN_EXPORT;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_WRONG_SKU;
import static com.sicpa.tt016.controller.flow.TT016ActivityTrigger.TRG_STOP_REASON_SELECTED;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.EXCEPTION_CODE_IN_AGING;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.EXCEPTION_CODE_IN_AGING_MSG_CODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.NO_INK_IN_REFEED_MODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.NO_INK_IN_REFEED_MODE_MSG_CODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.*;
import static com.sicpa.tt016.messages.TT016MessageEventKey.D900.*;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.EJECTED_PRODUCER;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.INK_DETECTED;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION_REASON_SELECTED;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.business.statistics.StatisticsRestoredEvent;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.statemachine.FlowTransition;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.plc.*;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.tt016.business.ejection.EjectionTypeSender;
import com.sicpa.tt016.devices.plc.PlcPersistentGrossNetProductCounterManager;
import com.sicpa.tt016.devices.plc.PlcPersistentGrossNetProductCounterManagerSCL;
import com.sicpa.tt016.model.DisallowedConfiguration;
import com.sicpa.tt016.model.TT016Permission;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.model.TT016ProductionMode;
import com.sicpa.tt016.monitoring.mbean.TT016SasAppLegacyMBean;
import com.sicpa.tt016.monitoring.mbean.TT016SclAppLegacyMBean;
import com.sicpa.tt016.provider.impl.TT016UnknownSkuProvider;
import com.sicpa.tt016.util.LegacyEncoderConverter;
import com.sicpa.tt016.view.selection.stop.StopReasonViewController;

import lombok.Setter;

public class TT016Bootstrap extends Bootstrap {

	private MainPanelGetter mainPanelGetter;
	private StopReasonViewController stopReasonViewController;
	private TT016UnknownSkuProvider unknownSkuProvider;
	private EjectionTypeSender ejectionTypeSender;
	private AutomatedBeamHeightManager automatedBeamHeightManager;
	private AutomatedBeamNtfHandler automatedBeamNtfHandler;
	private int codeTypeId;
	private boolean isHsOutMode;
	private boolean isBeamEnabled;
	private boolean isBeamInvalidHeightError;
	private List<DisallowedConfiguration> disallowedConfigurations;
	private LegacyEncoderConverter legacyEncoderConverter;
	private TT016SasAppLegacyMBean sasAppLegacyMBean;
	private TT016SclAppLegacyMBean sclAppLegacyMBean;
	private String productionBehaviorVar;
	@Setter
	private Object plcPersistentGrossNetProductCounterManager;
	
	private static final String SAS_MODE = "PRODUCTIONCONFIG-SAS";
	
	@Override
	public void executeSpringInitTasks() {
		if (isBeamEnabled) {
			addErrorMessagesForAutomatedBeam();
			initializeAlarmListenersForBeam();
			sendSKUHeightToBeam();
		}
		addErrorMessagesForD900();
		addAgingProductionMode();
		super.executeSpringInitTasks();
		noStopIfDmxDetectedInExport();
		selectStopReasonWhenProductionStop();
		setUnknownSkuCodeType();
		if (!isHsOutMode) {
			addProducerEjectionStatistic();
		}
		addInkDetectedStatistic();
		sendEjectionTypeForProductionMode();
		addDisallowedConfigurations(BeanProvider.getBean(BeansName.ALL_PROPERTIES));
		noStopIfBrsWrongCodeDetected();
		addWarningIfNoInkInRefeedMode();
		addWarningCodeFoundInAgingMode();
		convertLegacyEncodersIfAny();
		addNoInkInRefeedStatistic();
		addAgingProductionModePermission();
		addExportAgingStatistic();
	}

	public static void addPlcVariableJavaEjectionCounter() {
		addPlcVariable("NTF_LINE_JAVA_EJECTION_COUNTER", ".com.stLine[#x].stNotifications.nJavaEjectionCounter", PlcUtils.PLC_TYPE.I,
				new HashMap<>());
	}
	
	public static void addPlcVariableJavaNettCounter() {
		addPlcVariable("NTF_LINE_JAVA_GROSSNETT_COUNTER", PlcUtils.LINE_NTF+"nJavaNettCounter", PlcUtils.PLC_TYPE.I,
				new HashMap<>());
	}

	
	public static void addWiperPlcVariable() {
		addPlcVariable("PARAM_LINE_INHIBIT_WIPER", PlcUtils.LINE_PRM+"bInhibitWiper", PlcUtils.PLC_TYPE.B,
				new HashMap<String, String>() {{
				put("lineGrp", "wiper");
				}});
		addPlcVariable("PARAM_LINE_WIPER_DISTANCE", PlcUtils.LINE_PRM+"nWiperDistance", PlcUtils.PLC_TYPE.D,
				new HashMap<String, String>() {{
				put("lineGrp", "wiper");
				}});
		addPlcVariable("PARAM_LINE_WIPER_DISTANCE_TYPE", PlcUtils.LINE_PRM+"bWiperDistanceType", PlcUtils.PLC_TYPE.B,
				new HashMap<String, String>());

		addPlcVariable("PARAM_LINE_WIPER_LENGTH", PlcUtils.LINE_PRM+"nWiperLength", PlcUtils.PLC_TYPE.D,
				new HashMap<String, String>() {{
				put("lineGrp", "wiper");
				}});
		addPlcVariable("PARAM_LINE_WIPER_LENGTH_TYPE", PlcUtils.LINE_PRM+"bWiperLengthType", PlcUtils.PLC_TYPE.B,
				new HashMap<String, String>());

		addPlcVariable("PARAM_LINE_WIPER_RATIOENCODERMOTOR", PlcUtils.LINE_PRM+"nRatioEncoderMotor", PlcUtils.PLC_TYPE.I,
				new HashMap<String, String>() {{
				put("lineGrp", "wiper");
				}});
	}

	public static void addMotorizedBeamPlcVariables() {
		for (AutomatedBeamPlcEnums var : AutomatedBeamPlcEnums.values()) {
			if (var == AutomatedBeamPlcEnums.PARAM_HOME_TO_CONVEYOR_HEIGHT_MM ||
				var == AutomatedBeamPlcEnums.PARAM_PRODUCT_TO_PRINTER_HEIGHT_MM ||
				var == AutomatedBeamPlcEnums.REQUEST_BEAM_MANUAL_MODE) {
				CustoBuilder.addPlcVariable(var.toString(), var.getNameOnPlc(), var.getPlc_type(), new HashMap<String, String>() {{
					put("lineGrp", "misc");
				}});
			} else {
				CustoBuilder.addPlcVariable(var.toString(), var.getNameOnPlc(), var.getPlc_type(), new HashMap<>());
			}
		}
	}

	public static void addD900PlcVariables() {
		for (D900PlcEnums var : D900PlcEnums.values()) {
            CustoBuilder.addPlcVariable(var.toString(), var.getNameOnPlc(), var.getPlc_type(), new HashMap<>());
        }
	}

	private void convertLegacyEncodersIfAny() {
		legacyEncoderConverter.convertLegacyEncoders();
	}

	private void addWarningIfNoInkInRefeedMode() {
		addMessage(NO_INK_IN_REFEED_MODE, NO_INK_IN_REFEED_MODE_MSG_CODE, WARNING);
	}

	private void addWarningCodeFoundInAgingMode() {
		addMessage(EXCEPTION_CODE_IN_AGING, EXCEPTION_CODE_IN_AGING_MSG_CODE, WARNING);
	}

	private void addErrorMessagesForD900() {
		CustoBuilder.addMessage(PLC_D900_OFFLINE_ERROR, PLC_D900_OFFLINE_ERROR_MSG_CODE, ERROR);
	}

	private void addErrorMessagesForAutomatedBeam() {
		CustoBuilder.addMessage(AUTOMATED_BEAM_HEAD_TO_HOME, AUTOMATED_BEAM_HEAD_TO_HOME_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_ADJUST_HEIGHT, AUTOMATED_BEAM_ADJUST_HEIGHT_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_SAFETY_SENSOR_TRIG, AUTOMATED_BEAM_SAFETY_SENSOR_TRIG_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_AWAITING_RESET, AUTOMATED_BEAM_AWAITING_RESET_MSG_CODE, ERROR_DISPLAY);
		CustoBuilder.addMessage(AUTOMATED_BEAM_HEIGHT_SET, AUTOMATED_BEAM_HEIGHT_SET_MSG_CODE, WARNING);
		CustoBuilder.addMessage(AUTOMATED_BEAM_ERROR_STATE, AUTOMATED_BEAM_ERROR_STATE_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_ESTOP_STATE, AUTOMATED_BEAM_ESTOP_STATE_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_ESTOP_SWITCH_STATE_ENGAGED, AUTOMATED_BEAM_ESTOP_SWITCH_STATE_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_ESTOP_SWITCH_STATE_RELEASED, AUTOMATED_BEAM_ESTOP_SWITCH_STATE_RELEASED_MSG_CODE, WARNING);
		CustoBuilder.addMessage(PLC_AUTOMATED_BEAM_POWER_ERROR, PLC_AUTOMATED_BEAM_POWER_ERROR_MSG_CODE, ERROR);
		CustoBuilder.addMessage(PLC_AUTOMATED_BEAM_VELOCITY_ERROR, PLC_AUTOMATED_BEAM_VELOCITY_ERROR_MSG_CODE, ERROR);

		ActionMessageType invalidHeightMessageType = WARNING;
		if (isBeamInvalidHeightError) {
			invalidHeightMessageType = ERROR_DEVICE;
		}
		CustoBuilder.addMessage(AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED, AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED_MSG_CODE, invalidHeightMessageType);
	}

	private void noStopIfBrsWrongCodeDetected() {
		setMessageType(BRS_WRONG_SKU, WARNING);
	}

	private void sendEjectionTypeForProductionMode() {
		addActionOnConnectedApplicationState(()-> ejectionTypeSender.send(productionParameters));
	}

	private void sendSKUHeightToBeam() {
		addActionOnConnectedApplicationState(() -> automatedBeamHeightManager.setBeamHeight());
		addActionOnExitApplicationState(() -> automatedBeamHeightManager.setBeamHeight(0));
	}

	private void initializeAlarmListenersForBeam() { automatedBeamNtfHandler.init(); }

	private void addInkDetectedStatistic() {
		handleNewStatistic(ProductStatus.INK_DETECTED, INK_DETECTED, GREEN_DARK, 4, "stats.display.inkDetected", false);
	}

	private void addProducerEjectionStatistic() {
		handleNewStatistic(TT016ProductStatus.EJECTED_PRODUCER, EJECTED_PRODUCER, RED, 3,
				"stats.display.ejectedProducer", true);
	}

	private void addNoInkInRefeedStatistic() {
		addToStatisticsMapper(TT016ProductStatus.REFEED_NO_INK, StatisticsKey.BAD);
	}

    private void addAgingProductionMode() {
		IProductionConfigMapping mapping = BeanProvider.getBean(PRODUCTION_CONFIG_MAPPING);
		mapping.put(TT016ProductionMode.AGING, "aging");
	}

	private void addAgingProductionModePermission() {
		setProductionModePermission(TT016ProductionMode.AGING, TT016Permission.PRODUCTION_MODE_AGING);
	}

	private void addExportAgingStatistic() {
		addToStatisticsMapper(TT016ProductStatus.AGING, StatisticsKey.GOOD);
	}

	@Override
	protected void initRemoteServerConnected() {
		initSubsystemId();
		remoteServerSheduledJobs.executeInitialTasks();
	}

	@Override
	protected void installJMXBeans() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			if (productionBehaviorVar.equals(SAS_MODE)) {
				mbs.registerMBean(sasAppLegacyMBean, new ObjectName("SPLApplication:type=Monitoring"));
			} else {
				mbs.registerMBean(sclAppLegacyMBean, new ObjectName("SPLApplication:type=Statistics"));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void setUnknownSkuCodeType() {
		unknownSkuProvider.setCodeType(new CodeType(codeTypeId));
	}

	private void noStopIfDmxDetectedInExport() {
		setMessageType(EXCEPTION_CODE_IN_EXPORT, WARNING);

	}

	private void selectStopReasonWhenProductionStop() {
		addScreen(stopReasonViewController);
		addScreenTransitions(mainPanelGetter, new ScreenTransition(STOP_PRODUCTION, stopReasonViewController));
		addScreenTransitions(stopReasonViewController, new ScreenTransition(STOP_PRODUCTION_REASON_SELECTED,
				mainPanelGetter));

		setStateNextPossibleStates(STT_STOPPING, new FlowTransition(TRG_STOP_REASON_SELECTED, STT_CONNECTED),
				new FlowTransition(TRG_RECOVERING_CONNECTION, STT_RECOVERING), new FlowTransition(TRG_EXIT_APPLICATION,
						STT_EXIT));
	}

	private void addDisallowedConfigurations(Properties configuration) {
		disallowedConfigurations.forEach(
				d -> d.validate(configuration, (k,p) -> EventBusService.post(new ActionEventWarning(k,null,p))));
	}
	
    protected void restorePreviousSelectedProductionParams() {
        ProductionParameters previous = storage.getSelectedProductionParameters();
        if (productionParametersValidator.validate(previous)) {
            productionParameters.setBarcode(previous.getBarcode());
            productionParameters.setSku(previous.getSku());
            productionParameters.setProductionMode(previous.getProductionMode());
            EventBusService.post(new ProductionParametersEvent(previous));
            restoreStatistics();
            if(productionBehaviorVar.equals(SAS_MODE)) {
            	((PlcPersistentGrossNetProductCounterManager) plcPersistentGrossNetProductCounterManager).updateProductParamAndCount();
            }else {
            	((PlcPersistentGrossNetProductCounterManagerSCL) plcPersistentGrossNetProductCounterManager).updateProductParamAndCount();
            }
        }
    }
	
	public void setMainPanelGetter(MainPanelGetter mainPanelGetter) {
		this.mainPanelGetter = mainPanelGetter;
	}

	public void setStopReasonViewController(StopReasonViewController stopReasonViewController) {
		this.stopReasonViewController = stopReasonViewController;
	}

	public void setUnknownSkuProvider(TT016UnknownSkuProvider unknownSkuProvider) {
		this.unknownSkuProvider = unknownSkuProvider;
	}

	public void setCodeTypeId(int codeTypeId) {
		this.codeTypeId = codeTypeId;
	}

	public void setHsOutMode(boolean hsOutMode) {
		isHsOutMode = hsOutMode;
	}

	public void setBeamEnabled(boolean beamEnabled) {
		isBeamEnabled = beamEnabled;
	}

	public void setBeamInvalidHeightError(boolean beamInvalidHeightError) {
		isBeamInvalidHeightError = beamInvalidHeightError;
	}

	public void setEjectionTypeSender(EjectionTypeSender ejectionTypeSender) {
		this.ejectionTypeSender = ejectionTypeSender;
	}

	public void setAutomatedBeamHeightManager(AutomatedBeamHeightManager automatedBeamHeightManager) {
		this.automatedBeamHeightManager = automatedBeamHeightManager;
	}

	public void setAutomatedBeamNtfHandler(AutomatedBeamNtfHandler automatedBeamNtfHandler) {
		this.automatedBeamNtfHandler = automatedBeamNtfHandler;
	}

	public void setDisallowedConfigurations(List<DisallowedConfiguration> disallowedConfigurations) {
		this.disallowedConfigurations = disallowedConfigurations;
	}

	public void setLegacyEncoderConverter(LegacyEncoderConverter legacyEncoderConverter) {
		this.legacyEncoderConverter = legacyEncoderConverter;
	}

	public void setSasAppLegacyMBean(TT016SasAppLegacyMBean sasAppLegacyMBean) {
		this.sasAppLegacyMBean = sasAppLegacyMBean;
	}

	public void setSclAppLegacyMBean(TT016SclAppLegacyMBean sclAppLegacyMBean) {
		this.sclAppLegacyMBean = sclAppLegacyMBean;
	}

	public void setProductionBehaviorVar(String productionBehaviorVar) {
		this.productionBehaviorVar = productionBehaviorVar;
	}
}