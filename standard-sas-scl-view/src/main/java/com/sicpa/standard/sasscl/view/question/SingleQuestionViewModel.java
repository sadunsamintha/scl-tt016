package com.sicpa.standard.sasscl.view.question;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class SingleQuestionViewModel {
	protected String question;
	protected final List<Answer> answers = new ArrayList<Answer>();
	protected Color backgrounColor;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public SingleQuestionViewModel(String question, Color backgroundColor, List<Answer> answers) {
		setQuestion(question);
		setBackgrounColor(backgroundColor);
		setAnswer(answers);
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswer(List<Answer> answers) {
		this.answers.addAll(answers);
	}

	public void reset() {
		this.question = null;
		this.answers.clear();
	}

	public void setBackgrounColor(Color backgrounColor) {
		this.backgrounColor = backgrounColor;
	}

	public Color getBackgrounColor() {
		return backgrounColor;
	}
}
