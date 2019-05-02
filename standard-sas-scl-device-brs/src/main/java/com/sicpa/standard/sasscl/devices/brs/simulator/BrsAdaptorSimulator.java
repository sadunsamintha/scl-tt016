package com.sicpa.standard.sasscl.devices.brs.simulator;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.brs.BrsAdaptor;
import com.sicpa.standard.sasscl.devices.brs.reader.CodeReaderController;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class BrsAdaptorSimulator extends BrsAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(BrsAdaptorSimulator.class);

    private BrsSimulatorConfig config;

    private ProductionParameters productionParameters;

    private boolean simulationMode = false;


    public BrsAdaptorSimulator(BrsSimulatorConfig config, ProductionParameters productionParameters, String behavior) throws DeviceException {
        this();
        this.config = config;
        this.productionParameters = productionParameters;
        this.simulationMode = behavior.equalsIgnoreCase("simulator");
    }

    public BrsAdaptorSimulator() throws DeviceException {
        super();
    }

    @Override
    protected void doConnect() throws DeviceException {
        EventBusService.post(new MessageEvent(MessageEventKey.Simulator.BRS));
        fireDeviceStatusChanged(DeviceStatus.CONNECTED);
    }

    @Override
    public void doStart() throws DeviceException {
        fireDeviceStatusChanged(DeviceStatus.STARTED);
    }

    @Override
    public void doStop() throws DeviceException {
        fireDeviceStatusChanged(DeviceStatus.STOPPED);
    }

    @Subscribe
    public void receiveCameraCode(final CameraGoodCodeEvent evt) {
        if (simulationMode) {
            simulateReceiveBarcode();
        }
    }

    @Subscribe
    public void receiveCameraCodeError(final CameraBadCodeEvent evt) {
        if (simulationMode) {
            simulateReceiveBarcode();
        }
    }

    private void simulateReceiveBarcode() {
        if (config.isSendWrongBarcode()) {
            onCodeReceived(generateWrongBarcode(), null);
            return;
        }
        if (config.isReachUnreadBarcodesThresholds()) {
            // simulate unread barcodes
            return;
        }
        onCodeReceived(getGoodBarcode(),null);
    }

    private String generateWrongBarcode() {
        String barcodes = String.join("", productionParameters.getSku().getBarCodes());
        String badBarcode = barcodes + RandomStringUtils.randomNumeric(5);
        return badBarcode;

    }

    private String getGoodBarcode() {
        List<String> barcodes = productionParameters.getSku().getBarCodes();
        return barcodes.get(new Random().nextInt(barcodes.size()));
    }


}