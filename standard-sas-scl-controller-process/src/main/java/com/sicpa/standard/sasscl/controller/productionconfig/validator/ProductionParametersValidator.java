package com.sicpa.standard.sasscl.controller.productionconfig.validator;


import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.loader.IProductionConfigLoader;
import com.sicpa.standard.sasscl.controller.view.event.WarningViewEvent;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductionParametersValidator {

    private static final Logger logger = LoggerFactory.getLogger(ProductionParametersValidator.class);

    protected IProductionConfigLoader loader;

    public boolean validate(ProductionParameters productionParameters){
        return productionParameters == null ? false : checkConfigurationExists(productionParameters.getProductionMode());
    }

    private boolean checkConfigurationExists(ProductionMode productionMode) {
        IProductionConfig config = loader.get(productionMode);
        if (config == null) {
            logger.info("configuration_error,production_mode= {} reason=Configuration does not exist for this production mode",productionMode.getDescription());
            WarningViewEvent wve = new  WarningViewEvent("production.mode.config.does.not.exist",false, new Object[]{Messages.get(productionMode.getDescription())});
            EventBusService.post(wve);
            return false;
        }
        return true;
    }

    public boolean validate(ProductionMode productionMode){
       return checkConfigurationExists(productionMode);

    }



    public void setLoader(IProductionConfigLoader loader) {
        this.loader = loader;
    }
}
