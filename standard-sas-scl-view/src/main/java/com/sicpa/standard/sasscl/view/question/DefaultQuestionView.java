package com.sicpa.standard.sasscl.view.question;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.component.question.QuestionModelAdapter;
import com.sicpa.standard.sasscl.view.component.question.QuestionPanel;

@SuppressWarnings("serial")
public class DefaultQuestionView extends AbstractQuestionView {

	protected Map<Integer, QuestionPanel> panels = new HashMap<Integer, QuestionPanel>();
	protected int layerPosition;

	protected class WrappedAnswer extends Answer {

		public WrappedAnswer(final int index, final Answer answer) {
			super(answer.getText(), new Runnable() {
				@Override
				public void run() {
					fireAnswerSelected(index, answer);
				}
			});

			this.requireConfirmation = answer.isRequireConfirmation();
			this.confirmationText = answer.getConfirmationText();
			this.enabled = answer.isEnabled();
			this.backgroundColor = answer.getBackgroundColor();
		}
	}

	protected QuestionPanel getPanel(int index) {
		QuestionPanel panel = panels.get(index);
		if (panel == null) {
			panel = new QuestionPanel();
			panels.put(index, panel);
			init(panel, index);
		}
		return panel;
	}

	public DefaultQuestionView() {
	}

	public void ask(int index, String question, Color backgroundColor, List<Answer> answers) {

		List<Answer> wrappedAnswers = new ArrayList<Answer>();
		for (Answer answer : answers) {
			wrappedAnswers.add(new WrappedAnswer(index, answer));
		}

		getPanel(index).getModel().setAnswers(wrappedAnswers);
		getPanel(index).getModel().setBackgroundColor(backgroundColor);
		getPanel(index).getModel().setQuestion(question);
		getPanel(index).getModel().ask();

		getPanel(index).setVisible(true);
	}

	protected void hidePanel(int index) {
		getPanel(index).setVisible(false);
	}

	protected void init(final QuestionPanel panel, int index) {
		getMainView().getLayeredPane().add(panel);
		getMainView().getLayeredPane().setLayer(panel, JLayeredPane.FRAME_CONTENT_LAYER + 1 + index);
		panel.getModel().addListener(new QuestionModelAdapter() {
			@Override
			public void ask() {
				questionAsked(panel);
			}
		});
	}

	protected void questionAsked(QuestionPanel panel) {
		AbstractMachineFrame frame = getMainView();
		Rectangle bounds = SwingUtilities.convertRectangle(frame.getConfigPanel(), frame.getConfigPanel().getBounds(),
				frame);
		panel.setBounds(bounds);
	}

	/**
	 * Method that if a question has been asked, will display the Question Screen and orders the model to be updated.
	 */
	@Override
	public void modelChanged() {

		for (Entry<Integer, SingleQuestionViewModel> entry : model.getQuestions().entrySet()) {
			SingleQuestionViewModel model = entry.getValue();
			if (model.getQuestion() == null) {
				hidePanel(entry.getKey());
			} else {
				ask(entry.getKey(), model.getQuestion(), model.getBackgrounColor(), model.getAnswers());
			}
		}
	}

	public void setLayerNumber(int layerNumber) {
		this.layerPosition = layerNumber;
	}

	protected MainFrame getMainView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof MainFrame) {
				return (MainFrame) f;
			}
		}
		return null;
	}
}
