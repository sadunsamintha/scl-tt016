package com.sicpa.standard.sasscl.view.question;

import java.text.MessageFormat;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;

public class QuestionViewController implements IQuestionViewListener {

	public static final int FLOW_POSITION = 0;
	public static final int DISTANCE_VIEW_CONFIRM_POSITION = 1;
	public static final int CAMERA_DISPLAY_POSITION = 2;

	protected String message;

	protected QuestionViewModel model;

	public QuestionViewController() {
		this(new QuestionViewModel());
	}

	public QuestionViewController(QuestionViewModel model) {
		this.model = model;
	}

	/**
	 * Updates the model when a question has been asked to the user.
	 * 
	 * @param evt
	 *            Event that contains the question and the possible answers to it.
	 */
	@Subscribe
	public void questionAsked(final QuestionEvent evt) {
		String text = evt.getQuestion();
		if (message != null) {
			text = MessageFormat.format(text, message);
		}
		model.addQuestion(evt.getIndex(),
				new SingleQuestionViewModel(evt.getQuestion(), evt.getBackgrounColor(), evt.getAnswers()));
		model.notifyModelChanged();
	}

	/**
	 * Resets the model once the app user has chosen and answer.
	 */
	@Override
	public void questionAnswered(int questionIndex, final Answer answer) {
		model.reset(questionIndex);
		model.notifyModelChanged();
		answer.getCallback().run();
	}

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getMessage() != null) {
			message = evt.getMessage();
		}
	}

	public QuestionViewModel getModel() {
		return model;
	}
}
