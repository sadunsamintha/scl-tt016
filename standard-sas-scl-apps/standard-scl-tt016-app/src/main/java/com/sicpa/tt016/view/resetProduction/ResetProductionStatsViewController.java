package com.sicpa.tt016.view.resetProduction;


import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.question.Answer;
import com.sicpa.standard.client.common.view.question.QuestionEvent;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.event.LockFullScreenEvent;
import com.sicpa.standard.sasscl.event.UnlockFullScreenEvent;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;

public class ResetProductionStatsViewController extends AbstractViewFlowController implements
        IResetProductionStatsViewListener {

    private ResetProductionStatsViewModel model;

    private IStatistics statistics;

    public ResetProductionStatsViewController() {
        this(new ResetProductionStatsViewModel());
    }

    public ResetProductionStatsViewController(ResetProductionStatsViewModel model) {
        this.model = model;
    }


    @Override
    public void reset() {
        EventBusService.post(new LockFullScreenEvent());
        String questionText = Messages.get("question.reset.text");
        QuestionEvent evt = new QuestionEvent(questionText, createCancelAnswer(), createResetAnswer());
        EventBusService.post(evt);
    }

    private Answer createResetAnswer() {
        Answer reset = new Answer(Messages.get("question.reset.button.yes"), () -> {
            OperatorLogger.log("resetting production statistics");
            statistics.reset();
            EventBusService.post(new UnlockFullScreenEvent());
        });
        reset.setBackgroundColor(SicpaColor.RED);
        return reset;
    }

    private Answer createCancelAnswer() {
        Answer cancel = new Answer(Messages.get("question.reset.button.no"),
                () -> EventBusService.post(new UnlockFullScreenEvent()));
        return cancel;
    }

    public ResetProductionStatsViewModel getModel() {
        return model;
    }

    public void setStatistics(IStatistics statistics) {
        this.statistics = statistics;
    }

}
