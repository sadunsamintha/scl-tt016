package com.sicpa.standard.sasscl.view;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.descriptor.ModelEditableProperties;
import com.sicpa.standard.client.common.descriptor.validator.Validators;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.IMessageCodeMapper;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockingError.LockingErrorModel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.screen.machine.MachineViewController;
import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputmodel;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatus;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.view.event.ErrorBlockingViewEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraImageEvent;
import com.sicpa.standard.sasscl.event.LockFullScreenEvent;
import com.sicpa.standard.sasscl.event.UnlockFullScreenEvent;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.view.config.plc.FrameCameraImage;
import com.sicpa.standard.sasscl.view.messages.I18nableLockingErrorModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;

public class MainFrameController extends MachineViewController {

    protected final List<SecuredComponentGetter> securedPanels = new ArrayList<>();

    protected SkuListProvider productionParametersProvider;

    protected ModelEditableProperties editablePropertyDescriptors;
    protected Validators beanValidators;

    protected String lineId;

    protected ProductionParameters productionParameters;

    protected IMessageCodeMapper messageCodeMapper;

    public static String LINE_LABEL_SEPARATOR = " : ";

    public static String LINE_LABEL_ID = Messages.get("lineId");


    public void addSecuredPanel(SecuredComponentGetter securedComponentGetter) {
        securedPanels.add(securedComponentGetter);
    }

    public MainFrameController(final SkuListProvider productionParametersProvider) {
        this.productionParametersProvider = productionParametersProvider;
    }

    public MainFrameController() {
    }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt) {

        // refresh line id label
        setLineId(LINE_LABEL_ID + LINE_LABEL_SEPARATOR + lineId);

        // refresh application status label
        setApplicationStatus(getApplicationStatus());
    }


    @Subscribe
    public void addErrorMainPanel(final ErrorBlockingViewEvent evt) {
        addErrorMainPanel(evt.getKey(), evt.getMessage());
    }

    public void addErrorMainPanel(final String key, final String message, final Object... params) {
        if (lockingErrorModel != null) {
            ((I18nableLockingErrorModel) this.lockingErrorModel).addMessage(key, message, params);
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

    protected boolean filterProductionChangedEvent = false;

    public void productionParametersChanged() {
        productionParametersChanged(true);
    }

    public void productionParametersChanged(boolean sendEvent) {

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
    protected void setBarcodeModel(final IdInputmodel barcodeModel) {
        super.setBarcodeModel(barcodeModel);
    }

    protected ApplicationFlowState currentApplicationState = ApplicationFlowState.STT_NO_SELECTION;
    ;

    @Subscribe
    public void processStateChanged(final ApplicationFlowStateChangedEvent evt) {
        ThreadUtils.invokeLater(new Runnable() {
            @Override
            public void run() {

                currentApplicationState = evt.getCurrentState();

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

    protected void onNoSelection() {
        if (applicationStatusModel != null) {
            setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopped"));
        }
    }

    protected void onSelecting() {
        removeAllErrorMainPanel();
        setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopped"));
    }

    protected void onStarting() {
        removeAllWarnings();
        setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.GREEN_DARK, "view.status.starting"));
    }

    protected void onStarted() {
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

    protected void onStopping() {
        setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopping"));
    }

    protected void onStopped() {
        setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopped"));
    }

    protected void onExiting() {
        setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.exiting"));
    }

    protected void onConnected() {
        setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.stopped"));
    }

    protected void onConnecting(final ApplicationFlowStateChangedEvent evt) {
        setApplicationStatus(new STDSASSCLApplicationStatus(SicpaColor.RED, "view.status.recovering"));
    }

    @Subscribe
    public void handleHardwareConnecting(final HardwareControllerStatusEvent evt) {
        ThreadUtils.invokeLater(new Runnable() {
            @Override
            public void run() {

                removeAllErrorMainPanel();

                if (currentApplicationState == STT_SELECT_WITH_PREVIOUS) {
                    return;
                }

                if (evt.getStatus().equals(HardwareControllerStatus.CONNECTING)) {
                    String errorMsg = "";
                    for (String error : evt.getErrors()) {
                        errorMsg += error + "\n";
                    }
                    addErrorMainPanel("", "controller.device.recovering.waiting", "\n" + errorMsg);
                }
            }
        });
    }

    public void setEditablePropertyDescriptors(final ModelEditableProperties editablePropertyDescriptor) {
        this.editablePropertyDescriptors = editablePropertyDescriptor;
    }

    public ModelEditableProperties getEditablePropertyDescriptors() {
        return editablePropertyDescriptors;
    }

    public void setBeanValidators(final Validators beanValidators) {
        this.beanValidators = beanValidators;
    }

    public Validators getBeanValidators() {
        return this.beanValidators;
    }

    protected FrameCameraImage frameCameraImage;

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
            frameCameraImage.setimage(event);
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

    public void setSecuredPanels(List<SecuredComponentGetter> securedPanels) {
        this.securedPanels.clear();
        this.securedPanels.addAll(securedPanels);
    }

    public List<SecuredComponentGetter> getSecuredPanels() {
        return securedPanels;
    }

    public void replaceMainPanel(JComponent panel) {
        if (getView() != null) {
            getView().replaceMainPanel(panel);
        }
    }

    public void resetMainPanel() {
        if (getView() != null) {
            getView().resetMainPanel();
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
