package com.sicpa.standard.sasscl.business.activation.offline.impl;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.activation.offline.AbstractOfflineCounting;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.getLineIndexes;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

public class PlcOfflineCounting extends AbstractOfflineCounting {

    private static final Logger logger = LoggerFactory.getLogger(PlcOfflineCounting.class);

    private PlcProvider plcProvider;

    private IPlcAdaptor plc;

    private PlcOfflineCountingValuesProvider plcOfflineCountingValuesProvider;

    private String resetCountersVarName;

    @Override
    public void processOfflineCounting() {
        try {
            plc = plcProvider.get();

            if (plc == null) {
                return;
            }

            List<Integer> lineIndexes = getLineIndexes();

            for (Integer lineIndex : lineIndexes) {
                Integer quantity = plcOfflineCountingValuesProvider.getQuantityProducts(lineIndex);
                Date lastStop = plcOfflineCountingValuesProvider.getLastStopDateTime(lineIndex);
                Date lastProduct = plcOfflineCountingValuesProvider.getLastProductDateTime(lineIndex);

                if (quantity > 0) {
                    process(lastStop, lastProduct, quantity);
                }

                resetPlcOfflineCounters(lineIndex);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void resetPlcOfflineCounters(Integer lineIndex) throws PlcAdaptorException {
        IPlcVariable<Boolean> var = createBooleanVar(replaceLinePlaceholder(resetCountersVarName, lineIndex));

        var.setValue(true);

        plc.write(var);
    }

    public void setPlcProvider(final PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setPlcOfflineCountingValuesProvider(PlcOfflineCountingValuesProvider plcOfflineCountingValuesProvider) {
        this.plcOfflineCountingValuesProvider = plcOfflineCountingValuesProvider;
    }

    public void setResetCountersVarName(String resetCountersVarName) {
        this.resetCountersVarName = resetCountersVarName;
    }
}