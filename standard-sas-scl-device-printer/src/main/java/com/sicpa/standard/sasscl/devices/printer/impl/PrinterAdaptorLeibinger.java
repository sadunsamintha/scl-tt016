package com.sicpa.standard.sasscl.devices.printer.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.printer.controller.model.SequenceStatus;
import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;
import com.sicpa.standard.printer.leibinger.controller.LeibingerPrinterController;
import com.sicpa.standard.printer.leibinger.driver.leibinger.LeibingerUserLevel;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.IMappingExtendedCodeBehavior;
import com.sicpa.standard.sasscl.event.ChangePrinterUserLevelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.sicpa.standard.printer.leibinger.driver.leibinger.LeibingerSpecificSettings.CMD_SET_USER_LEVEL;

public class PrinterAdaptorLeibinger extends PrinterAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(PrinterAdaptorLeibinger.class);

    private IMappingExtendedCodeBehavior mappingExtendedCodeBehavior;

    public PrinterAdaptorLeibinger() {
        super();
    }

    public PrinterAdaptorLeibinger(IPrinterController controller) {
        super(controller);
    }

    private CountDownLatch startPrintingSignal = new CountDownLatch(1);

    private volatile boolean isStartPrintingBlocked = false;


    @Subscribe
    public void setUserLevel(ChangePrinterUserLevelEvent event) {
        try {
            controller.sendSpecificSettings(CMD_SET_USER_LEVEL.getValue(), LeibingerUserLevel.get(event.getLevel()));
        } catch (PrinterException e) {
            logger.error("", e.getMessage());
        }
    }

    @Override
    public void sendCodesToPrint(List<String> codes) throws PrinterAdaptorException {
        logger.debug("Printer sending codes to print");
        try {
            controller.sendExtendedCodes(mappingExtendedCodeBehavior.get(getName()).createExCodes(codes));
            codeSent = true;
        } catch (PrinterException e) {
            throw new PrinterAdaptorException("sending codes to printer failed", e);
        }
    }

    @Override
    public void onSequenceStatusChanged(Object sender, SequenceStatus args) {

        if (args.equals(SequenceStatus.READY)) {
            if (isStartPrintingBlocked) {
                unblockStartPrinting();
            }
            notifyAllIssuesSolved();
        }

        if (args.equals(SequenceStatus.READY_NOZZLE_NOT_OPEN) && !isStartPrintingBlocked) {
            notifyAllIssuesSolved();
        }

        if (!isConnected()) {
            // workaround because apparently "sometime" we don t get the
            // connected notification
            fireDeviceStatusChanged(DeviceStatus.CONNECTED);
        }
        if (lastSequence == args) {
            return;
        }
        logger.debug("sequence status received: {}", args.name());

        if (status.isConnected() && isNotReady(args)) {
            // if connected and the sequence is not ready
            fireMessage(PrinterMessageId.NOT_READY_TO_PRINT, args.name());
        }
        lastSequence = args;
    }

    private boolean isNotReady(SequenceStatus sequenceStatus) {
        return sequenceStatus != SequenceStatus.READY && sequenceStatus != SequenceStatus.READY_NOZZLE_NOT_OPEN;
    }

    @Override
    public void doStart() throws PrinterAdaptorException {
        try {
            codeSent = false;
            if (lastSequence == SequenceStatus.READY_NOZZLE_NOT_OPEN) {
                blockStartPrinting();
                notifyAllIssuesSolved();
            } else {
                controller.start();
            }
            if (startedOnce) {
                // the printer has already been started so it has codes
                fireDeviceStatusChanged(DeviceStatus.STARTED);
            } else {
                checkForCodeSent(0);
            }
            startedOnce = true;
        } catch (PrinterException e) {
            throw new PrinterAdaptorException("error when start printing", e);
        }
    }

    @Override
    public void doStop() throws PrinterAdaptorException {
        try {
            if (status == DeviceStatus.STARTED) {
                controller.stop();
            }
        } catch (PrinterException e) {
            throw new PrinterAdaptorException("error when stop printing", e);
        } finally {
            fireDeviceStatusChanged(DeviceStatus.STOPPED);
        }
    }

    private void blockStartPrinting() throws PrinterException {
        try {
            controller.start();
            isStartPrintingBlocked = true;
            startPrintingSignal.await();
        } catch (InterruptedException ex) {
            logger.error("The thread has been interrupted.", ex);
        }
    }

    private void unblockStartPrinting() {
        isStartPrintingBlocked = false;
        startPrintingSignal.countDown();
        startPrintingSignal = new CountDownLatch(1);
    }


    public void setMappingExtendedCodeBehavior(IMappingExtendedCodeBehavior mappingExtendedCodeBehavior) {
        this.mappingExtendedCodeBehavior = mappingExtendedCodeBehavior;
    }

    public long getNozzleDelayMS() throws Exception {
        LeibingerPrinterController printerController =
                (LeibingerPrinterController) ((Advised)this.controller).getTargetSource().getTarget();
        return printerController.getModel().getNozzleCloseDelayMs();
    }
}
