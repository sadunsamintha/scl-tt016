package com.sicpa.tt016.devices.plc;

import static com.sicpa.standard.gui.utils.ThreadUtils.waitForNextTimeStamp;
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

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.tt016.common.dto.SkuGrossNetProductCounterDTO;
import com.sicpa.tt016.monitoring.system.event.GrossNettCountSystemEvent;
import com.sicpa.tt016.storage.ITT016Storage;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STOPPING;

import lombok.Setter;

public class PlcPersistentGrossNetProductCounterManagerSCL {

    private final static Logger logger = LoggerFactory.getLogger(PlcPersistentGrossNetProductCounterManagerSCL.class);

    private PlcProvider plcProvider;
    private ProductionParameters productionParameters;
    private SubsystemIdProvider subsystemIdProvider;
    private ITT016Storage storage;
    
    private boolean getGrossNetProductCount_enabled;
    private String productionBehaviorVar;
    
    private String resetJavaProductCounterVarName;
    private String javaProductCounterNtfVarName;
    @Setter
    private String javaGrossNettCounterNtfVarName;
    
    private static HashMap<Integer, Integer> productCountLineMap = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> nettCountLineMap = new HashMap<Integer, Integer>();
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
	private static final String SCL_MODE = "PRODUCTIONCONFIG-SCL";
	
	private Integer totalGrossCount =  new Integer(0); // Total gross gross count for all lines.
	
	private Integer totalNettCount =  new Integer(0); // Total gross net count for all lines.
	
	
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
            int currentNettCount = 0;
    		
            IPlcVariable<Integer> javaProductCounterNtfVar = PlcVariable.createInt32Var(
                    replaceLinePlaceholder(javaProductCounterNtfVarName, lineIndex));
            currentProductCount = plcProvider.get().read(javaProductCounterNtfVar);
            
            if (null != productCountLineMap.get(lineIndex)) {
            	currentProductCount = currentProductCount - productCountLineMap.get(lineIndex);
            	productCountLineMap.put(lineIndex, productCountLineMap.get(lineIndex) + currentProductCount);
            } else {
            	productCountLineMap.put(lineIndex, currentProductCount);
            }
            
            IPlcVariable<Integer> javaGrossNettCounterNtfVar = PlcVariable.createInt32Var(
                    replaceLinePlaceholder(javaGrossNettCounterNtfVarName, lineIndex));
            currentNettCount = plcProvider.get().read(javaGrossNettCounterNtfVar);
            
            logger.debug("CurrentNettCount read from plc: "+currentNettCount+" for line: "+lineIndex);
            
            if (null != nettCountLineMap.get(lineIndex)) {
            	currentNettCount = currentNettCount - nettCountLineMap.get(lineIndex);
            	nettCountLineMap.put(lineIndex, nettCountLineMap.get(lineIndex) + currentNettCount);
            } else {
            	nettCountLineMap.put(lineIndex, currentNettCount);
            }
            
            logger.debug("CurrentNetCount after subtracting from nettCountLineMap: "+currentNettCount+" for line: "+lineIndex);
            
            Integer subsystemId = subsystemIdProvider.get().intValue();
            Integer skuId = null;
            Integer codetypeId = null;
            if(productionParameters.getSku()!=null) {
            	 skuId = productionParameters.getSku().getId();
            	 codetypeId = (int) productionParameters.getSku().getCodeType().getId();
            }
        	Date measurementDateTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        	String mode = Messages.get(productionParameters.getProductionMode().getDescription());
            
        	skuGrossNetProductCounter.setSubsystemId(subsystemId);
        	skuGrossNetProductCounter.setSkuId(skuId);
        	skuGrossNetProductCounter.setCodetypeId(codetypeId);
        	skuGrossNetProductCounter.setMeasurementDateTime(measurementDateTime);
        	skuGrossNetProductCounter.setMode(mode);
        	skuGrossNetProductCounter.setGross(currentProductCount);
        	skuGrossNetProductCounter.setNet(currentNettCount);
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
            sb.append(productionParameters.getSku()!=null?productionParameters.getSku().getDescription():null);
            
            sb.append(", Mode:");
            sb.append(mode);
            
            sb.append(", NumProducts:");
            sb.append(currentProductCount);
            
            sb.append(", NumNetProducts:");
            sb.append(currentNettCount);
            
            sb.append(",TotalNumProducts:");
            sb.append(productCountLineMap.get(lineIndex));
            
            sb.append(",TotalNumNetProducts:");
            sb.append(nettCountLineMap.get(lineIndex));
            
            logger.info(sb.toString());
            
            totalNettCount+=skuGrossNetProductCounter.getNet();
            totalGrossCount+=skuGrossNetProductCounter.getGross();
    	}
    	
    	if (productionBehaviorVar != null && productionBehaviorVar.toUpperCase().equals(SCL_MODE)) {
    		waitForNextTimeStamp();
        	storage.saveSkuGrossNet(skuGrossNetProductCounterList.toArray(new SkuGrossNetProductCounterDTO[skuGrossNetProductCounterList.size()]));
        	skuGrossNetProductCounterList.clear();
    	}
    	
    	 MonitoringService.addSystemEvent(new GrossNettCountSystemEvent(totalNettCount, totalGrossCount));
    	 totalNettCount = 0;
    	 totalGrossCount=0;
    }
    
	@Subscribe
	public void handleFlowControlStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(STT_STOPPING)) {
			try {
				this.log();
			} catch (PlcAdaptorException e) {
				logger.error("Plc exception: "+e.getMessage());
				e.printStackTrace();
			} catch (StorageException e) {
				logger.error("StorageException exception: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
    
    private void reset(int lineIndex) throws PlcAdaptorException {
    	logger.info("reset the JavaProductCounterVarName for line: "+lineIndex);
        IPlcVariable<Boolean> var = createBooleanVar(replaceLinePlaceholder(resetJavaProductCounterVarName, lineIndex));
        var.setValue(true);
        plcProvider.get().write(var);
        productCountLineMap.put(lineIndex, 0);
        nettCountLineMap.put(lineIndex, 0);
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
    
    public void setStorage(ITT016Storage storage) {
		this.storage = storage;
	}
    
    public void setGetGrossNetProductCount_enabled(boolean getGrossNetProductCount_enabled) {
		this.getGrossNetProductCount_enabled = getGrossNetProductCount_enabled;
	}

    public void setProductionBehaviorVar(String productionBehaviorVar) {
		this.productionBehaviorVar = productionBehaviorVar;
	}

	public void setJavaProductCounterNtfVarName(String javaProductCounterNtfVarName) {
        this.javaProductCounterNtfVarName = javaProductCounterNtfVarName;
    }

    public void setResetJavaProductCounterVarName(String resetJavaProductCounterVarName) {
        this.resetJavaProductCounterVarName = resetJavaProductCounterVarName;
    }
	
}