package com.sicpa.tt065.view;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.view.MainFrameController;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class TT065RedLightController extends MainFrameController {
    private final static Logger logger = LoggerFactory.getLogger(TT065RedLightController.class);

    private long RedLightTimeCtn;
    private boolean isRedLight;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private DateFormat dateFormatHour = new SimpleDateFormat("HH:MM:ss");

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

    @Subscribe
    public void processStateChanged(ApplicationFlowStateChangedEvent evt) {

        ThreadUtils.invokeLater(() -> {
            ApplicationFlowState currentState = evt.getCurrentState();
            isRedLight = !(currentState == STT_STARTED);
        });
    }

}