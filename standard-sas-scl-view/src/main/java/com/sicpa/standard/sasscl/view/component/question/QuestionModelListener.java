package com.sicpa.standard.sasscl.view.component.question;

public interface QuestionModelListener {

	void questionChanged();

	void answersChanged();

	void backgroundChanged();

	void abort();

	void ask();

}
