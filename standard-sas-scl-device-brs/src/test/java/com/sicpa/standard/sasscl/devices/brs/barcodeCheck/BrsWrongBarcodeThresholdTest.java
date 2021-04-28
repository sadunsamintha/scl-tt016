package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;


import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.common.util.ThreadUtils;
import com.sicpa.standard.sasscl.devices.brs.event.BrsWrongBarcodeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest(EventBusService.class)
public class BrsWrongBarcodeThresholdTest {

    private BrsWrongBarcodeThreshold threshold;


    @Before
    public void setUpStaticMock() throws Exception {
        PowerMockito.mockStatic(EventBusService.class);
        PowerMockito.doNothing().when(EventBusService.class, "post", any(MessageEvent.class));
    }


    @Test
    public void thresholdInactive() {

        int windowSize = 5000;
        int wrongSKUThreshold = 2;
        boolean thresholdActive = false;

        threshold =  new BrsWrongBarcodeThreshold(thresholdActive, wrongSKUThreshold, new BrsTimeWindow(windowSize));

        onWrongBarcodeReceived(1, 0);

        verityWrongSkuEventIsNotTrigger();
    }

    @Test
    public void thresholdReached() throws Exception {

        int windowSize = 5000;
        int wrongSKUThreshold = 1;
        boolean thresholdActive = true;

        threshold =  new BrsWrongBarcodeThreshold(thresholdActive, wrongSKUThreshold, new BrsTimeWindow(windowSize));

        onWrongBarcodeReceived(2, 0);

        verityWrongSkuEventIsTrigger();
    }

    @Test
    public void thresholdNotReached() throws Exception {

        int windowSize = 5000;
        int wrongSKUThreshold = 10;
        boolean thresholdActive = true;

        threshold =  new BrsWrongBarcodeThreshold(thresholdActive, wrongSKUThreshold, new BrsTimeWindow(windowSize));

        onWrongBarcodeReceived(9, 0);

        verityWrongSkuEventIsNotTrigger();
    }

    @Test
    public void thresholdNotReachedInAWindow() throws Exception {

        int windowSize = 1500;
        int wrongSKUThreshold = 2;
        boolean thresholdActive = true;

        threshold =  new BrsWrongBarcodeThreshold(thresholdActive, wrongSKUThreshold, new BrsTimeWindow(windowSize));

        onWrongBarcodeReceived(3, 1000);

        verityWrongSkuEventIsNotTrigger();
    }

    @Test
    public void thresholdReachedInAWindow() throws Exception {

        int windowSize = 5000;
        int wrongSKUThreshold = 2;
        boolean thresholdActive = true;

        threshold =  new BrsWrongBarcodeThreshold(thresholdActive, wrongSKUThreshold, new BrsTimeWindow(windowSize));

        onWrongBarcodeReceived(3, 1000);

        verityWrongSkuEventIsTrigger();
    }



    private void onWrongBarcodeReceived(int numberOfTimes, long sleepTimeBetweenCalls) {
        for(int i= 0; i< numberOfTimes; i++) {
            threshold.onWrongBarcodeReceived(new BrsWrongBarcodeEvent(new ArrayList<String>(), "1234"));
            ThreadUtils.sleepQuietly(sleepTimeBetweenCalls);
        }
    }

    private void verityWrongSkuEventIsTrigger() {
        //verify that the static method is call only ones
        PowerMockito.verifyStatic(times(1));
        EventBusService.post(any(MessageEvent.class));
    }

    private void verityWrongSkuEventIsNotTrigger() {
        //verify that the static method is never call
        PowerMockito.verifyStatic(never());
        EventBusService.post(any(MessageEvent.class));
    }

}
