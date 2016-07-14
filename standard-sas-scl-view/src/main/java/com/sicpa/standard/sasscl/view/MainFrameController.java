package com.sicpa.standard.sasscl.view;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_EXIT;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_NO_SELECTION;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_RECOVERING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_NO_PREVIOUS;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_WITH_PREVIOUS;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STOPPING;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
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
import com.sicpa.standard.sasscl.view.config.plc.FrameCameraImage;
import com.sicpa.standard.sasscl.view.messages.I18nableLockingErrorModel;

public class MainFrameController extends MachineViewController {

	private final List<ISecuredComponentGetter> securedPanels = new ArrayList<>();
	private String lineId;
	private ProductionParameters productionParameters;
	private IMessageCodeMapper messageCodeMapper;
	private boolean filterProductionChangedEvent = false;
	private FrameCameraImage frameCameraImage;

	public static String LINE_LABEL_SEPARATOR = " : ";
	public static String LINE_LABEL_ID = Messages.get("lineId");

	public void addSecuredPanel(ISecuredComponentGetter securedComponentGetter) {
		securedPanels.add(securedComponentGetter);
	}

	public MainFrameController() {
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		// refresh application status label
		setApplicationStatus(getApplicationStatus());
	}

	public void addErrorMainPanel(String key, String message, Object... params) {
		if (lockingErrorModel != null && lockingErrorModel instanceof I18nableLockingErrorModel) {
			((I18nableLockingErrorModel) lockingErrorModel).addMessage(key, message, params);
		}
	}

	@Subscribe
	public void productionParametersChanged(final ProductionParametersEvent evt) {
		if (!filterProductionChangedEvent) {
			productionParametersChanged(false);
			filterProductionChangedEvent = false;
		}
	}

	public void setBarcode(String barcode) {
		productionParameters.setBarcode(barcode);
	}

	public void setSku(SKU sku) {
		productionParameters.setSku(sku);
	}

	public void setProductionMode(ProductionMode productionMode) {
		productionParameters.setProductionMode(productionMode);
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

	@Override
	protected void setBarcodeModel(IdInputmodel barcodeModel) {
		super.setBarcodeModel(barcodeModel);
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

	@Subscribe
	public void receiveCameraImage(final CameraImageEvent event) {
		if (frameCameraImage == null) {
			for (Window d : Dialog.getWindows()) {
				if (d instanceof FrameCameraImage) {
					frameCameraImage = (FrameCameraImage) d;
				}
			}
		}
		if (frameCameraImage != null) {
			frameCameraImage.setImage(event);
		}
	}

	public void setMessageCodeMapper(final IMessageCodeMapper messageCodeMapper) {
		this.messageCodeMapper = messageCodeMapper;
	}

	public IMessageCodeMapper getMessageCodeMapper() {
		return this.messageCodeMapper;
	}

	@Override
	protected void setLockingErrorModel(final LockingErrorModel lockingErrorModel) {
		super.setLockingErrorModel(lockingErrorModel);
	}

	public void setVisible(boolean visible) {
		if (getView() != null) {
			getView().setVisible(visible);
		}
	}

	public void setSecuredPanels(List<ISecuredComponentGetter> securedPanels) {
		this.securedPanels.clear();
		this.securedPanels.addAll(securedPanels);
	}

	public List<ISecuredComponentGetter> getSecuredPanels() {
		return securedPanels;
	}

	public void replaceMainPanel(JComponent panel) {
		if (getView() != null) {
			getView().replaceMainPanel(panel);
		}
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Subscribe
	public void handleLockFullScreenEvent(LockFullScreenEvent evt) {
		lockFrame(true, "exit");
	}

	@Subscribe
	public void handleUnlockFullScreenEvent(UnlockFullScreenEvent evt) {
		lockFrame(false, "exit");
	}

	@Override
	public void removeAllErrorMainPanel() {
		if (lockingErrorModel != null) {
			super.removeAllErrorMainPanel();
		}
	}

	@Override
	public void addErrorMainPanel(String key, String message) {
		if (lockingErrorModel != null) {
			super.addErrorMainPanel(key, message);
		}
	}

	@Override
	public void setApplicationStatus(ApplicationStatus status) {
		if (applicationStatusModel != null) {
			super.setApplicationStatus(status);
		}
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getLineId() {
		return lineId;
	}

}
