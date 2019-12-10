package com.sicpa.standard.gui.screen.machine.component.confirmation;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXLabel;

import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultConfirmationPanel extends AbstractConfirmationPanel {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(500, 500);

				ConfirmationModel model = new ConfirmationModel();
				model.setCancelButtonText("cancel");
				model.setConfirmButtonText("OK\ncontinue");
				model.setQuestion("Would you like to continueOKcontinueOKcontinueOKcontinueOKcontinueOKcontinueOK\ncontinueOK\ncontinueOK\ncontinueOK\ncontinueOK\ncontinueOK\ncontinueOK\ncontinueOK\ncontinueOK\ncontinueOK\ncontinueOK\ncontinue?");
				model.addCallback(new ConfirmationCallback() {
					@Override
					public void confirmationTaken(final ConfirmationEvent evt) {
						System.out.println(evt.isConfirmed());
					}
				});

				DefaultConfirmationPanel p = new DefaultConfirmationPanel();

				p.setModel(model);
				model.ask();
				p.setConfirmButtonText("YES\nconfirm");
				f.getContentPane().add(p);
				f.setVisible(true);

			}
		});
	}

	// NO I18N here, the ok/cancel button text is define in
	// pccFrame.askConfirmation
	// because ok/cancel is give too poor info to the user
	// better use YES resume , no Destroy

	private JScrollPane scroll;
	private JXLabel textArea;
	private JButton confirmButton;
	private JButton cancelButton;

	private PaddedButton confirmPaddedButton;
	private PaddedButton cancelPaddedButton;

	public DefaultConfirmationPanel() {
		this(new ConfirmationModel());
	}

	public DefaultConfirmationPanel(final ConfirmationModel model) {
		super(model);
		initGUI();
	}

	private void initGUI() {
		setOpaque(false);
		setLayout(new MigLayout("fill", "[]70[]"));
		add(SmallScrollBar.createLayerSmallScrollBar(getScroll()),
				"gap top 50, gap left 20, gap right 20, span,grow,push,wrap");
		add(getConfirmPaddedButton(), "gap bottom 50,gap top 20,growx,pushx, h 70, sg 1");
		add(getCancelPaddedButton(), "gap bottom 50, gap top 20,growx,pushx, sg 1");

		setName("confirmationPanel");
		setBackground(SicpaColor.YELLOW);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	private PaddedButton getConfirmPaddedButton() {
		if (this.confirmPaddedButton == null) {
			this.confirmPaddedButton = new PaddedButton(getConfirmButton());
		}
		return this.confirmPaddedButton;
	}

	private PaddedButton getCancelPaddedButton() {
		if (this.cancelPaddedButton == null) {
			this.cancelPaddedButton = new PaddedButton(getCancelButton());
		}
		return this.cancelPaddedButton;
	}

	private JXLabel getTextArea() {
		if (this.textArea == null) {
			this.textArea = new JXLabel();
			this.textArea.setLineWrap(true);
			this.textArea.setFont(SicpaFont.getFont(30));
		}
		return this.textArea;
	}

	private JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getTextArea());
			// SmoothScrolling.enableFullScrolling(this.scroll);
			this.scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
			this.scroll.setOpaque(false);
			this.scroll.getViewport().setOpaque(false);
		}
		return this.scroll;
	}

	private JButton getConfirmButton() {
		if (this.confirmButton == null) {
			this.confirmButton = new JButton("OK");
			this.confirmButton.setName("confirmationButton");
			this.confirmButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					confirmButtonActionPerformed();
				}
			});
		}
		return this.confirmButton;
	}

	private void confirmButtonActionPerformed() {
		getModel().confirm();
	}

	private JButton getCancelButton() {
		if (this.cancelButton == null) {
			this.cancelButton = new JButton("CANCEL");
			this.cancelButton.setName("cancelButton");
			this.cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					cancelButtonActionPerformed();
				}
			});
		}
		return this.cancelButton;
	}

	private void cancelButtonActionPerformed() {
		getModel().cancel();
	}

	public void setConfirmButtonText(final String text) {
		getConfirmPaddedButton().setVisible(text != null && text.length() != 0);
		getConfirmButton().setText(text);
	}

	public void setCancelButtonText(final String text) {
		getCancelPaddedButton().setVisible(text != null && text.length() != 0);
		getCancelButton().setText(text);
	}

	public void setText(final String text) {
		getTextArea().setText(text);
	}

	private void enabledButtons(final boolean flag) {
		this.cancelButton.setEnabled(flag);
		this.confirmButton.setEnabled(flag);

		if (flag) {
			confirmButton.setEnabled(getModel().isConfirmButtonEnabled());
		}
	}

	@Override
	protected void modelCancelButtonTextChanged(final PropertyChangeEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getCancelButton().setText(evt.getNewValue() + "");
			}
		});
	}

	@Override
	protected void modelCancelButtonVisibilityChanged(final PropertyChangeEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getCancelPaddedButton().setVisible((Boolean) evt.getNewValue());
			}
		});
	}

	@Override
	protected void modelConfirmButtonTextChanged(final PropertyChangeEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getConfirmButton().setText(evt.getNewValue() + "");
			}
		});
	}

	@Override
	protected void modelQuestionTextChanged(final PropertyChangeEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				setText(evt.getNewValue() + "");
			}
		});
	}

	@Override
	protected void modelAskQuestionChanged(final PropertyChangeEvent evt) {

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				enabledButtons(true);
				setVisible(true);
				getModel().addCallback(new ConfirmationCallback() {
					@Override
					public void confirmationTaken(final ConfirmationEvent evt) {
						ThreadUtils.invokeLater(new Runnable() {
							@Override
							public void run() {
								setVisible(false);
							}
						});
					}
				});
			}
		});
	}

	@Override
	public void setModel(final ConfirmationModel model) {
		super.setModel(model);
		ThreadUtils.invokeLater(new Runnable() {

			@Override
			public void run() {
				setCancelButtonText(model.getCancelButtonText());
				setConfirmButtonText(model.getConfirmButtonText());
				setText(model.getQuestion());
				getCancelPaddedButton().setVisible(model.isCancelVisible());
			}
		});
	}

	@Override
	protected void modelAbort() {
		setVisible(false);
	}

	@Override
	protected void modelConfirmButtonenabilityChanged(PropertyChangeEvent evt) {
		getConfirmButton().setEnabled((Boolean) evt.getNewValue());

	}
}
