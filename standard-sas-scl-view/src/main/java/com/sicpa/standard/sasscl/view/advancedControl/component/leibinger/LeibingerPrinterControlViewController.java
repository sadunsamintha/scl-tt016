package com.sicpa.standard.sasscl.view.advancedControl.component.leibinger;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.printer.impl.PrinterAdaptorLeibinger;
import com.sicpa.standard.sasscl.event.ChangePrinterUserLevelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LeibingerPrinterControlViewController implements ILeibingerPrinterViewControlListener {

    private static final Logger logger = LoggerFactory.getLogger(LeibingerPrinterControlViewController.class);


    private final LeibingerPrinterControlViewModel model = new LeibingerPrinterControlViewModel();
    private IHardwareController hardwareController;

    private PrinterAdaptorLeibinger findPrinter(String printerId) {
        Optional<PrinterAdaptorLeibinger> printer = hardwareController.getDevices().stream()
                .filter(d -> d.getName().equals(printerId)).map(d -> (PrinterAdaptorLeibinger) d).findFirst();

        return printer.orElseThrow(() -> new IllegalArgumentException("no printer found for:" + printerId));
    }

    @Override
    public void changeLeibingerUserLevel(String printerId, int userLevel) {
        PrinterAdaptorLeibinger printer = findPrinter(printerId);
        logger.debug("change user level on printer {} to user level {}", printerId, userLevel);
        printer.setUserLevel(new ChangePrinterUserLevelEvent(userLevel));
    }

    public LeibingerPrinterControlViewModel getModel() {
        return model;
    }

    public void setHardwareController(IHardwareController hardwareController) {
        this.hardwareController = hardwareController;
    }

    @Subscribe
    public void handleApplicationStateChange(ApplicationFlowStateChangedEvent evt) {
        if (evt.getCurrentState() == ApplicationFlowState.STT_DISCONNECTING_ON_PARAM_CHANGED) {
            model.reset();
            model.notifyModelChanged();
        }
        if (evt.getCurrentState() == ApplicationFlowState.STT_CONNECTING) {
            addAllPrinterToModel();
        }
    }

    private void addAllPrinterToModel() {
        for (IDevice device : hardwareController.getDevices()) {
            if (device instanceof PrinterAdaptorLeibinger) {
                model.addPrinter(device.getName());
            }
        }
        model.notifyModelChanged();
    }


}
