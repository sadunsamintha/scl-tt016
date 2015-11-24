package com.sicpa.standard.sasscl.devices.brs.alert;


import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.BrsConfig;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class ProductCountAlertTaskAbstractTest {

    @Mock
    private ProductionConfigProvider productionConfigProvider;

    @Mock
    private IProductionConfig productionConfig;



    protected AbstractBrsProductCountAlertTask task;

    public abstract AbstractBrsProductCountAlertTask instantiateAlertTask();

    public abstract void setUnreadBarcodesThreshold(int threshold);

    @Before
    public void setUp() {
        task = instantiateAlertTask();
        task.setProductionConfigProvider(productionConfigProvider);
        when(productionConfigProvider.get()).thenReturn(productionConfig);
        when(productionConfig.getBrsConfig()).thenReturn(new BrsConfig());
    }

    @Test
    public void alertIsPresent() {
        setUnreadBarcodesThreshold(10);

        final int numberOfCameraCodes = 11;
        receiveCameraCode(numberOfCameraCodes);
        Assert.assertTrue(task.isAlertPresent());
    }

    @Test
    public void alertIsNotPresent() {
        setUnreadBarcodesThreshold(10);

        final int numberOfCameraCodes = 9;
        receiveCameraCode(numberOfCameraCodes);
        Assert.assertFalse(task.isAlertPresent());
    }

    @Test
    public void alertIsNotPresentonBrsCodeReceived() {
        setUnreadBarcodesThreshold(10);

        final int numberOfCameraCodes = 9;
        receiveCameraCode(numberOfCameraCodes);
        task.onBrsCodeReceived(new BrsProductEvent("12345"));
        receiveCameraCode(numberOfCameraCodes);
        Assert.assertFalse(task.isAlertPresent());
    }




    private void receiveCameraCode(int numberOfCameraCodes) {
        for(int i =  0; i < numberOfCameraCodes; i++) {
            task.receiveCameraCode(new CameraGoodCodeEvent(null, null));
        }
    }

}
