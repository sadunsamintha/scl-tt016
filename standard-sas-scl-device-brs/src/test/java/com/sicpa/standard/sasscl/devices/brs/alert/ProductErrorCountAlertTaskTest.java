package com.sicpa.standard.sasscl.devices.brs.alert;


import com.sicpa.standard.sasscl.messages.MessageEventKey;
import junit.framework.Assert;
import org.junit.Test;


public class ProductErrorCountAlertTaskTest extends  ProductCountAlertTaskAbstractTest{


    @Override
    public AbstractBrsProductCountAlertTask instantiateAlertTask() {
        return new ProductErrorCountAlertTask();
    }

    @Override
    public void setUnreadBarcodesThreshold(int threshold) {
        //safe cast
        ((ProductErrorCountAlertTask)task).setUnreadBarcodesErrorThreshold(threshold);

    }

    @Test
    public void getAlertMessage() {
        Assert.assertEquals(MessageEventKey.BRS.BRS_TOO_MANY_UNREAD_BARCODES_ERROR, ((ProductErrorCountAlertTask)task).getAlertMessage().getKey());
    }

    @Test
    public void getAlertName() {
        Assert.assertEquals("Brs Product Error Count Alert", task.getAlertName());

    }
}
