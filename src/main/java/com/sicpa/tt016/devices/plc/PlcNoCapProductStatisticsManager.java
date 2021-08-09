package com.sicpa.tt016.devices.plc;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTED;

/**
 * This class will fetch the NoCapsProduct count from PLC and update the statistics of SCL production
 * 
 * generateBadProduct method as hardcoded QC property to Cognex since Morocco Camera job using Cognex and code property set 000 since all codes to consider as BAD.
 * 
 * @author ssomineni July 9th 2018 for producer ejection statistics no cap products
 * 
 * 
 */

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.tt016.model.TT016ProductStatus;

public class PlcNoCapProductStatisticsManager {

	private final static Logger logger = LoggerFactory.getLogger(PlcNoCapProductStatisticsManager.class);

	private PlcProvider plcProvider;
	private String plcNoCapProductNtfVarName;

	protected ProductionBatchProvider batchIdProvider;
	protected SubsystemIdProvider subsystemIdProvider;
	protected ProductionParameters productionParameters;
	protected ProductionConfigProvider productionConfigProvider;

	private final Map<Integer, Integer> previousPlcNoCapsCounterByLine = new HashMap<>();
	private AtomicBoolean isProductionStarted = new AtomicBoolean(false);
	private AtomicBoolean isfinalUpdateDoneAfterProductionStop = new AtomicBoolean(false);
	
	private final String PRODUCT_ERROR_CODE = "000";
	private final String QC_NAME = "Cognex";

	public void updateNoCapProductCount() {
		if(isfinalUpdateDoneAfterProductionStop.get()){
			Map<Integer, Integer> plcNoCapsCounterByLine = updateLocalPlcCounter();
			generateProducts(plcNoCapsCounterByLine);
			previousPlcNoCapsCounterByLine.putAll(plcNoCapsCounterByLine);
		}
	}

	protected void generateProducts(final Map<Integer, Integer> plcNoCapsCounterByLine) {

		for (Entry<Integer, Integer> plcNoCapCount : plcNoCapsCounterByLine.entrySet()) {
			int noCapProductCount = (previousPlcNoCapsCounterByLine != null
					&& previousPlcNoCapsCounterByLine.get(plcNoCapCount.getKey()) != null)
							? plcNoCapCount.getValue() - previousPlcNoCapsCounterByLine.get(plcNoCapCount.getKey())
							: plcNoCapCount.getValue();
			logger.info("Number of no cap products detected {} ", noCapProductCount);
			if (noCapProductCount > 0) {
				generateBadProduct(noCapProductCount, plcNoCapCount.getKey());
			}
		}
	}

	protected void generateBadProduct(int count, Integer lineIndex) {
		for (int i = 0; i < count; i++) {
			Product p = createProduct();
			p.setCode(new Code(PRODUCT_ERROR_CODE));
			p.setStatus(TT016ProductStatus.EJECTED_PRODUCER);
			p.setQc(QC_NAME);
			EventBusService.post(new NewProductEvent(p));
		}
	}

	protected Product createProduct() {
		Product p = new Product();
		p.setActivationDate(new Date());
		p.setProductionBatchId(batchIdProvider.get());
		p.setSubsystem(subsystemIdProvider.get());
		p.setSku(productionParameters.getSku());
		p.setPrinted(false);
		return p;
	}
	
	@Subscribe
	public void onProductionStarted(ApplicationFlowStateChangedEvent event) {
		
		if(event.getCurrentState().equals(STT_STARTED)){
			isProductionStarted.set(Boolean.TRUE);
			isfinalUpdateDoneAfterProductionStop.set(Boolean.TRUE);
			previousPlcNoCapsCounterByLine.clear();
		}else{
			isProductionStarted.set(Boolean.FALSE);
		}
	}
	
	public Map<Integer, Integer> updateLocalPlcCounter() {
		
		Map<Integer, Integer> plcNoCapsCounterByLine = new HashMap<>();
		
		if(!isProductionStarted.get()){
			isfinalUpdateDoneAfterProductionStop.set(Boolean.FALSE);
		}
			
		List<String> noCapsCounterVars = PlcLineHelper.getLinesVariableName(plcNoCapProductNtfVarName);

		for (String noCapsCounterVar : noCapsCounterVars) {
			try {
				plcNoCapsCounterByLine.put(PlcLineHelper.getLineIndex(noCapsCounterVar), readPlcVar(noCapsCounterVar));
			} catch (Exception e) {
				logger.error("Error reading PLC variable: {}", noCapsCounterVar);
			}
		}
		
		return plcNoCapsCounterByLine;
	}

	private int readPlcVar(String var) throws PlcAdaptorException {
		return plcProvider.get().read(PlcVariable.createInt32Var(var));
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setPlcNoCapProductNtfVarName(String plcNoCapProductNtfVarName) {
		this.plcNoCapProductNtfVarName = plcNoCapProductNtfVarName;
	}

	public ProductionBatchProvider getBatchIdProvider() {
		return this.batchIdProvider;
	}

	public void setBatchIdProvider(final ProductionBatchProvider batchIdProvider) {
		this.batchIdProvider = batchIdProvider;
	}

	public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
		this.subsystemIdProvider = subsystemIdProvider;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

}
