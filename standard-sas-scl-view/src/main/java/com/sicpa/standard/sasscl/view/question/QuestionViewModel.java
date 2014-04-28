package com.sicpa.standard.sasscl.view.question;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class QuestionViewModel extends AbstractObservableModel {

	protected Map<Integer, SingleQuestionViewModel> questions = new HashMap<Integer, SingleQuestionViewModel>();

	public void reset(int questionIndex) {
		questions.get(questionIndex).reset();
	}

	public void addQuestion(int index, SingleQuestionViewModel question) {
		questions.put(index, question);
	}

	public Map<Integer, SingleQuestionViewModel> getQuestions() {
		return questions;
	}
}
