package com.sicpa.tt079.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt079.model.productionParameters.TT079Permission;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.setProductionModePermission;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_CONFIG_MAPPING;
import static com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider.productionBatchId;
import static com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider.productionExpdt;
import static com.sicpa.tt079.model.TT079ProductionMode.IMPORT;

public class TT079Bootstrap extends Bootstrap {

  @Override
  public void executeSpringInitTasks() {
    // Need to add Import Production Mode in the Permission and Config Mapping if will be required in the future
    // For now, only the Production Parameters are required to be loaded for use by KE SAS (EGMS-DTS integration)

//		addImportProductionMode();
    super.executeSpringInitTasks();
//		productionModePermission();
  }

  @Override
  protected void restorePreviousSelectedProductionParams() {
    ProductionParameters previous = storage.getSelectedProductionParameters();
    if (productionParametersValidator.validate(previous)) {
      productionParameters.setBarcode(previous.getBarcode());
      productionParameters.setSku(previous.getSku());
      productionParameters.setProductionMode(previous.getProductionMode());
      productionParameters.setProperty(productionBatchId, previous.getProperty(productionBatchId));
      productionParameters.setProperty(productionExpdt, previous.getProperty(productionExpdt));
      restoreStatistics();
      EventBusService.post(new ProductionParametersEvent(previous));
    }
  }

  private void addImportProductionMode() {
    IProductionConfigMapping mapping = BeanProvider.getBean(PRODUCTION_CONFIG_MAPPING);
    mapping.put(IMPORT, "import");
  }

  private void productionModePermission() {
    setProductionModePermission(IMPORT, TT079Permission.PRODUCTION_MODE_IMPORT);
  }
}