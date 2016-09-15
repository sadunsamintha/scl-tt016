package com.sicpa.standard.sasscl.devices.brs.reader;


import com.sicpa.standard.common.util.ThreadUtils;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.brs.model.BrsReaderModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

public class BrsReaderController implements CodeReaderController {

    private static final Logger logger = LoggerFactory.getLogger(BrsReaderController.class);

    private CodeReaderAdaptor codeReaderAdaptor;

    private CodeReaderListener codeReaderListener;

    private final BrsReconnectionHandler brsReconnectionHandler = new BrsReconnectionHandler();

    private final AtomicBoolean isConnected = new AtomicBoolean(false);

    private BrsReaderModel brsReaderModel;

    public BrsReaderController(CodeReaderListener codeReaderListener, BrsReaderModel brsReaderModel) {
        this.codeReaderListener = codeReaderListener;
        this.brsReaderModel = brsReaderModel;
    }

    @Override
    public void onCodeReceived(String code) {
        codeReaderListener.onCodeReceived(code);
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

    @Override
    public void start() throws DeviceException {
        try {
            codeReaderAdaptor.sendEnableReadingCommand();
        } catch (IOException ex) {
            logger.error("Error starting BRS reader", ex.getMessage());
            throw new DeviceException(ex);
        }
    }

    @Override
    public void stop() throws DeviceException {
        try {
            codeReaderAdaptor.sendDisableReadingCommand();
        } catch (IOException ex) {
            logger.error("Error stoping BRS reader", ex.getMessage());
            throw new DeviceException(ex);
        }
    }

    @Override
    public void connect() throws DeviceException {
        createReader();
        if (codeReaderAdaptor != null) {
            codeReaderAdaptor.start(); // Allow life check
        }
    }

    @Override
    public void disconnect() throws DeviceException {
        codeReaderAdaptor.stop();
    }

    @Override
    public void setCodeReader(CodeReaderAdaptor codeReaderAdaptor) {
        this.codeReaderAdaptor = codeReaderAdaptor;
    }

    @Override
    public String getId() {
        return brsReaderModel.getAddress();
    }

    @Override
    public boolean isConnected() {
        return isConnected.get();
    }

    // PRIVATE METHODS

    private void createReader() throws DeviceException {
        try {
            codeReaderAdaptor = CodeReaderFactory.createCodeReaderAdaptor(brsReaderModel, this);
        } catch (ConnectException ex) {
            logger.error("Error connection to the brs reader {} ." +
                    " Let's try to create again the code reader", ex.getMessage());
            /* if during the process of initializing the codeReader the BRS is diconnected
               the lifecheck mechanism is not initialize in the driver and therefore
               we are not able to reconnect anymore to the device. Let's
               try to create again the code reader. */
            createReader();
        } catch (IOException | URISyntaxException ex) {
            logger.error("Error creating code readers {}", ex.getMessage());
            codeReaderAdaptor = null;
        }
    }


    private void onBrsConnected(boolean connected) {
        logger.debug("onBrsConnected {} ", connected);
        isConnected.set(connected);
        codeReaderListener.onReaderConnected(connected, brsReaderModel.getAddress());
        if (connected) {
            brsReconnectionHandler.stopReconnection();
        } else {
            brsReconnectionHandler.startReconnection();
        }
    }

    private class BrsReconnectionHandler {

        private Thread reconnectionHandler = new Thread();
        private volatile boolean onReconnection = false;

        public void startReconnection() {
            if (!reconnectionHandler.isAlive()) {
                onReconnection = true;

                reconnectionHandler = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (onReconnection) {
                            logger.debug("Trying to reconnect BRS..." + Thread.currentThread().getId());
                            try {
                                connect();
                            } catch (DeviceException ex) {
                                logger.error("Error on reconnection", ex.getMessage());
                            }
                            ThreadUtils.sleepQuietly(5000);
                        }
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
