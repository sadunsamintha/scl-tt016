package com.sicpa.standard.sasscl.view.question;

import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.client.common.view.question.Answer;

@SuppressWarnings("serial")
public abstract class AbstractQuestionView extends AbstractView<IQuestionViewListener, QuestionViewModel> {

	protected IQuestionViewListener controller;

	/**
	 * Method that fires the method in the controller which is in charge of reseting the model once the question has
	 * been answered.
	 * 
	 * @param answer
	 *            Answer give by the app user.
	 */
	protected void fireAnswerSelected(int index, Answer answer) {
		synchronized (listeners) {
			for (IQuestionViewListener l : listeners) {
				l.questionAnswered(index, answer);
			}
		}
	}
}
