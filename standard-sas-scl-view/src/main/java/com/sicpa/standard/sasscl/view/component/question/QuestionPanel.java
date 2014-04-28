package com.sicpa.standard.sasscl.view.component.question;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.color.ColorUtil;
import org.pushingpixels.trident.Timeline;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton.Direction;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.view.question.Answer;

@SuppressWarnings("serial")
public class QuestionPanel extends JPanel {

	public static void main(String[] args) {
		SicpaLookAndFeel.install();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setSize(800, 600);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);

				final QuestionModel model = new QuestionModel();
				QuestionPanel q = new QuestionPanel(model);

				f.getLayeredPane().add(q);
				f.getLayeredPane().setLayer(q, JLayeredPane.POPUP_LAYER - 1);

				JPanel p = new JPanel();
				f.getLayeredPane().add(p);
				f.getLayeredPane().setLayer(p, JLayeredPane.POPUP_LAYER - 2);

				q.setBounds(0, 0, 400, 500);

				model.setAnswers(Arrays.asList(

				new Answer("111", new Runnable() {
					@Override
					public void run() {
					}
				}, true, "Are you sure?", true),

				new Answer("222", new Runnable() {
					@Override
					public void run() {
					}
				}, false)

				));
				model.setBackgroundColor(Color.WHITE);
				model.ask();

