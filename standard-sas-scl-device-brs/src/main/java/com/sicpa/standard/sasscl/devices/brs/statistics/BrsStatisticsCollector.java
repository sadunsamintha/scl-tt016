package com.sicpa.standard.sasscl.devices.brs.statistics;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class BrsStatisticsCollector implements BrsStatisticsListener {

    private static final Logger logger = LoggerFactory.getLogger(BrsStatisticsCollector.class);

    private BrsStatisticsModel statistics = new BrsStatisticsModel();

    @Override
    public void onBrsCodeReceived(String code) {
        statistics.incrementValueNumberBrsCodesCounted(code);
    }

    @Override
    public void onCodeReceived(String code) {
        statistics.incrementValueTotalNumberProductsCounted();
    }


    @Subscribe
    public void onBrsCodeReceived(BrsProductEvent event) {
        onBrsCodeReceived(event.getCode());
    }

    @Subscribe
    public void receiveCameraCode(final CameraGoodCodeEvent evt) {
        onCodeReceived(evt.getCode().getStringCode());
    }

    @Subscribe
    public void receiveCameraCodeError(final CameraBadCodeEvent evt) {
        onCodeReceived(evt.getCode().getStringCode());
    }

    private void logStatistics() {
        logger.info("Brs statistics :  {} ", statistics);
    }

}
