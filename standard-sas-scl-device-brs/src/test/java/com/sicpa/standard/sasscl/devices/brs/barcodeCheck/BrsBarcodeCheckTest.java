package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;


import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProduct;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;

import junit.framework.Assert;

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
        
        ApplicationFlowStateChangedEvent appStateEvent = buildApplicationFlowStateChangedEvent();
        brsBarcodeCheck.handleApplicationStateChange(appStateEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("12345");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);
        verify(barcodes, times(1)).contains("12345");
    }
    
    @Test
    public void onBrsCodeReceivedStringNOBARCODE() {
        when(compliantProduct.isCompliant(anyObject())).thenReturn(true);
        ProductionParametersEvent prodEvent = buildProductionParametersEventStringNOBARCODE();
        brsBarcodeCheck.onProductionParametersChanged(prodEvent);
        
        ApplicationFlowStateChangedEvent appStateEvent = buildApplicationFlowStateChangedEvent();
        brsBarcodeCheck.handleApplicationStateChange(appStateEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("12345");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);
        
        Assert.assertTrue(barcodes.size() > 0);
        Assert.assertTrue(barcodes.contains("NOBARCODE"));
        verify(barcodes, times(1)).contains("NOBARCODE");
    }
    
    @Test
    public void onBrsCodeReceivedStringNOBARCODEMatch() {
        when(compliantProduct.isCompliant(anyObject())).thenReturn(true);
        ProductionParametersEvent prodEvent = buildProductionParametersEventStringNOBARCODE();
        brsBarcodeCheck.onProductionParametersChanged(prodEvent);
        
        ApplicationFlowStateChangedEvent appStateEvent = buildApplicationFlowStateChangedEvent();
        brsBarcodeCheck.handleApplicationStateChange(appStateEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("NOBARCODE");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);
        
        Assert.assertTrue(barcodes.size() > 0);
        Assert.assertTrue(barcodes.contains("NOBARCODE"));
        Assert.assertTrue(barcodes.contains(brsProductEvent.getCode()));
    }
    
    @Test
    public void onBrsCodeReceivedNoBarcode() {
        when(compliantProduct.isCompliant(anyObject())).thenReturn(true);
        ProductionParametersEvent prodEvent = buildProductionParametersEventNoBarcode();
        brsBarcodeCheck.onProductionParametersChanged(prodEvent);
        
        ApplicationFlowStateChangedEvent appStateEvent = buildApplicationFlowStateChangedEvent();
        brsBarcodeCheck.handleApplicationStateChange(appStateEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("12345");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);
        
        Assert.assertTrue(barcodes.size() == 0);
        Assert.assertFalse(barcodes.contains("12345"));
        Assert.assertFalse(barcodes.contains(brsProductEvent.getCode()));
    }
    
    @Test
    public void onBrsCodeReceivedNotCompliantNoBarcode() {
        when(compliantProduct.isCompliant(anyObject())).thenReturn(false);
        ProductionParametersEvent prodEvent = buildProductionParametersEventNoBarcode();
        brsBarcodeCheck.onProductionParametersChanged(prodEvent);
        
        ApplicationFlowStateChangedEvent appStateEvent = buildApplicationFlowStateChangedEvent();
        brsBarcodeCheck.handleApplicationStateChange(appStateEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("12345");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);
        
        Assert.assertTrue(barcodes.size() == 0);
        Assert.assertFalse(barcodes.contains("12345"));
        Assert.assertFalse(barcodes.contains(brsProductEvent.getCode()));
    }

    @Test
    public void onBrsDisableCodeReceived() {
        when(compliantProduct.isCompliant(anyObject())).thenReturn(false);
        ProductionParametersEvent prodEvent = buildProductionParametersEvent();
        brsBarcodeCheck.onProductionParametersChanged(prodEvent);
        
        ApplicationFlowStateChangedEvent appStateEvent = buildApplicationFlowStateChangedEvent();
        brsBarcodeCheck.handleApplicationStateChange(appStateEvent);

        BrsProductEvent brsProductEvent = new BrsProductEvent("12345");
        brsBarcodeCheck.onBrsCodeReceived(brsProductEvent);
        verify(barcodes, times(0)).contains("12345");
    }

    private ProductionParametersEvent buildProductionParametersEvent() {
        List<String> barcodesExpected = Arrays.asList("12345", "123456");
        SKU sku = new SKU(1, "description", barcodesExpected);
        ProductionParameters prodParameter = new ProductionParameters();
        prodParameter.setSku(sku);
        return new ProductionParametersEvent(prodParameter);
    }
    
    private ProductionParametersEvent buildProductionParametersEventNoBarcode() {
        List<String> barcodesExpected = Arrays.asList();
        SKU sku = new SKU(1, "description", barcodesExpected);
        ProductionParameters prodParameter = new ProductionParameters();
        prodParameter.setSku(sku);
        return new ProductionParametersEvent(prodParameter);
    }
    
    private ProductionParametersEvent buildProductionParametersEventStringNOBARCODE() {
        List<String> barcodesExpected = Arrays.asList("NOBARCODE");
        SKU sku = new SKU(1, "description", barcodesExpected);
        ProductionParameters prodParameter = new ProductionParameters();
        prodParameter.setSku(sku);
        return new ProductionParametersEvent(prodParameter);
    }
    
    private ApplicationFlowStateChangedEvent buildApplicationFlowStateChangedEvent() {
    	return new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTING, ApplicationFlowState.STT_STARTED, "Started");
    }


}
