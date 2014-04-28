package com.sicpa.standard.sasscl.view.question;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * event sent on the event bus when application required the operator to confirm an action
 * 
 * @author DIelsch
 * 
 */
public class QuestionEvent {

	protected int index;
	protected String question;
	protected List<Answer> answers;
	protected Color backgrounColor;

	public QuestionEvent(int index, String question, List<Answer> answers) {
		this.index = index;
		this.question = question;
		this.answers = answers;
		setBackgrounColor(new Color(255, 255, 149));
	}

	public QuestionEvent(String question, List<Answer> answers) {
		this(0, question, answers);
	}

	public QuestionEvent(int index, String question, Answer... answers) {
		this(index, question, toList(answers));
	}

	public QuestionEvent(String question, Answer... answers) {
		this(0, question, answers);
	}

	public static List<Answer> toList(Answer... answers) {
		List<Answer> res = new ArrayList<Answer>();
		for (Answer a : answers) {
			res.add(a);
		}
		return res;
	}

	public String getQuestion() {
		return question;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setBackgrounColor(Color backgrounColor) {
		this.backgrounColor = backgrounColor;
	}

	public Color getBackgrounColor() {
		return backgrounColor;
	}
}
