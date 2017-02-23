package com.sicpa.tt016.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.flow.statemachine.FlowTransition;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.tt016.business.ejection.EjectionTypeSender;
import com.sicpa.tt016.model.DisallowedConfiguration;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.monitoring.mbean.TT016SclAppLegacyMBean;
import com.sicpa.tt016.provider.impl.TT016UnknownSkuProvider;
import com.sicpa.tt016.util.LegacyEncoderConverter;
import com.sicpa.tt016.view.selection.stop.StopReasonViewController;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;

import static com.sicpa.standard.gui.plaf.SicpaColor.GREEN_DARK;
import static com.sicpa.standard.gui.plaf.SicpaColor.RED;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_EXIT_APPLICATION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_RECOVERING_CONNECTION;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.*;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.WARNING;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.Activation.EXCEPTION_CODE_IN_EXPORT;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_WRONG_SKU;
import static com.sicpa.tt016.controller.flow.TT016ActivityTrigger.TRG_STOP_REASON_SELECTED;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.NO_INK_IN_REFEED_MODE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.ACTIVATION.NO_INK_IN_REFEED_MODE_MSG_CODE;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.EJECTED_PRODUCER;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.INK_DETECTED;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION_REASON_SELECTED;

public class TT016Bootstrap extends Bootstrap {

	private MainPanelGetter mainPanelGetter;
	private StopReasonViewController stopReasonViewController;
	private TT016UnknownSkuProvider unknownSkuProvider;
	private EjectionTypeSender ejectionTypeSender;
	private int codeTypeId;
	private List<DisallowedConfiguration> disallowedConfigurations;
	private LegacyEncoderConverter legacyEncoderConverter;
	private TT016SclAppLegacyMBean sclAppLegacyMBean;

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		noStopIfDmxDetectedInExport();
		selectStopReasonWhenProductionStop();
		setUnknownSkuCodeType();
		addProducerEjectionStatistic();
		addInkDetectedStatistic();
		sendEjectionTypeForProductionMode();
		addDisallowedConfigurations(BeanProvider.getBean(BeansName.ALL_PROPERTIES));
		noStopIfBrsWrongCodeDetected();
		addWarningIfNoInkInRefeedMode();
		convertLegacyEncodersIfAny();
	}

	private void convertLegacyEncodersIfAny() {
		legacyEncoderConverter.convertLegacyEncoders();
	}

	private void addWarningIfNoInkInRefeedMode() {
		addMessage(NO_INK_IN_REFEED_MODE, NO_INK_IN_REFEED_MODE_MSG_CODE, WARNING);
	}

	private void noStopIfBrsWrongCodeDetected() {
		setMessageType(BRS_WRONG_SKU, WARNING);
	}

	private void sendEjectionTypeForProductionMode() {
		addActionOnConnectedApplicationState(()-> ejectionTypeSender.send(productionParameters));
	}

	private void addInkDetectedStatistic() {
		handleNewStatistic(ProductStatus.INK_DETECTED, INK_DETECTED, GREEN_DARK, 4, "stats.display.inkDetected", false);
	}

	private void addProducerEjectionStatistic() {
		handleNewStatistic(TT016ProductStatus.EJECTED_PRODUCER, EJECTED_PRODUCER, RED, 3,
				"stats.display.ejectedProducer", true);
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
			mbs.registerMBean(sclAppLegacyMBean, new ObjectName("SPLApplication:type=Statistics"));
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

	public void setDisallowedConfigurations(List<DisallowedConfiguration> disallowedConfigurations) {
		this.disallowedConfigurations = disallowedConfigurations;
	}

	public void setLegacyEncoderConverter(LegacyEncoderConverter legacyEncoderConverter) {
		this.legacyEncoderConverter = legacyEncoderConverter;
	}

	public void setSclAppLegacyMBean(TT016SclAppLegacyMBean sclAppLegacyMBean) {
		this.sclAppLegacyMBean = sclAppLegacyMBean;
	}
}
