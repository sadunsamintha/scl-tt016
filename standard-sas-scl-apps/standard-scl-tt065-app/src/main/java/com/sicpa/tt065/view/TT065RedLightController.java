package com.sicpa.tt065.view;


import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;

import com.sicpa.standard.sasscl.view.MainFrameController;


import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@EnableScheduling
public class TT065RedLightController extends MainFrameController {
    private final static Logger logger = LoggerFactory.getLogger(TT065RedLightController.class);

    public long RedLightTimeCtn;
    public boolean isRedLight;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat dateFormatHour = new SimpleDateFormat("HH:MM:ss");

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        Date date = new Date();
        if (dateFormatHour.format(date).equals("23:59:59")){
            RedLightTimeCtn = 0;
        }
        if (isRedLight){
            RedLightTimeCtn++;
            logger.info(dateFormat.format(date)+ " - Total RedLight (seconds): " + RedLightTimeCtn);
        }
    }

    TT065RedLightController() {
    }

    @Subscribe
    public void processStateChanged(ApplicationFlowStateChangedEvent evt) {

        ThreadUtils.invokeLater(new Runnable() {
            @Override
            public void run() {

                if ((evt.getCurrentState() == STT_STARTING) || (evt.getCurrentState() == STT_STARTED)){
                    isRedLight = false;
                }else{
                    isRedLight = true;
                }
            }
        });
    }

}