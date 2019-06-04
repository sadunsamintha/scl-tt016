package com.sicpa.tt016.devices.plc;

import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;

public class PlcPersistentGrossNetProductCounterManager {

    private final static Logger logger = LoggerFactory.getLogger(PlcPersistentGrossNetProductCounterManager.class);

    private PlcProvider plcProvider;
    private ProductionParameters productionParameters;
    private SubsystemIdProvider subsystemIdProvider;

    private String javaProductCounterNtfVarName;
    private String resetJavaProductCounterVarName;
    private String javaEjectionCounterNtfVarName;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public void updateProductParamAndCount() {
        try {
            for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
                reset(lineIndex);
            }
        } catch (Exception e) {
            logger.error("Error reading PLC Java Product Counter", e);
        }
    }
    
    public void log() throws PlcAdaptorException {
    	for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
    		int currentProductCount = 0;
            int currentEjectionCount = 0;
            int currentGrossNetCount = 0;
    		
    		StringBuilder sb = new StringBuilder();

            sb.append("Line:");
            sb.append(lineIndex);
            sb.append(", Date:");
            sb.append(formatter.format(LocalDateTime.now()));
            
            sb.append(", SubSystemId:");
            sb.append(subsystemIdProvider.get());
            
            sb.append(", SkuId:");
            sb.append(productionParameters.getSku().getId());
            
            sb.append(", Sku:");
            sb.append(productionParameters.getSku().getDescription());
            
            sb.append(", Mode:");
            sb.append(Messages.get(productionParameters.getProductionMode().getDescription()));
            
            sb.append(", NumProducts:");
            IPlcVariable<Integer> javaProductCounterNtfVar = PlcVariable.createInt32Var(
                    replaceLinePlaceholder(javaProductCounterNtfVarName, lineIndex));
            currentProductCount = plcProvider.get().read(javaProductCounterNtfVar);
            sb.append(currentProductCount);

            sb.append(", NumEjections:");
            IPlcVariable<Integer> javaEjectionCounterNtfVar = PlcVariable.createInt32Var(
                    replaceLinePlaceholder(javaEjectionCounterNtfVarName, lineIndex));
            currentEjectionCount = plcProvider.get().read(javaEjectionCounterNtfVar);
            sb.append(currentEjectionCount);
            
            currentGrossNetCount = currentProductCount - currentEjectionCount;
            
            sb.append(", NumNet:");
            sb.append(currentGrossNetCount);
            
            logger.info(sb.toString());
    	}
    }
    
    private void reset(int lineIndex) throws PlcAdaptorException {
        IPlcVariable<Boolean> var = createBooleanVar(replaceLinePlaceholder(resetJavaProductCounterVarName, lineIndex));
        var.setValue(true);
        plcProvider.get().write(var);
    }

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }

    public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
        this.subsystemIdProvider = subsystemIdProvider;
    }

    public void setJavaProductCounterNtfVarName(String javaProductCounterNtfVarName) {
        this.javaProductCounterNtfVarName = javaProductCounterNtfVarName;
    }

    public void setResetJavaProductCounterVarName(String resetJavaProductCounterVarName) {
        this.resetJavaProductCounterVarName = resetJavaProductCounterVarName;
    }

    public void setJavaEjectionCounterNtfVarName(String javaEjectionCounterNtfVarName) {
        this.javaEjectionCounterNtfVarName = javaEjectionCounterNtfVarName;
    }
}