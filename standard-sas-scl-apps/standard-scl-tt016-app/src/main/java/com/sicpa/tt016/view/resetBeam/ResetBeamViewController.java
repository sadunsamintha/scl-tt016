package com.sicpa.tt016.view.resetBeam;


import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.question.Answer;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.event.UnlockFullScreenEvent;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.tt016.event.AutomatedBeamResetEvent;

public class ResetBeamViewController extends AbstractViewFlowController implements
        IResetBeamViewListener {

    private ResetBeamViewModel model;

    public ResetBeamViewController() {
        this(new ResetBeamViewModel());
    }

    public ResetBeamViewController(ResetBeamViewModel model) {
        this.model = model;
    }

    @Override
    public void reset() {
        EventBusService.post(new AutomatedBeamResetEvent());
    }

    public ResetBeamViewModel getModel() {
        return model;
    }

}
