package com.sicpa.standard.sasscl.devices.brs.alert;

import com.google.common.eventbus.Subscribe;

import com.sicpa.standard.sasscl.business.alert.task.AbstractAlertTask;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProduct;
import com.sicpa.standard.sasscl.devices.brs.utils.ResettableAtomicCounter;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractBrsProductCountAlertTask extends AbstractAlertTask  {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBrsProductCountAlertTask.class);

    private ResettableAtomicCounter brsProductsDeltaCounter = new ResettableAtomicCounter();

    private boolean isUnreadBarcodesEnable;

    private boolean isSkuSelectedCompliantProduct = true;

    public abstract int getUnreadBarcodesThreshold();

    private ProductionConfigProvider productionConfigProvider;

    private CompliantProduct compliantProductResolver;



    @Subscribe
    public void receiveCameraCode(final CameraGoodCodeEvent evt) {
        increaseProductCount();
    }

    @Subscribe
    public void receiveCameraCodeError(final CameraBadCodeEvent evt) {
        increaseProductCount();
    }

    @Subscribe
    public void onProductionParametersChanged(ProductionParametersEvent evt) {
        reset();
        SKU sku = evt.getProductionParameters().getSku();
        if(sku != null ) {
            isSkuSelectedCompliantProduct = compliantProductResolver.isCompliant(sku);
        }
    }

    private void increaseProductCount() {
        brsProductsDeltaCounter.getNextValue();
        logger.debug("increasing brs delta counter. The Current value is {} ", brsProductsDeltaCounter.getValue());
        checkForMessage();
    }

    @Subscribe
    public void onBrsCodeReceived(BrsProductEvent evt) {
        brsProductsDeltaCounter.reset();
    }


    @Override
    public boolean isAlertPresent() {
        return brsProductsDeltaCounter.getValue() > getUnreadBarcodesThreshold();
    }

    @Override
    public boolean isEnabled() {
        boolean isBrsDeviceEnable =  productionConfigProvider.get().getBrsConfig() != null;
        return isBrsDeviceEnable && isUnreadBarcodesEnable &&  isSkuSelectedCompliantProduct;
    }


    @Override
    public void reset() {
        brsProductsDeltaCounter.reset();
    }

    public int getCountValue() {
        return brsProductsDeltaCounter.getValue();
    }


    public boolean isUnreadBarcodesEnable() {
        return isUnreadBarcodesEnable;
    }

    public void setIsUnreadBarcodesEnable(boolean isUnreadBarcodesEnable) {
        this.isUnreadBarcodesEnable = isUnreadBarcodesEnable;
    }

    public ProductionConfigProvider getProductionConfigProvider() {
        return productionConfigProvider;
    }

    public void setProductionConfigProvider(ProductionConfigProvider productionConfigProvider) {
        this.productionConfigProvider = productionConfigProvider;
    }

    public void setCompliantProductResolver(CompliantProduct compliantProductResolver) {
        this.compliantProductResolver = compliantProductResolver;
    }

}
