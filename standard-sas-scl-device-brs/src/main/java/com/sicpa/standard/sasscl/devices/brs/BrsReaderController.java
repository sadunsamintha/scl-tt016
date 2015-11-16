package com.sicpa.standard.sasscl.devices.brs;


import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.model.BrsModel;
import com.sicpa.standard.sasscl.devices.brs.reader.CodeReaderAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class BrsReaderController implements BrsReaderDevice {

    private static final Logger logger = LoggerFactory.getLogger(BrsReaderController.class);

    private CodeReaderAdaptor codeReaderAdaptor;

    private BrsReaderListener brsReaderListener;

    private BrsReconnectionHandler brsReconnectionHandler = new BrsReconnectionHandler();

    private AtomicBoolean isConnected = new AtomicBoolean(false);


    public BrsReaderController(CodeReaderAdaptor codeReaderAdaptor, BrsReaderListener brsReaderListener) {
        this.codeReaderAdaptor = codeReaderAdaptor;
        this.brsReaderListener = brsReaderListener;
    }


    @Override
    public void onCodeReceived(String code) {
        EventBusService.post(new BrsProductEvent(code));
    }

    @Override
    public void onErrorReceived(String errorMsg) {
        //do nothing
    }

    @Override
    public void onDisconnection(boolean interrupted) {
        onBrsConnected(false);
    }

    @Override
    public void onLifecheck() {
        /* this lifecheck is done periodically,
        do nothing if is already connected */
        if (!isConnected.get()) {
            onBrsConnected(true);
        }
    }


    // PRIVATE METHODS

    private void onBrsConnected(boolean connected) {
        logger.debug("onBrsConnected {} ", connected);
        isConnected.set(connected);
        brsReaderListener.onBrsReaderConnected(connected, codeReaderAdaptor.getId());
        if (connected) {
            brsReconnectionHandler.stopReconnection();
        } else {
            codeReaderAdaptor.stop();
            brsReconnectionHandler.startReconnection();
        }
    }

    private class BrsReconnectionHandler {

        private Thread reconnectionHandler = new Thread();
        private boolean onReconnection = false;

        public void startReconnection() {
            if (!reconnectionHandler.isAlive()) {
                onReconnection = true;

                reconnectionHandler = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (onReconnection) {
//                            logger.debug("Trying to reconnect BRS..." + Thread.currentThread().getId());
//                            try {
//                                doConnect();
//                            } catch (DeviceException e) {
//                                logger.error("Error connecting to brs device", e.getMessage());
//                            }
//
//                            ThreadUtils.sleepQuietly(5000);
                        }
                        ;
                    }
                });

                reconnectionHandler.start();
            }
        }

        public void stopReconnection() {
            onReconnection = false;
        }
    }


}
