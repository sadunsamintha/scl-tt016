package com.sicpa.tt080.scl;


import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt080.scl.provider.impl.TT080ProductionBatchProvider;

import static com.sicpa.standard.sasscl.provider.ProductBatchIdProvider.productionBatchId;

public class TT080Bootstrap extends Bootstrap{

  private TT080ProductionBatchProvider productionBatchProvider;

  @Override
  public void executeSpringInitTasks(){
    super.executeSpringInitTasks();
  }

  @Override
  protected void restorePreviousSelectedProductionParams() {
    ProductionParameters previous = storage.getSelectedProductionParameters();
    if (productionParametersValidator.validate(previous)) {
      productionParameters.setBarcode(previous.getBarcode());
      productionParameters.setSku(previous.getSku());
      productionParameters.setProductionMode(previous.getProductionMode());
      productionParameters.setProperty(productionBatchId, previous.getProperty(productionBatchId));
      restoreStatistics();
      EventBusService.post(new ProductionParametersEvent(previous));

      productionBatchProvider.set(previous.getProperty(productionBatchId).getProductionBatchId());
    }
  }

  public void setStorage(IStorage storage) {
    this.storage = storage;
  }

  public void setProductionBatchProvider(TT080ProductionBatchProvider productionBatchProvider) {
    this.productionBatchProvider = productionBatchProvider;
  }
}
