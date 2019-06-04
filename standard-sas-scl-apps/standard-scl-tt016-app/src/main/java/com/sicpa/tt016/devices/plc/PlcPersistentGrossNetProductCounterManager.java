package com.sicpa.tt016.devices.plc;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

public class PlcPersistentGrossNetProductCounterManager {

    private final static Logger logger = LoggerFactory.getLogger(PlcPersistentGrossNetProductCounterManager.class);

    private PlcProvider plcProvider;
    private ProductionParameters productionParameters;
    private SubsystemIdProvider subsystemIdProvider;

    private String javaProductCounterNtfVarName;
    private String resetJavaProductCounterVarName;
    private String javaEjectionCounterNtfVarName;
    private Integer grossNetProductCountSec;
    
    private static List<ProductionParameters> prodParamList = new ArrayList<ProductionParameters>();
    private static HashMap<Integer, Integer> totalProductCountLineMap = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> totalEjectionCountLineMap = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> totalGrossNetCountLineMap = new HashMap<Integer, Integer>();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public void updateProductParamAndCount() {
        try {
            for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
                if (productionParameters.getSku() != null) {
                	updateProductParamAndCount(lineIndex);
                }
                reset(lineIndex);
            }
        } catch (Exception e) {
            logger.error("Error reading PLC Java Product Counter", e);
        }
    }
    
    private void updateProductParamAndCount(int lineIndex) throws PlcAdaptorException {
    	ProductionParameters currentProdParam = new ProductionParameters(productionParameters.getProductionMode(), productionParameters.getSku(), productionParameters.getBarcode());
    	prodParamList.add(currentProdParam);
    	
    	initializeLineMap(lineIndex);
    	
    	int currentProductCount = 0;
    	int currentEjectionCount = 0;
    	int currentGrossNetCount = 0;
    	
    	IPlcVariable<Integer> javaProductCounterNtfVar = PlcVariable.createInt32Var(
                replaceLinePlaceholder(javaProductCounterNtfVarName, lineIndex));
    	currentProductCount = plcProvider.get().read(javaProductCounterNtfVar);
    	totalProductCountLineMap.put(lineIndex, totalProductCountLineMap.get(lineIndex) + currentProductCount);
    	
        IPlcVariable<Integer> javaEjectionCounterNtfVar = PlcVariable.createInt32Var(
                replaceLinePlaceholder(javaEjectionCounterNtfVarName, lineIndex));
        currentEjectionCount = plcProvider.get().read(javaEjectionCounterNtfVar);
        totalEjectionCountLineMap.put(lineIndex, totalEjectionCountLineMap.get(lineIndex) + currentEjectionCount);
        
        currentGrossNetCount = currentProductCount - currentEjectionCount;
        totalGrossNetCountLineMap.put(lineIndex, totalGrossNetCountLineMap.get(lineIndex) + currentGrossNetCount);
    }

    public void log() throws PlcAdaptorException {
    	for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
    		updateProductParamAndCount(lineIndex);
    		
    		StringBuilder sb = new StringBuilder();

            sb.append("Line:");
            sb.append(lineIndex);
            sb.append(", Date:");
            
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            
            sb.append(formatter.format(localDateTimeNow));
            
            sb.append(", SubSystemId:");
            sb.append(subsystemIdProvider.get());
            
            String noProdSelected = "None";
            String skuId = "";
            String skuDesc = "";
            String productionMode = "";
            
            if (null != prodParamList && !prodParamList.isEmpty()) {
            	List<String> skuIdList = new ArrayList<String>();
            	List<String> productionModeList = new ArrayList<String>();
            	
            	for (ProductionParameters prodParam : prodParamList) {
            		String currentSkuId = String.valueOf(prodParam.getSku().getId());
            		String currentSkuDesc = prodParam.getSku().getDescription();
            		String currentProductionMode = Messages.get(prodParam.getProductionMode().getDescription());
            		
        			if (isSkuNotYetSelected(skuIdList, currentSkuId)) {
        				skuId = skuId + currentSkuId + "; ";
                		skuDesc = skuDesc + currentSkuDesc + "; ";
                		
                		skuIdList.add(currentSkuId);
        			}
        			
        			if (isProductionModeNotYetSelected(productionModeList, currentProductionMode)) {
        				productionMode = productionMode + currentProductionMode + "; ";
        				
        				productionModeList.add(currentProductionMode);
        			}
        		}
            	
            	// Remove extra ";" at the end
            	skuId = skuId.substring(0, skuId.length() - 2);
            	skuDesc = skuDesc.substring(0, skuDesc.length() - 2);
            	productionMode = productionMode.substring(0, productionMode.length() - 2);
            } else {
            	skuId = noProdSelected;
                skuDesc = noProdSelected;
                productionMode = noProdSelected;
            }
            
            sb.append(", SkuId:");
            sb.append(skuId);
            
            sb.append(", Sku:");
            sb.append(skuDesc);
            
            sb.append(", Mode:");
            sb.append(productionMode);
            
            initializeLineMap(lineIndex);
            
            sb.append(", NumProducts:");
            sb.append(totalProductCountLineMap.get(lineIndex));

            sb.append(", NumEjections:");
            sb.append(totalEjectionCountLineMap.get(lineIndex));
            
            sb.append(", NumNet:");
            sb.append(totalGrossNetCountLineMap.get(lineIndex));
            
            int hours = grossNetProductCountSec / 3600;
            int minutes = (grossNetProductCountSec % 3600) / 60;
            int seconds = grossNetProductCountSec % 60;

            String timeDurationString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            
            sb.append(", TimeDuration:");
            sb.append(timeDurationString);
            
            logger.info(sb.toString());
    	}
        
        resetProductParamAndCount();
    }
    
    private void initializeLineMap(int lineIndex) {
    	if (null == totalProductCountLineMap.get(lineIndex)) {
    		totalProductCountLineMap.put(lineIndex, 0);
    	}
    	
    	if (null == totalEjectionCountLineMap.get(lineIndex)) {
    		totalEjectionCountLineMap.put(lineIndex, 0);
    	}
    	
    	if (null == totalGrossNetCountLineMap.get(lineIndex)) {
    		totalGrossNetCountLineMap.put(lineIndex, 0);
    	}
    }
    
    private void resetProductParamAndCount() {
    	prodParamList = new ArrayList<ProductionParameters>();
    	totalProductCountLineMap = new HashMap<Integer, Integer>();
    	totalEjectionCountLineMap = new HashMap<Integer, Integer>();
    	totalGrossNetCountLineMap = new HashMap<Integer, Integer>();
    }
    
    private boolean isSkuNotYetSelected(List<String> skuIdList, String currentSkuId) {
    	return !skuIdList.contains(currentSkuId);
    }
    
    private boolean isProductionModeNotYetSelected(List<String> productionModeList, String currentProductionMode) {
    	return !productionModeList.contains(currentProductionMode);
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

	public void setGrossNetProductCountSec(Integer grossNetProductCountSec) {
		this.grossNetProductCountSec = grossNetProductCountSec;
	}
}