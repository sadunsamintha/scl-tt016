package com.sicpa.standard.sasscl.view.component.question;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sicpa.standard.sasscl.view.question.Answer;

public class QuestionModel {

	protected final List<QuestionModelListener> listeners = new ArrayList<QuestionModelListener>();

	protected String question;
	protected Collection<Answer> answers;
	protected Color backgroundColor;

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
		fireAnswersChanged();
	}

	public void setQuestion(String question) {
		this.question = question;
		fireQuestionChanged();
	}

	public void addListener(QuestionModelListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeListener(QuestionModelListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	public Collection<Answer> getAnswers() {
		return answers;
	}

	public String getQuestion() {
		return question;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		fireBackgroundChanged();
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	protected void fireQuestionChanged() {
		synchronized (listeners) {
			for (QuestionModelListener l : listeners) {
				l.questionChanged();
			}
		}
	}

	protected void fireAnswersChanged() {
		synchronized (listeners) {
			for (QuestionModelListener l : listeners) {
				l.answersChanged();
			}
		}
	}

	protected void fireBackgroundChanged() {
		synchronized (listeners) {
			for (QuestionModelListener l : listeners) {
				l.backgroundChanged();
			}
		}
	}

	public void abort() {
		synchronized (listeners) {
			for (QuestionModelListener l : listeners) {
				l.abort();
			}
		}
	}

	public void ask() {
		synchronized (listeners) {
			for (QuestionModelListener l : listeners) {
				l.ask();
			}
		}
	}
}
