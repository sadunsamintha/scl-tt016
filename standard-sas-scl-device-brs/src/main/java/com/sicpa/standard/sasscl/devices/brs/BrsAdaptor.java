package com.sicpa.standard.sasscl.devices.brs;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.model.BrsModel;
import com.sicpa.standard.sasscl.devices.brs.model.BrsReaderModel;
import com.sicpa.standard.sasscl.devices.brs.model.BrsType;
import com.sicpa.standard.sasscl.devices.brs.reader.BrsReaderController;
import com.sicpa.standard.sasscl.devices.brs.reader.CodeReaderController;
import com.sicpa.standard.sasscl.devices.brs.reader.CodeReaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BrsAdaptor extends AbstractStartableDevice implements CodeReaderListener {

    private static final Logger logger = LoggerFactory.getLogger(BrsAdaptor.class);

    private BrsModel brsModel;

    private List<CodeReaderController> readers;

    private int brsLifeCheckInterval;

    private int brsLifeCheckTimeout;

    private int brsLifeCheckNumberOfRetries;


    public BrsAdaptor() throws DeviceException {
        this(null);
    }

    public BrsAdaptor(BrsModel model) throws DeviceException {
        super();
        setName("Brs");
        this.brsModel = model;
        readers = new ArrayList<>();
    }


    @Override
    protected void doConnect() throws DeviceException {
        logger.debug("Establishing connection to BRS devices {}", brsModel);
        connectReaders();
    }

    @Override
    protected void doDisconnect() throws DeviceException {
        logger.debug("disconnecting brs devices");
        stopReaders();
        fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
    }

    @Override
    public void doStart() throws DeviceException {
        logger.debug("starting brs devices");
        startReaders();
        fireDeviceStatusChanged(DeviceStatus.STARTED);
    }

    @Override
    public void doStop() throws DeviceException {
        logger.debug("stopping brs devices");
        stopReaders();
        fireDeviceStatusChanged(DeviceStatus.STOPPED);
    }

    @Override
    public boolean isBlockProductionStart() {
        return true;
    }

    @Override
    public void onReaderConnected(boolean isConnected, String readerId) {
        if (isConnected && areAllConnected()) {
            try {
                stopReaders();
            } catch (DeviceException ex) {
                logger.error("Error stoping readers", ex.getMessage());
            }
            fireDeviceStatusChanged(DeviceStatus.CONNECTED);
        } else if (!isConnected) {
            fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
        }
    }

    @Override
    public void onCodeReceived(String code) {
        EventBusService.post(new BrsProductEvent(code));
    }

    private boolean areAllConnected() {
        for (CodeReaderController reader : readers) {
            if (!reader.isConnected()) {
                return false;
            }
        }
        return true;
    }

    // Setters, Getters

    public void setBrsLifeCheckInterval(int brsLifeCheckInterval) {
        this.brsLifeCheckInterval = brsLifeCheckInterval;
    }

    public void setBrsLifeCheckTimeout(int brsLifeCheckTimeout) {
        this.brsLifeCheckTimeout = brsLifeCheckTimeout;
    }

    public void setBrsLifeCheckNumberOfRetries(int brsLifeCheckNumberOfRetries) {
        this.brsLifeCheckNumberOfRetries = brsLifeCheckNumberOfRetries;
    }

    public void createReaders() throws DeviceException {
        if (brsModel == null)
            return; //do nothing

        readers.clear();
        for (String brsAddress : brsModel.getActiveAddresses()) {
            BrsReaderModel brsReaderModel = buildBrsReaderModel(brsAddress);
            CodeReaderController brsReader = new BrsReaderController(this, brsReaderModel);
            readers.add(brsReader);
        }
    }

    // PRIVATE METHODS

    private void stopAndDisconnectReaders() throws DeviceException {
        for (CodeReaderController reader : readers) {
            reader.stop(); // Prevent reading barcodes
            reader.disconnect();
        }
    }

    private void connectReaders() throws DeviceException {
        for (CodeReaderController reader : readers) {
            reader.connect();
        }
    }


    private BrsReaderModel buildBrsReaderModel(String brsAddress) {
        int port = Integer.valueOf(brsModel.getPort());
        BrsType brsType = BrsType.valueOf(brsModel.getBrsType().trim().toUpperCase());

        return new BrsReaderModel.BrsReaderModelBuilder(brsAddress, port, brsType)
                .brsLifeCheckInterval(brsLifeCheckInterval)
                .brsLifeCheckTimeout(brsLifeCheckTimeout)
                .brsLifeCheckNumberOfRetries(brsLifeCheckNumberOfRetries).build();
    }


    private void startReaders() throws DeviceException {
        for (CodeReaderController reader : readers) {
            reader.start();
        }
    }

    private void stopReaders() throws DeviceException {
        for (CodeReaderController reader : readers) {
            reader.stop();
        }
    }

}
