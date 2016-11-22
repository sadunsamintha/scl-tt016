package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;


import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProduct;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BrsBarcodeCheckTest {

    @Spy
    private Set<String> barcodes = new HashSet<>();

    @Mock
    private CompliantProduct compliantProduct;

    @InjectMocks
    private BrsBarcodeCheck brsBarcodeCheck = new BrsBarcodeCheck();


    @Test
    public void onProductionParametersChanged() {

        when(compliantProduct.isCompliant(anyObject())).thenReturn(true);
        ProductionParametersEvent event = buildProductionParametersEvent();
        brsBarcodeCheck.onProductionParametersChanged(event);

        Assert.assertTrue(barcodes.contains("12345"));
        Assert.assertTrue(barcodes.contains("123456"));
    }

    @Test
    public void onBrsCodeReceived() {
        when(compliantProduct.isCompliant(anyObject())).thenReturn(true);
        ProductionParametersEvent prodEvent = buildProductionParametersEvent();
        brsBarcodeCheck.onProductionParametersChanged(prodEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("12345");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);
        verify(barcodes, times(1)).contains("12345");

    }

    @Test
    public void onBrsDisableCodeReceived() {
        when(compliantProduct.isCompliant(anyObject())).thenReturn(false);
        ProductionParametersEvent prodEvent = buildProductionParametersEvent();
        brsBarcodeCheck.onProductionParametersChanged(prodEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("12345");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);
        verify(barcodes, times(0)).contains("12345");

    }

    @Test
    public void barCodeInvalid(){
        when(compliantProduct.isCompliant(anyObject())).thenReturn(true);
        ProductionParametersEvent prodEvent = buildProductionParametersEvent();
        brsBarcodeCheck.onProductionParametersChanged(prodEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("44444");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);

        Assert.assertFalse("Not should contain this barcode. ", barcodes.contains("44444"));
    }

    private ProductionParametersEvent buildProductionParametersEvent() {
        List<String> barcodesExpected = Arrays.asList("12345", "123456");
        SKU sku = new SKU(1, "description", barcodesExpected);
        ProductionParameters prodParameter = new ProductionParameters();
        prodParameter.setSku(sku);
        return new ProductionParametersEvent(prodParameter);
    }


}
