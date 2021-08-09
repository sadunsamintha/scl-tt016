package com.sicpa.tt016.devices.plc;

import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.tt016.common.dto.SkuGrossNetProductCounterDTO;

public class PlcPersistentGrossNetProductCounterManager {

    private final static Logger logger = LoggerFactory.getLogger(PlcPersistentGrossNetProductCounterManager.class);

    private PlcProvider plcProvider;
    private ProductionParameters productionParameters;
    private SubsystemIdProvider subsystemIdProvider;
    
    private boolean getGrossNetProductCount_enabled;
    private String javaProductCounterNtfVarName;
    private String resetJavaProductCounterVarName;
    private String javaEjectionCounterNtfVarName;
    
    private static HashMap<Integer, Integer> productCountLineMap = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> ejectionCountLineMap = new HashMap<Integer, Integer>();
    
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
    
    public void log() throws PlcAdaptorException, StorageException {
    	if (!getGrossNetProductCount_enabled) {
    		return;
    	}
    	
    	LocalDateTime localDateTime = LocalDateTime.now();
    	List<SkuGrossNetProductCounterDTO> skuGrossNetProductCounterList = new ArrayList<SkuGrossNetProductCounterDTO>();
    	
    	for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
    		SkuGrossNetProductCounterDTO skuGrossNetProductCounter = new SkuGrossNetProductCounterDTO();
    		int currentProductCount = 0;
            int currentEjectionCount = 0;
            int currentGrossNetCount = 0;
    		
            IPlcVariable<Integer> javaProductCounterNtfVar = PlcVariable.createInt32Var(
                    replaceLinePlaceholder(javaProductCounterNtfVarName, lineIndex));
            currentProductCount = plcProvider.get().read(javaProductCounterNtfVar);
            
            if (null != productCountLineMap.get(lineIndex)) {
            	currentProductCount = currentProductCount - productCountLineMap.get(lineIndex);
            	productCountLineMap.put(lineIndex, productCountLineMap.get(lineIndex) + currentProductCount);
            } else {
            	productCountLineMap.put(lineIndex, currentProductCount);
            }
            
            IPlcVariable<Integer> javaEjectionCounterNtfVar = PlcVariable.createInt32Var(
                    replaceLinePlaceholder(javaEjectionCounterNtfVarName, lineIndex));
            currentEjectionCount = plcProvider.get().read(javaEjectionCounterNtfVar);
            
            if (null != ejectionCountLineMap.get(lineIndex)) {
            	currentEjectionCount = currentEjectionCount - ejectionCountLineMap.get(lineIndex);
            	ejectionCountLineMap.put(lineIndex, ejectionCountLineMap.get(lineIndex) + currentEjectionCount);
            } else {
            	ejectionCountLineMap.put(lineIndex, currentEjectionCount);
            }
            
            currentGrossNetCount = currentProductCount - currentEjectionCount;
            
            Integer subsystemId = subsystemIdProvider.get().intValue();
        	Integer skuId = productionParameters.getSku().getId();
        	Integer codetypeId = new Long(productionParameters.getSku().getCodeType().getId()).intValue();
        	Date measurementDateTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        	String mode = Messages.get(productionParameters.getProductionMode().getDescription());
        	Integer gross = currentProductCount;
        	Integer net = currentGrossNetCount;
            
        	skuGrossNetProductCounter.setSubsystemId(subsystemId);
        	skuGrossNetProductCounter.setSkuId(skuId);
        	skuGrossNetProductCounter.setCodetypeId(codetypeId);
        	skuGrossNetProductCounter.setMeasurementDateTime(measurementDateTime);
        	skuGrossNetProductCounter.setMode(mode);
        	skuGrossNetProductCounter.setGross(gross);
        	skuGrossNetProductCounter.setNet(net);
        	skuGrossNetProductCounterList.add(skuGrossNetProductCounter);
            
    		StringBuilder sb = new StringBuilder();

            sb.append("Line:");
            sb.append(lineIndex);
            
            sb.append(", Date:");
            sb.append(formatter.format(localDateTime));
            
            sb.append(", SubSystemId:");
            sb.append(subsystemId);
            
            sb.append(", SkuId:");
            sb.append(skuId);
            
            sb.append(", Sku:");
            sb.append(productionParameters.getSku().getDescription());
            
            sb.append(", Mode:");
            sb.append(mode);
            
            sb.append(", NumProducts:");
            sb.append(gross);

            sb.append(", NumEjections:");
            sb.append(currentEjectionCount);
            
            sb.append(", NumNet:");
            sb.append(net);
            
            logger.info(sb.toString());
    	}
    }
    
    private void reset(int lineIndex) throws PlcAdaptorException {
        IPlcVariable<Boolean> var = createBooleanVar(replaceLinePlaceholder(resetJavaProductCounterVarName, lineIndex));
        var.setValue(true);
        plcProvider.get().write(var);
        
        productCountLineMap.put(lineIndex, 0);
        ejectionCountLineMap.put(lineIndex, 0);
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
    
    public void setGetGrossNetProductCount_enabled(boolean getGrossNetProductCount_enabled) {
		this.getGrossNetProductCount_enabled = getGrossNetProductCount_enabled;
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