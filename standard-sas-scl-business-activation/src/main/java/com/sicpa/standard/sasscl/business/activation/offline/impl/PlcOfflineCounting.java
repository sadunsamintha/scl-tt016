package com.sicpa.standard.sasscl.business.activation.offline.impl;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.activation.offline.AbstractOfflineCounting;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.plc.value.PlcVariable.createInt32Var;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.getLineIndexes;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

public class PlcOfflineCounting extends AbstractOfflineCounting {

    private static final Logger logger = LoggerFactory.getLogger(PlcOfflineCounting.class);

    private PlcProvider plcProvider;

    private String quantityVarName;
    private String lastStopTimeVarName;
    private String lastProductTimeVarName;
    private String resetCountersVarName;

    @Override
    public void processOfflineCounting() {
        try {
            IPlcAdaptor plc = plcProvider.get();

            if (plc == null) {
                return;
            }

            List<Integer> lineIndexes = getLineIndexes();

            for (Integer lineIndex : lineIndexes) {
                Integer quantity = plc.read(createInt32Var(replaceLinePlaceholder(quantityVarName, lineIndex)));
                Integer lastStop = plc.read(createInt32Var(replaceLinePlaceholder(lastStopTimeVarName, lineIndex)));
                Integer lastProduct = plc.read(createInt32Var(replaceLinePlaceholder(lastProductTimeVarName, lineIndex)));

                if (quantity != null && lastProduct != null && lastStop != null && quantity > 0 && lastStop > 0
                        && lastProduct > 0) {
                    process(1000L * lastStop, 1000L * lastProduct, quantity);
                }

                reset(createBooleanVar(replaceLinePlaceholder(resetCountersVarName, lineIndex)));
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    protected void reset(IPlcVariable<Boolean> var) throws PlcAdaptorException {
        var.setValue(true);
        plcProvider.get().write(var);
    }

    public PlcProvider getPlcProvider() {
        return plcProvider;
    }

    public void setPlcProvider(final PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setQuantityVarName(String quantityVarName) {
        this.quantityVarName = quantityVarName;
    }

    public void setLastStopTimeVarName(String lastStopTimeVarName) {
        this.lastStopTimeVarName = lastStopTimeVarName;
    }

    public void setLastProductTimeVarName(String lastProductTimeVarName) {
        this.lastProductTimeVarName = lastProductTimeVarName;
    }

    public void setResetCountersVarName(String resetCountersVarName) {
        this.resetCountersVarName = resetCountersVarName;
    }
}