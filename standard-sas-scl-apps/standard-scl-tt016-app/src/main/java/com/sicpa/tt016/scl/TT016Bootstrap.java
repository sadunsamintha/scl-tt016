package com.sicpa.tt016.scl;

import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.flow.statemachine.FlowTransition;
import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.provider.impl.UnknownSkuProvider;
import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.tt016.refeed.TT016RefeedAvailabilityProvider;
import com.sicpa.tt016.scl.remote.remoteservices.ITT016RemoteServices;
import com.sicpa.tt016.view.selection.stop.StopReasonViewController;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

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
	private int codeTypeId;
	private TT016RefeedAvailabilityProvider refeedAvailabilityProvider;
	private ITT016RemoteServices tt016RemoteServices;

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		noStopIfDmxDetectedInExport();
		selectStopReasonWhenProductionStop();
		setUnknownSkuCodeType();
	}

	@Override
	protected void initRemoteServerConnected() {
		initSubsystemId();
		initIsRefeedAvailable();
		remoteServerSheduledJobs.executeInitialTasks();
	}

	private void initIsRefeedAvailable() {
		boolean isRefeedAvailable = tt016RemoteServices.isRefeedAvailable();
		refeedAvailabilityProvider.setIsRefeedAvailableInRemoteServer(isRefeedAvailable);
		saveIsRefeedAvailable(isRefeedAvailable);
	}

	private void saveIsRefeedAvailable(boolean isRefeedAvailable) {
		try {
			File globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();
			PropertiesUtils.savePropertiesKeepOrderAndComment(globalPropertiesFile, "refeedAvailable", Boolean.toString(isRefeedAvailable));
		} catch (Exception ex) {
			logger.error("Failed to save IsRefeedAvailable property", ex);
		}
	}


	private void setUnknownSkuCodeType() {
		unknownSkuProvider.get().setCodeType(new CodeType(codeTypeId));
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


	public void setTt016RemoteServices(ITT016RemoteServices tt016RemoteServices) {
		this.tt016RemoteServices = tt016RemoteServices;
	}

	public void setRefeedAvailabilityProvider(TT016RefeedAvailabilityProvider refeedAvailabilityProvider) {
		this.refeedAvailabilityProvider = refeedAvailabilityProvider;
	}

}
