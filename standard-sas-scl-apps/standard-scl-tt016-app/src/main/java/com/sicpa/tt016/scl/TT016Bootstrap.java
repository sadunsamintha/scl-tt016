package com.sicpa.tt016.scl;

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
import com.sicpa.standard.sasscl.controller.flow.statemachine.FlowTransition;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.plc.AutomatedBeamNtfHandler;
import com.sicpa.standard.sasscl.devices.plc.AutomatedBeamHeightManager;
import com.sicpa.standard.sasscl.devices.plc.AutomatedBeamPlcEnums;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.tt016.business.ejection.EjectionTypeSender;
import com.sicpa.tt016.model.DisallowedConfiguration;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.monitoring.mbean.TT016SasAppLegacyMBean;
import com.sicpa.tt016.monitoring.mbean.TT016SclAppLegacyMBean;
import com.sicpa.tt016.provider.impl.TT016UnknownSkuProvider;
import com.sicpa.tt016.util.LegacyEncoderConverter;
import com.sicpa.tt016.view.selection.stop.StopReasonViewController;

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
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.setStateNextPossibleStates;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR_DEVICE;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR_DISPLAY;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.WARNING;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.Activation.EXCEPTION_CODE_IN_EXPORT;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_WRONG_SKU;
import static com.sicpa.tt016.controller.flow.TT016ActivityTrigger.TRG_STOP_REASON_SELECTED;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.NO_INK_IN_REFEED_MODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.NO_INK_IN_REFEED_MODE_MSG_CODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_ADJUST_HEIGHT;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_ADJUST_HEIGHT_MSG_CODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_AWAITING_RESET;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_AWAITING_RESET_MSG_CODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_HEAD_TO_HOME;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_HEAD_TO_HOME_MSG_CODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_HEIGHT_SET;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_HEIGHT_SET_MSG_CODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_SAFETY_SENSOR_TRIG;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_SAFETY_SENSOR_TRIG_MSG_CODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED_MSG_CODE;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.EJECTED_PRODUCER;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.INK_DETECTED;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION_REASON_SELECTED;

public class TT016Bootstrap extends Bootstrap {

	private MainPanelGetter mainPanelGetter;
	private StopReasonViewController stopReasonViewController;
	private TT016UnknownSkuProvider unknownSkuProvider;
	private EjectionTypeSender ejectionTypeSender;
	private AutomatedBeamHeightManager automatedBeamHeightManager;
	private AutomatedBeamNtfHandler automatedBeamNtfHandler;
	private int codeTypeId;
	private List<DisallowedConfiguration> disallowedConfigurations;
	private LegacyEncoderConverter legacyEncoderConverter;
	private TT016SasAppLegacyMBean sasAppLegacyMBean;
	private TT016SclAppLegacyMBean sclAppLegacyMBean;
	private String productionBehaviorVar;
	
	private static final String SAS_MODE = "PRODUCTIONCONFIG-SAS";
	private static final String SCL_MODE = "PRODUCTIONCONFIG-SCL";

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		noStopIfDmxDetectedInExport();
		selectStopReasonWhenProductionStop();
		setUnknownSkuCodeType();
		addProducerEjectionStatistic();
		addInkDetectedStatistic();
		sendEjectionTypeForProductionMode();
		sendSKUHeightToBeam();
		addDisallowedConfigurations(BeanProvider.getBean(BeansName.ALL_PROPERTIES));
		noStopIfBrsWrongCodeDetected();
		addWarningIfNoInkInRefeedMode();
		convertLegacyEncodersIfAny();
		addNoInkInRefeedStatistic();
		addErrorMessagesForAutomatedBeam();
		initializeAlarmListenersForBeam();
	}

	public static void addPlcVariableJavaEjectionCounter() {
		addPlcVariable("NTF_LINE_JAVA_EJECTION_COUNTER", ".com.stLine[#x].stNotifications.nJavaEjectionCounter", PlcUtils.PLC_TYPE.I,
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
			if (var == AutomatedBeamPlcEnums.PARAM_CONVEYOR_HEIGHT_FROM_FLOOR_MM) {
				CustoBuilder.addPlcVariable(var.toString(), var.getNameOnPlc(), var.getPlc_type(), new HashMap<String, String>() {{
					put("lineGrp", "misc");
				}});
			} else {
				CustoBuilder.addPlcVariable(var.toString(), var.getNameOnPlc(), var.getPlc_type(), new HashMap<>());
			}
		}
	}

	private void convertLegacyEncodersIfAny() {
		legacyEncoderConverter.convertLegacyEncoders();
	}

	private void addWarningIfNoInkInRefeedMode() {
		addMessage(NO_INK_IN_REFEED_MODE, NO_INK_IN_REFEED_MODE_MSG_CODE, WARNING);
	}

	private void addErrorMessagesForAutomatedBeam() {
		CustoBuilder.addMessage(AUTOMATED_BEAM_HEAD_TO_HOME, AUTOMATED_BEAM_HEAD_TO_HOME_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_ADJUST_HEIGHT, AUTOMATED_BEAM_ADJUST_HEIGHT_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_SAFETY_SENSOR_TRIG, AUTOMATED_BEAM_SAFETY_SENSOR_TRIG_MSG_CODE, ERROR_DEVICE);
		CustoBuilder.addMessage(AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED, AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED_MSG_CODE, ERROR);
		CustoBuilder.addMessage(AUTOMATED_BEAM_AWAITING_RESET, AUTOMATED_BEAM_AWAITING_RESET_MSG_CODE, ERROR_DISPLAY);
		CustoBuilder.addMessage(AUTOMATED_BEAM_HEIGHT_SET, AUTOMATED_BEAM_HEIGHT_SET_MSG_CODE, WARNING);
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