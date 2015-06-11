package com.sicpa.standard.sasscl.view.question;

import com.sicpa.standard.client.common.view.question.Answer;


public interface IQuestionViewListener {

	void questionAnswered(int questionIndex,Answer answer);

}
