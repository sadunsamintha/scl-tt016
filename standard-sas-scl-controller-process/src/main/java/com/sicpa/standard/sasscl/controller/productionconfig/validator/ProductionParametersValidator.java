package com.sicpa.standard.sasscl.controller.productionconfig.validator;



import com.sicpa.standard.client.common.eventbus.service.EventBusService;
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
        if (productionParameters !=null) {
            return _validate(productionParameters.getProductionMode());
        }
        return false;
    }

    private boolean _validate(ProductionMode productionMode) {
        IProductionConfig config = loader.get(productionMode);
        if (config == null) {
            logger.info("configuration_error,production_mode=" + productionMode + "reason=Configuration does not exist for this production mode");
            WarningViewEvent wve = new  WarningViewEvent("production.mode.config.does.not.exist",false, new Object[]{productionMode.getDescription()});
            EventBusService.post(wve);
            return false;
        }
        return true;
    }

    public boolean validate(ProductionMode productionMode){
       return _validate(productionMode);

    }



    public void setLoader(IProductionConfigLoader loader) {
        this.loader = loader;
    }
}
