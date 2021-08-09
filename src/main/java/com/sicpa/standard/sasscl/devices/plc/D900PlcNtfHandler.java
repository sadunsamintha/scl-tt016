package com.sicpa.standard.sasscl.devices.plc;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.tt016.storage.TT016FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.d900.D900CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.d900.D900CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.Alert.TOO_MANY_VIDI_WARNING;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.Alert.VIDI_CROSS_CHECK_FAILED;


public class D900PlcNtfHandler {

    private static final Logger logger = LoggerFactory.getLogger(D900PlcNtfHandler.class);

    private PlcProvider plcProvider;

    private int d900ProductCount;
    private int d900UnrecognizedCount;

    private int maxUnrecognizedCount;
    private int plcCrossCheckThreshold;

    public D900PlcNtfHandler() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(TT016FileStorage.GLOBAL_PROPERTIES_PATH));
        maxUnrecognizedCount = Integer.parseInt(prop.getProperty("d900.UnrecognizedCount.threshold"));
        plcCrossCheckThreshold = Integer.parseInt(prop.getProperty("d900.plc.CrossCheck.Threshold"));
    }

    @Subscribe
    public void handleLineStarting(ApplicationFlowStateChangedEvent evt) {
        if (evt.getCurrentState() == STT_STARTING) {
            this.d900ProductCount = 0;
            this.d900UnrecognizedCount = 0;
        }
    }

    @Subscribe
    public void handleD900GoodCode(D900CameraGoodCodeEvent evt) {
        this.d900ProductCount++;
        logger.info("D900 Good Code Added , Total Products="+this.d900ProductCount);
        if (isExceededPlcCrossThreshold()) {
            EventBusService.post(new MessageEvent(plcProvider.get(), VIDI_CROSS_CHECK_FAILED));
        }
    }

    @Subscribe
    public void handleD900BadCode(D900CameraBadCodeEvent evt) {
        this.d900ProductCount++;
        this.d900UnrecognizedCount++;
        logger.info("D900 BAD Code Counter "+this.d900UnrecognizedCount+", Total Products="+this.d900ProductCount);
        if (isExceededPlcCrossThreshold()) {
            EventBusService.post(new MessageEvent(plcProvider.get(), VIDI_CROSS_CHECK_FAILED));
        }

        if (this.d900UnrecognizedCount > this.maxUnrecognizedCount) {
            EventBusService.post(new MessageEvent(this, TOO_MANY_VIDI_WARNING));
        }
    }

    private int getD900PlcProductCount() throws PlcAdaptorException {
        IPlcVariable<Integer> d900PlcProductCount = PlcVariable.createInt32Var(
            //TODO:: Make this applicable for multiple lines.
            replaceLinePlaceholder(D900PlcEnums.PARAM_D900_PRODUCT_COUNT.getNameOnPlc(), 1));
        return plcProvider.get().read(d900PlcProductCount);
    }

    private boolean isExceededPlcCrossThreshold() {
        try {
            int d900PlcProductCount = getD900PlcProductCount();
            return Math.abs(d900ProductCount - d900PlcProductCount) > this.plcCrossCheckThreshold;
        } catch (PlcAdaptorException e) {
            logger.error("Unable to get D900 product count from PLC", e);
            return false;
        }
    }

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setMaxUnrecognizedCount(int maxUnrecognizedCount) {
        this.maxUnrecognizedCount = maxUnrecognizedCount;
    }

    public void setPlcCrossCheckThreshold(int plcCrossCheckThreshold) {
        this.plcCrossCheckThreshold = plcCrossCheckThreshold;
    }

}