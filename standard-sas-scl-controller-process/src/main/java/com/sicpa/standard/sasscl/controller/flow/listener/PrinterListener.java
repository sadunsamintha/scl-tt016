package com.sicpa.standard.sasscl.controller.flow.listener;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.printer.controller.model.command.PrinterMessage;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.statemachine.IStateMachine;
import com.sicpa.standard.sasscl.devices.printer.event.PrinterStoppedEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrinterListener {


    private static final Logger logger = LoggerFactory.getLogger(PrinterListener.class);

    protected IStateMachine stateMachine;

    @Subscribe
    public void onPrinterStopped(PrinterStoppedEvent evt) {
        logger.debug("Printer Stopped Event receive {}", evt);
        ApplicationFlowState currentState = stateMachine.getCurrentState();
        if(currentState.equals(ApplicationFlowState.STT_STARTED)){
            /**
             * The printer has been stopped while doing production.
             * Let's stop production.
             */
            EventBusService.post(new PrinterMessage(MessageEventKey.Printer.PRINTER_STOPPED_DURING_PRODUCTION));
        }
    }

    public void setStateMachine(IStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }


}
