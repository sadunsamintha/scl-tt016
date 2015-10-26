package com.sicpa.standard.sasscl.devices.brs;

import com.sicpa.common.device.reader.CodeReader;
import com.sicpa.common.device.reader.CodeReceiver;
import com.sicpa.common.device.reader.DisconnectionListener;
import com.sicpa.common.device.reader.lifecheck.EchoListener;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.common.util.ThreadUtils;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.brs.event.BrsStartFailedEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.model.BrsModel;
import com.sicpa.standard.sasscl.devices.brs.reader.CodeReaderAdaptor;
import com.sicpa.standard.sasscl.devices.brs.reader.BrsCodeReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BrsAdaptor extends AbstractStartableDevice implements CodeReceiver, DisconnectionListener, EchoListener {

    private static final Logger logger = LoggerFactory.getLogger(BrsAdaptor.class);

    private BrsModel brsModel;

    private final AtomicBoolean isConnected;

    private BrsReconnectionHandler brsReconnectionHandler = new BrsReconnectionHandler();

    private List<CodeReaderAdaptor> readers;


    private int brsLifeCheckInterval;

    private int brsLifeCheckTimeout;

    private int brsLifeCheckNumberOfRetries;


    public BrsAdaptor() {
        this(null);
    }

    public BrsAdaptor(BrsModel model) {
        super();
        setName("Brs");
        this.brsModel = model;
        isConnected = new AtomicBoolean(false);
        readers = new ArrayList<>();
    }

    @Override
    protected void doConnect() throws DeviceException {
        logger.debug("Establishing connection to BRS devices {}", brsModel);
        readers.clear();

        try {
            readers.addAll(BrsCodeReaderFactory.createReaders(this));
        } catch (IOException | URISyntaxException e) {
            logger.error("Error creating code readers {}", e.getMessage());
            onBrsConnected(false);
            return;
        }
        checkBrsConnectivity();
        onBrsConnected(true);
    }

    @Override
    protected void doDisconnect() throws DeviceException {
        logger.debug("do DISCONNECTED");
        doStop();
        fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
    }

    @Override
    public void doStart() throws DeviceException {
        if(!isConnected.get()) {
            EventBusService.post(new BrsStartFailedEvent(Messages.get("brs.start.failed.not.connected")));
            return;
        }
        try {
            enableBrsReading();
        } catch (IOException e) {
            logger.error("Error starting brs device", e.getMessage());
            EventBusService.post(new BrsStartFailedEvent(Messages.get("brs.start.failed")));
        }
        fireDeviceStatusChanged(DeviceStatus.STARTED);
    }

    @Override
    public void doStop() throws DeviceException {
        try {
            disableBrsReading();
        } catch (IOException e) {
            logger.error("Error stopping brs devices", e.getMessage());
        }
        fireDeviceStatusChanged(DeviceStatus.STOPPED);
    }

    @Override
    public void onCodeReceived(String s) {
        EventBusService.post(new BrsProductEvent(s));
    }


    @Override
    public void onDisconnection(boolean b) {
        isConnected.getAndSet(false);
        onBrsConnected(false);
    }

    @Override
    public boolean isBlockProductionStart() {
        return true;
    }


    @Override
    public void onErrorReceived(String s) {
    }

    @Override
    public void onLifecheck() {
        if (!isConnected.getAndSet(true)) {
            onBrsConnected(true);
        }
    }

    // Setters, Getters

    public int getBrsLifeCheckInterval() {
        return brsLifeCheckInterval;
    }

    public void setBrsLifeCheckInterval(int brsLifeCheckInterval) {
        this.brsLifeCheckInterval = brsLifeCheckInterval;
    }

    public int getBrsLifeCheckTimeout() {
        return brsLifeCheckTimeout;
    }

    public void setBrsLifeCheckTimeout(int brsLifeCheckTimeout) {
        this.brsLifeCheckTimeout = brsLifeCheckTimeout;
    }

    public int getBrsLifeCheckNumberOfRetries() {
        return brsLifeCheckNumberOfRetries;
    }

    public void setBrsLifeCheckNumberOfRetries(int brsLifeCheckNumberOfRetries) {
        this.brsLifeCheckNumberOfRetries = brsLifeCheckNumberOfRetries;
    }

    public BrsModel getBrsModel() {
        return brsModel;
    }


    // Private methods

    private void onBrsConnected(boolean connected) {
        if (connected) {
            logger.debug("BRS CONNECTED");
            fireDeviceStatusChanged(DeviceStatus.CONNECTED);
            brsReconnectionHandler.stopReconnection();
        } else {
            disconnectReaders();
            logger.debug("BRS DISCONNECTED");
            fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);

            brsReconnectionHandler.startReconnection();
        }
    }

    private void disconnectReaders() {
        for (CodeReader reader : readers) {
            reader.stop();
        }
    }

    private void checkBrsConnectivity() throws DeviceException {
        for(CodeReader reader : readers) {
            // Allow life check
            reader.start();
            // Prevent reading barcodes
            doStop();
        }
    }


    private void enableBrsReading() throws IOException {
        for (CodeReaderAdaptor reader : readers) {
            reader.sendEnableReadingCommand();
        }
    }

    private void disableBrsReading() throws IOException {
        for (CodeReaderAdaptor reader : readers) {
            reader.sendDisableReadingCommand();
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
                            logger.debug("Trying to reconnect BRS..." + Thread.currentThread().getId());
                            try {
                                doConnect();
                            } catch (DeviceException e) {
                                logger.error("Error connecting to brs device", e.getMessage());
                            }

                            ThreadUtils.sleepQuietly(5000);
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
