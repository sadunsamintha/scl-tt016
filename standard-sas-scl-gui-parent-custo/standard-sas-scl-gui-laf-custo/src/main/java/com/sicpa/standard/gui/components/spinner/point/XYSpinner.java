package com.sicpa.standard.gui.components.spinner.point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerBottomButton;
import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerButton;
import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerLeftButton;
import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerRightButton;
import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerTopButton;
import com.sicpa.standard.gui.listener.CoalescentChangeListener;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class XYSpinner extends JPanel {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SicpaLookAndFeelCusto.install();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(300, 300);

				SpinnerNumberModel modelX = new SpinnerNumberModel(0d, 0d, 9999d, 0.01d);
				SpinnerNumberModel modelY = new SpinnerNumberModel(0, 0, 9999, 1);
				final XYSpinner spinner = new XYSpinner(modelX, modelY);
				spinner.addChangeListener(new CoalescentChangeListener() {
					@Override
					public void doAction() {
						System.out.println("X=" + spinner.getXValue());
						System.out.println("Y=" + spinner.getYValue());
					}
				});

				f.getContentPane().add(spinner);
				f.setVisible(true);
			}
		});
	}

	private SpinnerNumberModel modelX;
	private SpinnerNumberModel modelY;

	protected List<ChangeListener> changeListeners;

	private String textFormat = "({0},{1})";

	private SpinnerLeftButton leftButton;
	private SpinnerRightButton rightButton;

	private SpinnerTopButton topButton;
	private SpinnerBottomButton bottomButton;

	private JLabel labelValue;

	public XYSpinner() {
		this(new SpinnerNumberModel(0, 0, 99999, 1), new SpinnerNumberModel(0, 0, 99999, 1));
	}

	public XYSpinner(final SpinnerNumberModel modelX, final SpinnerNumberModel modelY) {
		this.modelX = modelX;
		this.modelY = modelY;
		this.changeListeners = new ArrayList<ChangeListener>();
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0, gap 0 0 0 0"));
		add(getTopButton(), "grow,skip 1,wrap,pushx,sg 1");
		add(getLeftButton(), "grow,pushx,sg 1");
		add(getLabelValue(), "center,sg 1");
		add(getRightButton(), "grow,wrap,pushx,sg 1");
		add(getBottomButton(), "grow,skip 1,pushx,sg 1");
	}

	public void addChangeListener(final ChangeListener listener) {
		this.changeListeners.add(listener);
	}

	public void removeChangeListener(final ChangeListener listener) {
		this.changeListeners.remove(listener);
	}

	protected void fireValueChanged() {
		ChangeEvent evt = new ChangeEvent(this);
		for (ChangeListener listener : this.changeListeners) {
			listener.stateChanged(evt);
		}
	}

	public SpinnerLeftButton getLeftButton() {
		if (this.leftButton == null) {
			this.leftButton = new SpinnerLeftButton();
			this.leftButton.addMouseListener(new SpinnerButtonMouseListener(this.leftButton, this.modelX, false));
		}
		return this.leftButton;
	}

	public SpinnerRightButton getRightButton() {
		if (this.rightButton == null) {
			this.rightButton = new SpinnerRightButton();
			this.rightButton.addMouseListener(new SpinnerButtonMouseListener(this.rightButton, this.modelX, true));
		}
		return this.rightButton;
	}

	public JLabel getLabelValue() {
		if (this.labelValue == null) {
			this.labelValue = new JLabel();
			updateText();
		}
		return this.labelValue;
	}

	public SpinnerBottomButton getBottomButton() {
		if (this.bottomButton == null) {
			this.bottomButton = new SpinnerBottomButton();
			this.bottomButton.addMouseListener(new SpinnerButtonMouseListener(this.bottomButton, this.modelY, false));
		}
		return this.bottomButton;
	}

	public SpinnerTopButton getTopButton() {
		if (this.topButton == null) {
			this.topButton = new SpinnerTopButton();
			this.topButton.addMouseListener(new SpinnerButtonMouseListener(this.topButton, this.modelY, true));
		}
		return this.topButton;
	}

	private void updateText() {
		getLabelValue().setText(MessageFormat.format(this.textFormat, this.modelX.getValue(), this.modelY.getValue()));
	}

	// the what is happening when clicking on the arrows buttons
	private class SpinnerButtonMouseListener extends MouseAdapter {

		SpinnerButton button;

		int sleepTime = 1000;
		long clickedTime;

		// used to be sure if it s actually the same click operation going on,
		// and not a fast clicks on it
		// if instead we use a boolean while having fast click on a button =>
		// multiple threads running
		long i = 0;
		private SpinnerNumberModel model;
		private boolean increase;

		public SpinnerButtonMouseListener(final SpinnerButton button, final SpinnerNumberModel model,
				final boolean increase) {
			this.button = button;
			this.model = model;
			this.increase = increase;
		}

		@Override
		public void mousePressed(final MouseEvent mouseEvent) {
			if (!this.button.isEnabled()) {
				return;
			}

			this.clickedTime = System.currentTimeMillis();

			Runnable changeValue = new Runnable() {
				@Override
				public void run() {
					doStuff();
				}
			};
			new Thread(changeValue).start();
		}

		private void doStuff() {

			final long num = this.i;
			while (num == SpinnerButtonMouseListener.this.i) {
				long delta = System.currentTimeMillis() - SpinnerButtonMouseListener.this.clickedTime;
				if (delta < 500) {
					SpinnerButtonMouseListener.this.sleepTime = 1000;
				} else if (delta < 750) {
					SpinnerButtonMouseListener.this.sleepTime = 60;
				} else if (delta < 1000) {
					SpinnerButtonMouseListener.this.sleepTime = 50;
				} else if (delta < 1500) {
					SpinnerButtonMouseListener.this.sleepTime = 40;
				} else if (delta < 2000) {
					SpinnerButtonMouseListener.this.sleepTime = 50;
				} else if (delta < 3000) {
					SpinnerButtonMouseListener.this.sleepTime = 30;
				} else if (delta < 4000) {
					SpinnerButtonMouseListener.this.sleepTime = 10;
				} else if (delta < 5000) {
					SpinnerButtonMouseListener.this.sleepTime = 5;
				} else if (delta < 7000) {
					SpinnerButtonMouseListener.this.sleepTime = 1;
				}

				Object value;
				if (this.increase) {
					value = this.model.getNextValue();
				} else {
					value = this.model.getPreviousValue();
				}

				if (value != null) {
					this.model.setValue(value);
					fireValueChanged();
				} else {
					break;
				}

				updateText();

				ThreadUtils.sleepQuietly(SpinnerButtonMouseListener.this.sleepTime);
			}

		}

		@Override
		public void mouseReleased(final MouseEvent mouseEvent) {
			this.i++;
		}
	};

	public SpinnerNumberModel getModelX() {
		return this.modelX;
	}

	public SpinnerNumberModel getModelY() {
		return this.modelY;
	}

	public void setTextFormat(final String textFormat) {
		this.textFormat = textFormat;
	}

	public Number getXValue() {
		return getModelX().getNumber();
	}

	public Number getYValue() {
		return getModelY().getNumber();
	}
}
