package com.sicpa.standard.sasscl.devices.brs.alert;


import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class ProductCountAlertTaskAbstractTest {

    protected AbstractBrsProductCountAlertTask task;

    public abstract AbstractBrsProductCountAlertTask instantiateAlertTask();

    public abstract void setUnreadBarcodesThreshold(int threshold);

    @Before
    public void setUp() {
        task = instantiateAlertTask();
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