				f.getContentPane().setLayout(new MigLayout("fill"));
				f.getContentPane().add(new JScrollPane(new JTree()), "push,grow");
			}
		});

	}

	protected QuestionModel model;

	protected FirstQuestionPanel internalQuestionPanel;
	protected ConfirmationPanel confirmationPanel;
	protected JLayeredPane layeredPane;

	public QuestionPanel() {
		this(new QuestionModel());
	}

	public QuestionPanel(QuestionModel model) {
		setModel(model);
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill,gap 0 0 0 0 , insets 0 0 0 0"));
		add(getLayeredPane(), "push,grow");
		setOpaque(false);
	}

	public JLayeredPane getLayeredPane() {
		if (layeredPane == null) {
			layeredPane = new JLayeredPane();
			layeredPane.add(getInternalQuestionPanel());
			layeredPane.add(getConfirmationPanel());

			layeredPane.setLayer(getInternalQuestionPanel(), 1);
			layeredPane.setLayer(getConfirmationPanel(), 2);
			layeredPane.setOpaque(false);

			layeredPane.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					getInternalQuestionPanel().setSize(getSize());
					getConfirmationPanel().setSize(getSize());
				}
			});
		}
		return layeredPane;
	}

	protected Point clickedPoint;

	public FirstQuestionPanel getInternalQuestionPanel() {
		if (internalQuestionPanel == null) {
			internalQuestionPanel = new FirstQuestionPanel();
		}
		return internalQuestionPanel;
	}

	public ConfirmationPanel getConfirmationPanel() {
		if (confirmationPanel == null) {
			confirmationPanel = new ConfirmationPanel();
			confirmationPanel.setVisible(false);
		}
		return confirmationPanel;
	}

	public void setQuestion(String question) {
		getInternalQuestionPanel().getLabelQuestion().setText(question);
	}

	public void setbackgroundColor(Color backgroundColor) {
		getInternalQuestionPanel().getCollapsiblePanel().setColor(backgroundColor);
	}

	public void setAvailableAnswer(Collection<Answer> answers) {
		resetAnswer();
		for (Answer a : answers) {
			getInternalQuestionPanel().addAnswer(a);
		}
		revalidate();
	}

	protected void displayConfirmation(Answer answer) {
		getConfirmationPanel().setToConfirmed(answer);
	}

	protected void resetAnswer() {
		getInternalQuestionPanel().getPanelAnswer().removeAll();
	}

	public void ask() {
		setVisible(true);
		getInternalQuestionPanel().setVisible(true);
	}

	public void setModel(QuestionModel model) {
		if (this.model != model) {
			if (this.model != null) {
				model.removeListener(modelListener);
			}
			model.addListener(modelListener);
		}
		this.model = model;
	}

	protected void modelQuestionChanged() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				setQuestion(model.getQuestion());
			}
		});
	}

	protected void modelAnswersChanged() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				setAvailableAnswer(model.getAnswers());
			}
		});
	}

	protected void modelAbort() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(false);
			}
		});
	}

	protected void modelAsk() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				ask();
			}
		});
	}

	protected void modelBackgroundChanged() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getInternalQuestionPanel().getCollapseActionPanel().setColor(model.getBackgroundColor());
				getInternalQuestionPanel().getCollapsiblePanel().setColor(model.getBackgroundColor());
			}
		});
	}

	public QuestionModel getModel() {
		return model;
	}

	protected QuestionModelListener modelListener = new QuestionModelListener() {

		@Override
		public void questionChanged() {
			modelQuestionChanged();
		}

		@Override
		public void answersChanged() {
			modelAnswersChanged();
		}

		@Override
		public void abort() {
			modelAbort();
		}

		public void ask() {
			modelAsk();
		};

		@Override
		public void backgroundChanged() {
			modelBackgroundChanged();
		}
	};

	public void setAnimDuration(int animDuration) {
		getInternalQuestionPanel().animDuration = animDuration;
		getConfirmationPanel().animDuration = animDuration;
	}

	public static abstract class AbstractQuestionPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		protected boolean collapsed;
		protected int animDuration = 250;

		protected TranslucentPanel collapsiblePanel;
		protected JPanel panelAnswer;
		protected MultiLineLabel labelQuestion;
		protected JScrollPane scrollLabelQuestion;

		public AbstractQuestionPanel() {
			setOpaque(false);
			setLayout(new MigLayout("fill,gap 0 0 0 0 , insets 0 0 0 0"));
			add(getCollapsiblePanel(), "grow,push,wrap");
		}

		public MultiLineLabel getLabelQuestion() {
			if (labelQuestion == null) {
				labelQuestion = new MultiLineLabel("this should contain the question") {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean contains(int x, int y) {
						return false;
					}
				};
				labelQuestion.setFont(SicpaFont.getFont(30));
			}
			return labelQuestion;
		}

		public TranslucentPanel getCollapsiblePanel() {
			if (collapsiblePanel == null) {
				collapsiblePanel = new TranslucentPanel(new Color(255, 255, 149), 0.75f);
				collapsiblePanel.setMinimumSize(new Dimension());

				collapsiblePanel.setLayout(new MigLayout(""));
				collapsiblePanel.add(getLabelQuestion(), "span,center,push,growx,bottom");
				collapsiblePanel.add(getPanelAnswer(), "spanx,grow");

			}
			return collapsiblePanel;
		}

		public JPanel getPanelAnswer() {
			if (panelAnswer == null) {
				panelAnswer = new JPanel();
				panelAnswer.setLayout(new MigLayout("fill,wrap 3"));
				panelAnswer.setOpaque(false);
			}
			return panelAnswer;
		}

		protected void addAnswer(final Answer answer) {

			JButton b = new JButton(answer.getText());
			PaddedButton pb = new PaddedButton(b);

			if (answer.isRequireConfirmation()) {
				b.setBackground(SicpaColor.RED);
				pb.setEnabledColor(SicpaColor.RED);
			}
			if (answer.getBackgroundColor() != null) {
				b.setBackground(answer.getBackgroundColor());
				pb.setEnabledColor(answer.getBackgroundColor());
			}

			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					answerButtonActionPerformed(answer);
				}
			});
			b.setEnabled(answer.isEnabled());
			getPanelAnswer().add(pb, "sg 1,grow, h 70!");
		}

		protected abstract void answerButtonActionPerformed(Answer answer);

		public void resetAllAnswers() {
			getPanelAnswer().removeAll();
		}

		protected Dimension orignalSize;

		public void collapse(boolean flag) {
			if (t != null) {
				t.cancel();
			}
			if (flag) {
				collapse();
			} else {
				open();
			}
		}

		protected Timeline t = null;

		protected void collapse() {
			if (collapsed) {
				return;
			}
			collapsed = true;
			orignalSize = getSize();
			t = new Timeline(this);
			t.setDuration(animDuration);
			t.addPropertyToInterpolate("size", getSize(), new Dimension(getWidth(), 30));
			t.play();

		}

		protected void open() {
			if (!collapsed) {
				return;
			}
			collapsed = false;
			if (orignalSize == null) {
				return;
			}

			t = new Timeline(this);
			t.setDuration(animDuration);
			t.addPropertyToInterpolate("size", getSize(), orignalSize);
			t.play();
		}

		public void setSize(Dimension size) {
			super.setSize(size);
			revalidate();
		}
	}

	public class FirstQuestionPanel extends AbstractQuestionPanel {

		private static final long serialVersionUID = 1L;

		protected TranslucentPanel collapseActionPanel;
		protected DirectionButton buttonCollapse;

		public FirstQuestionPanel() {

			add(getCollapseActionPanel(), "grow, h 30!");

			addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					thisMouseDragged(e);
				}
			});

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					thisMousePressed(e);
				}
			});

		}

		public DirectionButton getButtonCollapse() {
			if (buttonCollapse == null) {
				buttonCollapse = new DirectionButton(Direction.UP);
				buttonCollapse.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonCollapseActionPerformed();
					}
				});
			}
			return buttonCollapse;
		}

		public TranslucentPanel getCollapseActionPanel() {
			if (collapseActionPanel == null) {
				collapseActionPanel = new TranslucentPanel(new Color(255, 255, 149), 0.75f) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void paintComponent(Graphics g) {
						if (collapsed) {
							Graphics2D g2 = (Graphics2D) g.create();

							Color colorWithHigherAlpha = ColorUtil.setAlpha(color, 50);

							g2.setPaint(new LinearGradientPaint(new Point(0, 0), new Point(0, getHeight()),
									new float[] { 0, 1 }, new Color[] { color, colorWithHigherAlpha }));
							g2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
							g2.fillRect(0, 0, getWidth(), getHeight());
							g2.dispose();
						} else {
							super.paintComponent(g);
						}
					}
				};
				collapseActionPanel.setLayout(new MigLayout("fill"));
				collapseActionPanel.add(getButtonCollapse(), "w 20!, east");
			}
			return collapseActionPanel;
		}

		protected void thisMouseDragged(MouseEvent e) {
			if (clickedPoint == null) {
				return;
			}
			int yDelta = e.getPoint().y - clickedPoint.y;
			if (yDelta > 10) {
				collapse(false);
				clickedPoint = null;
			} else if (yDelta < -10) {
				collapse(true);
				clickedPoint = null;
			}
		}

		protected void thisMousePressed(MouseEvent e) {
			clickedPoint = e.getPoint();
		}

		protected void buttonCollapseActionPerformed() {
			collapse(!collapsed);
		}

		protected void answerButtonActionPerformed(Answer answer) {
			if (answer.isRequireConfirmation()) {
				displayConfirmation(answer);
			} else {
				answer.getCallback().run();
				QuestionPanel.this.setVisible(false);
			}
		}

		protected void collapse() {
			super.collapse();
			getButtonCollapse().setDirection(Direction.DOWN);
		}

		protected void open() {
			super.open();
			getInternalQuestionPanel().getButtonCollapse().setDirection(Direction.UP);
		}
	}

	public class ConfirmationPanel extends AbstractQuestionPanel {
		private static final long serialVersionUID = 1L;
		protected Answer toConfirmed;

		public ConfirmationPanel() {
			// to block mouse input
			addMouseListener(new MouseAdapter() {
			});
		}

		public void setToConfirmed(Answer toConfirmed) {
			this.toConfirmed = toConfirmed;
			resetAllAnswers();
			getLabelQuestion().setText(this.toConfirmed.getConfirmationText());
			orignalSize = getInternalQuestionPanel().getSize();
			setSize(new Dimension(QuestionPanel.this.getWidth(), 0));
			setVisible(true);
			collapsed = true;
			open();
			initAnswers();
		}

		protected void initAnswers() {

			Answer confirm = new Answer(Messages.get("question.confirm.yes"), new Runnable() {
				@Override
				public void run() {
					toConfirmed.getCallback().run();
					QuestionPanel.this.setVisible(false);
					setVisible(false);
					collapse();
				}
			});
			confirm.setBackgroundColor(SicpaColor.RED);

			Answer cancel = new Answer(Messages.get("question.confirm.no"), new Runnable() {
				@Override
				public void run() {
					setVisible(false);
					collapse();
					QuestionPanel.this.internalQuestionPanel.setVisible(true);
				}
			});

			addAnswer(cancel);
			addAnswer(confirm);
		}

		@Override
		protected void answerButtonActionPerformed(Answer answer) {
			answer.getCallback().run();
		}
	}
}
