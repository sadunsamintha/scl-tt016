package com.sicpa.tt016.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.flow.statemachine.FlowTransition;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.provider.impl.UnknownSkuProvider;
import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.tt016.business.ejection.EjectionTypeSender;
import com.sicpa.tt016.model.DisallowedConfiguration;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.model.statistics.TT016StatisticsKey;
import com.sicpa.tt016.refeed.TT016RefeedAvailabilityProvider;
import com.sicpa.tt016.scl.remote.RemoteServerRefeedAvailability;
import com.sicpa.tt016.view.selection.stop.StopReasonViewController;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_EXIT_APPLICATION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_RECOVERING_CONNECTION;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.*;
import static com.sicpa.tt016.controller.flow.TT016ActivityTrigger.TRG_STOP_REASON_SELECTED;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION_REASON_SELECTED;

public class TT016Bootstrap extends Bootstrap {

	private MainPanelGetter mainPanelGetter;
	private StopReasonViewController stopReasonViewController;
	private UnknownSkuProvider unknownSkuProvider;
	private EjectionTypeSender ejectionTypeSender;
	private int codeTypeId;
	private TT016RefeedAvailabilityProvider refeedAvailabilityProvider;
	private RemoteServerRefeedAvailability remoteServerRefeedAvailability;
	private File globalPropertiesFile;
	private List<DisallowedConfiguration> disallowedConfigurations;

	@Override
	public void executeSpringInitTasks() {
		Properties configuration = new Properties();
		try {
			globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();
			configuration.load(new FileInputStream(globalPropertiesFile));
		}catch(Exception ex){
			logger.error("Cannot load properties file,file=" + ConfigUtilEx.GLOBAL_PROPERTIES_PATH, ex);
		}

		super.executeSpringInitTasks();
		noStopIfDmxDetectedInExport();
		selectStopReasonWhenProductionStop();
		unknownSkuProvider.get().setCodeType(new CodeType(codeTypeId));
		addProducerEjectedStatistic();
		addInkDetectedStatistic();
		addActionOnStartingProduction(()-> ejectionTypeSender.send(productionParameters));
		disallowedConfigurations.forEach(d -> d.validate(configuration, (k,p) -> EventBusService.post(new ActionEventWarning(k,null,p))));
	}


	private void addInkDetectedStatistic() {
		CustoBuilder.handleNewStatistic(ProductStatus.INK_DETECTED, TT016StatisticsKey.INK_DETECTED,
				SicpaColor.GREEN_DARK, 4, "stats.display.inkDetected");
	}

	private void addProducerEjectedStatistic() {
		CustoBuilder.handleNewStatistic(TT016ProductStatus.EJECTED_PRODUCER, TT016StatisticsKey.EJECTED_PRODUCER,
				SicpaColor.RED, 3, "stats.display.ejectedProducer");
	}

	@Override
	protected void initRemoteServerConnected() {
		initSubsystemId();
		initIsRefeedAvailable();
		remoteServerSheduledJobs.executeInitialTasks();
	}

	private void initIsRefeedAvailable() {
		boolean isRefeedAvailableInRemoteServer = remoteServerRefeedAvailability.isRemoteRefeedAvailable();
		refeedAvailabilityProvider.setIsRefeedAvailableInRemoteServer(isRefeedAvailableInRemoteServer);
		saveIsRefeedAvailable(isRefeedAvailableInRemoteServer);
	}

	private void saveIsRefeedAvailable(boolean isRefeedAvailable) {
		try {
			PropertiesUtils.savePropertiesKeepOrderAndComment(globalPropertiesFile, "refeedAvailable", Boolean.toString(isRefeedAvailable));
		} catch (Exception ex) {
			logger.error("Failed to save IsRefeedAvailable property", ex);
		}
	}



	private void noStopIfDmxDetectedInExport() {
		setMessageType(MessageEventKey.Activation.EXCEPTION_CODE_IN_EXPORT, ActionMessageType.WARNING);
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

	public void setMainPanelGetter(MainPanelGetter mainPanelGetter) {
		this.mainPanelGetter = mainPanelGetter;
	}

	public void setStopReasonViewController(StopReasonViewController stopReasonViewController) {
		this.stopReasonViewController = stopReasonViewController;
	}

	public void setUnknownSkuProvider(UnknownSkuProvider unknownSkuProvider) {
		this.unknownSkuProvider = unknownSkuProvider;
	}

	public void setCodeTypeId(int codeTypeId) {
		this.codeTypeId = codeTypeId;
	}


	public void setRemoteServerRefeedAvailability(RemoteServerRefeedAvailability remoteServerRefeedAvailability) {
		this.remoteServerRefeedAvailability = remoteServerRefeedAvailability;
	}

	public void setRefeedAvailabilityProvider(TT016RefeedAvailabilityProvider refeedAvailabilityProvider) {
		this.refeedAvailabilityProvider = refeedAvailabilityProvider;
	}

	public void setEjectionTypeSender(EjectionTypeSender ejectionTypeSender) {
		this.ejectionTypeSender = ejectionTypeSender;
	}

	public void setDisallowedConfigurations(List<DisallowedConfiguration> disallowedConfigurations) {
		this.disallowedConfigurations = disallowedConfigurations;
	}
}
