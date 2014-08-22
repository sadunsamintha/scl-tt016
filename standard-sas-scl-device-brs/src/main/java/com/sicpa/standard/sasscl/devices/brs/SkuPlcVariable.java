package com.sicpa.standard.sasscl.devices.brs;

import com.sicpa.standard.plc.value.IPlcValue;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcString;
import com.sicpa.standard.sasscl.model.ProductionParameters;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.lang.StringUtils.EMPTY;

public class SkuPlcVariable implements IPlcVariable<String> {

    protected final ProductionParameters productionParameters;
    protected final String variableName;

    protected AtomicBoolean forwardProvidedSkuId = new AtomicBoolean(true);

    public SkuPlcVariable(ProductionParameters productionParameters, String variableName) {

        this.productionParameters = productionParameters;
        this.variableName = variableName;
    }

    @Override
    public String getVariableName() {

        return variableName;
    }

    @Override
    public Class<? extends IPlcValue<String>> getVariableType() {

        return PlcString.class;
    }

    @Override
    public Class<String> getValueType() {

        return String.class;
    }

    @Override
    public IPlcValue<String> getPlcValue() {

        String sku = getProvidedValue() + '\0';
        return new PlcString(sku, sku.length());
    }

    protected String getProvidedValue() {

        return isBarCodeAvailable() ? productionParameters.getSku().getBarCodes().iterator().next() : EMPTY;
    }

    protected boolean isBarCodeAvailable() {

        return forwardProvidedSkuId.get()
                && productionParameters.getSku()!= null
                && productionParameters.getSku().getBarCodes() != null
                && !productionParameters.getSku().getBarCodes().isEmpty();
    }

    @Override
    public void setPlcValue(IPlcValue<String> stringIPlcValue) {

        throw new IllegalStateException("Trying to set the PLC value of selected SKU");
    }

    @Override
    public String getValue() {

        return getProvidedValue();
    }

    @Override
    public void setValue(String value) {

        boolean reset = EMPTY.equals(value);
        forwardProvidedSkuId.compareAndSet(reset, !reset);
    }
}