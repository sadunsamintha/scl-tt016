package com.sicpa.standard.sasscl.devices.brs.skuCheck;


import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BrsSkuCheckTest {

    @Spy
    private Set<String> barcodes = new HashSet<>();

    @InjectMocks
    private BrsSkuCheck brsSkuCheck = new BrsSkuCheck();


    @Test
    public void onProductionParametersChanged() {

        ProductionParametersEvent event = buildProductionParametersEvent();
        brsSkuCheck.onProductionParametersChanged(event);

        Assert.assertTrue(barcodes.contains("12345"));
        Assert.assertTrue(barcodes.contains("123456"));
    }

    @Test
    public void onBrsCodeReceived() {
        ProductionParametersEvent prodEvent = buildProductionParametersEvent();
        brsSkuCheck.onProductionParametersChanged(prodEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("12345");
        brsSkuCheck.onBrsCodeReceived(brsProductEvent);
        verify(barcodes, times(1)).contains("12345");

    }

    private ProductionParametersEvent buildProductionParametersEvent() {
        List<String> barcodesExpected = Arrays.asList("12345", "123456");
        SKU sku = new SKU(1, "description", barcodesExpected);
        ProductionParameters prodParameter = new ProductionParameters();
        prodParameter.setSku(sku);
        return new ProductionParametersEvent(prodParameter);
    }


}
