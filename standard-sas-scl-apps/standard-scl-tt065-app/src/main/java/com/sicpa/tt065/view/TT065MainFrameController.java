package com.sicpa.tt065.view;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.IMessageCodeMapper;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockingError.LockingErrorModel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.screen.machine.MachineViewController;
import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputmodel;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatus;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraImageEvent;
import com.sicpa.standard.sasscl.event.LockFullScreenEvent;
import com.sicpa.standard.sasscl.event.UnlockFullScreenEvent;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.sasscl.view.STDSASSCLApplicationStatus;
import com.sicpa.standard.sasscl.view.config.plc.FrameCameraImage;
import com.sicpa.standard.sasscl.view.messages.I18nableLockingErrorModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;

public class TT065MainFrameController extends MainFrameController {

	private ProductionParameters productionParameters;
	private boolean filterProductionChangedEvent = false;

	public void setBarcode(String barcode) {
		productionParameters.setBarcode(barcode);
	}

	public void setSku(SKU sku) {
		productionParameters.setSku(sku);
	}

	public void setProductionMode(ProductionMode productionMode) {
		productionParameters.setProductionMode(productionMode);
	}

	@Subscribe
	public void productionParametersChanged(final ProductionParametersEvent evt) {
		if (!filterProductionChangedEvent) {
			productionParametersChanged(false);
			filterProductionChangedEvent = false;
		}
	}

	public void productionParametersChanged() {
		productionParametersChanged(true);
	}

	private void productionParametersChanged(boolean sendEvent) {

		// refresh all errors and warning when production mode has been changed
		if (lockingErrorModel != null) {
			removeAllWarnings();
			removeAllErrorMainPanel();
		}
		if (sendEvent) {
			filterProductionChangedEvent = true;
			EventBusService.post(new ProductionParametersEvent(productionParameters));
		}
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {

				if (evt.getCurrentState() == STT_NO_SELECTION) {
					onNoSelection();
				} else if (evt.getCurrentState() == STT_STARTING) {
					onStarting();
				} else if (evt.getCurrentState() == STT_STARTED) {
					onStarted();
				} else if (evt.getCurrentState() == STT_SELECT_NO_PREVIOUS) {
					onSelecting();
				} else if (evt.getCurrentState() == STT_SELECT_WITH_PREVIOUS) {
					onSelecting();
				} else if (evt.getCurrentState() == STT_STOPPING) {
					onStopping();
				} else if (evt.getCurrentState() == STT_RECOVERING) {
					onConnecting(evt);
				} else if (evt.getCurrentState() == STT_CONNECTING) {
					onConnecting(evt);
				} else if (evt.getCurrentState() == STT_CONNECTED) {
					onConnected();
				} else if (evt.getCurrentState() == STT_EXIT) {
					onExiting();
				}
			}
		});
	}

	private void onNoSelection() {
		if (applicationStatusModel != null) {
			setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopped"));
		}
	}

	private void onSelecting() {
		removeAllErrorMainPanel();
		setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopped"));
	}

	private void onStarting() {
		removeAllWarnings();
		setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.GREEN_DARK, "view.status.starting"));
	}

	private void onStarted() {
		Color color;
		String text;
		if (productionParameters.getProductionMode().equals(ProductionMode.MAINTENANCE)) {
			color = SicpaColor.ORANGE;
			text = "view.status.maintenance";
		} else {
			color = SicpaColor.GREEN_DARK;
			text = "view.status.running";
		}
		setApplicationStatus(new STDSASSCLApplicationStatus(color, text));
	}

	private void onStopping() {
		setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopping"));
	}

	private void onExiting() {
		setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.exiting"));
	}

	private void onConnected() {
		setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopped"));
	}

	private void onConnecting(final ApplicationFlowStateChangedEvent evt) {
		setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.recovering"));
	}
}
