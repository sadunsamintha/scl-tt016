package com.sicpa.standard.sasscl.devices.plc.alert;

import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.business.alert.task.model.NoCapsAlertTaskModel;
import com.sicpa.standard.sasscl.business.alert.task.scheduled.AbstractScheduledAlertTask;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoCapsAlertTask extends AbstractScheduledAlertTask {

    private static final Logger logger = LoggerFactory.getLogger(NoCapsAlertTask.class);

    private PlcProvider plcProvider;

    private NoCapsAlertTaskModel model;

    private String productCounterVarName;
    private String noCapsCounterVarName;

    private final Map<Integer, Integer> plcProductCounterByLine = new HashMap<>();
    private final Map<Integer, Integer> plcNoCapsCounterByLine = new HashMap<>();

    private final Map<Integer, Integer> previousPlcProductCounterByLine = new HashMap<>();
    private final Map<Integer, Integer> previousPlcNoCapsCounterByLine = new HashMap<>();

    private int alertPresentLineIndex;
    private int numProductsSinceLastThresholdCheck;
    private int numNoCapsProductsSinceLastThresholdCheck;

    @Override
    protected MessageEvent getAlertMessage() {
        return new MessageEvent(this, MessageEventKey.Alert.PLC_TOO_MANY_NO_CAPS, alertPresentLineIndex);
    }

    @Override
    protected boolean isAlertPresent() {
        updateLocalPlcCounters();

        if (isFirstTime()) {
            storeCurrentPlcCountersIntoPreviousPlcCounters();
            return false;
        }

        for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
            numProductsSinceLastThresholdCheck = getNumProductsSinceLastThresholdCheck(lineIndex);
            numNoCapsProductsSinceLastThresholdCheck = getNumNoCapsProductsSinceLastThresholdCheck(lineIndex);

            if (isSampleSizeReached()) {
                if (isThresholdReached()) {
                    alertPresentLineIndex = lineIndex;
                    return true;
                }

                storeCurrentPlcCountersIntoPreviousPlcCounters();
            }
        }

        return false;
    }

    @Override
    protected boolean isEnabledDefaultImpl() {
        return model.isEnabled();
    }

    @Override
    public String getAlertName() {
        return "Too many no caps products detected";
    }

    @Override
    public long getDelay() {
        return model.getDelayInSec();
    }

    @Override
    public void reset() {
        plcProductCounterByLine.clear();
        plcNoCapsCounterByLine.clear();

        previousPlcProductCounterByLine.clear();
        previousPlcNoCapsCounterByLine.clear();
    }

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setModel(NoCapsAlertTaskModel model) {
        this.model = model;
    }

    public void setProductCounterVarName(String productCounterVarName) {
        this.productCounterVarName = productCounterVarName;
    }

    public void setNoCapsCounterVarName(String noCapsCounterVarName) {
        this.noCapsCounterVarName = noCapsCounterVarName;
    }

    private int getNumProductsSinceLastThresholdCheck(int lineIndex) {
        return plcProductCounterByLine.get(lineIndex) - previousPlcProductCounterByLine.get(lineIndex);
    }

    private int getNumNoCapsProductsSinceLastThresholdCheck(int lineIndex) {
        return plcNoCapsCounterByLine.get(lineIndex) - previousPlcNoCapsCounterByLine.get(lineIndex);
    }

    private boolean isSampleSizeReached() {
        return numProductsSinceLastThresholdCheck >= model.getSampleSize();
    }

    private boolean isThresholdReached() {
        return numNoCapsProductsSinceLastThresholdCheck >= model.getThreshold();
    }

    private boolean isFirstTime() {
        return previousPlcProductCounterByLine.isEmpty() || previousPlcNoCapsCounterByLine.isEmpty();
    }

    private void storeCurrentPlcCountersIntoPreviousPlcCounters() {
        previousPlcProductCounterByLine.putAll(plcProductCounterByLine);
        previousPlcNoCapsCounterByLine.putAll(plcNoCapsCounterByLine);
    }

    private void updateLocalPlcCounters() {
        plcProductCounterByLine.clear();
        plcNoCapsCounterByLine.clear();

        List<String> productCounterVars = PlcLineHelper.getLinesVariableName(productCounterVarName);
        List<String> noCapsCounterVars = PlcLineHelper.getLinesVariableName(noCapsCounterVarName);

        for (String productCounterVar : productCounterVars) {
            try {
                plcProductCounterByLine.put(
                        PlcLineHelper.getLineIndex(productCounterVar),
                        readPlcVar(productCounterVar));
            } catch (Exception e) {
                logger.error("Error reading PLC variable: {}", productCounterVar);
            }
        }

        for (String noCapsCounterVar : noCapsCounterVars) {
            try {
                plcNoCapsCounterByLine.put(
                        PlcLineHelper.getLineIndex(noCapsCounterVar),
                        readPlcVar(noCapsCounterVar));
            } catch (Exception e) {
                logger.error("Error reading PLC variable: {}", noCapsCounterVar);
            }
        }
    }

    private int readPlcVar(String var) throws PlcAdaptorException {
        return plcProvider.get().read(PlcVariable.createInt32Var(var));
    }
}
