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

    private static Logger logger = LoggerFactory.getLogger(NoCapsAlertTask.class);

    private PlcProvider plcProvider;

    private NoCapsAlertTaskModel model;

    private String productCounterVarName;
    private String noCapsCounterVarName;

    private Map<Integer, Integer> plcProductCounterByLine = new HashMap<>();
    private Map<Integer, Integer> plcNoCapsCounterByLine = new HashMap<>();

    private Map<Integer, Integer> previousPlcProductCounterByLine = new HashMap<>();
    private Map<Integer, Integer> previousPlcNoCapsCounterByLine = new HashMap<>();

    private int alertPresentLineIndex;

    @Override
    protected MessageEvent getAlertMessage() {
        return new MessageEvent(this, MessageEventKey.Alert.PLC_TOO_MANY_NO_CAPS, alertPresentLineIndex);
    }

    @Override
    protected boolean isAlertPresent() {
        updateLocalPlcCounters();

        if (previousPlcProductCounterByLine.isEmpty() || previousPlcNoCapsCounterByLine.isEmpty()) {
            previousPlcProductCounterByLine.putAll(plcProductCounterByLine);
            previousPlcNoCapsCounterByLine.putAll(plcNoCapsCounterByLine);
            return false;
        }

        boolean isAlertPresent = false;

        for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
            int numProductsSinceLastThresholdCheck = getNumProductsSinceLastThresholdCheck(lineIndex);
            int numNoCapsProductsSinceLastThresholdCheck = getNumNoCapsProductsSinceLastThresholdCheck(lineIndex);

            if (numProductsSinceLastThresholdCheck >= model.getSampleSize()
                    && numNoCapsProductsSinceLastThresholdCheck >= model.getThreshold()) {
                alertPresentLineIndex = lineIndex;
                isAlertPresent = true;

                previousPlcProductCounterByLine.putAll(plcProductCounterByLine);
                previousPlcNoCapsCounterByLine.putAll(plcNoCapsCounterByLine);
            }
        }

        return isAlertPresent;
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
