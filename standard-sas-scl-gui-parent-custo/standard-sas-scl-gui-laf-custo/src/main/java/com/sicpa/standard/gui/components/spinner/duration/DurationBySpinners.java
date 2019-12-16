package com.sicpa.standard.gui.components.spinner.duration;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerBottomButton;
import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerTopButton;
import com.sicpa.standard.gui.listener.ProgressiveMousePressedListener;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DurationBySpinners extends DurationSelector {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SicpaLookAndFeelCusto.install();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.setFontSize(10);
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout(""));

				DurationBySpinners p = new DurationBySpinners();
				p.addDurationUnit(DurationUnit.DAY, "-");
				p.addDurationUnit(DurationUnit.HOUR, "-");
				p.addDurationUnit(DurationUnit.MINUTE);
				p.addDurationUnit(DurationUnit.SECOND);
				// p.addDurationUnit(DurationUnit.MILLISECOND);

				p.getModel().setDuration(1000);

				p.getModel().addDurationChangedListener(new IDurationChangedListener() {

					@Override
					public void durationChanged(DurationChangedEvent evt) {
						System.out.println(evt.getDuration());
					}
				});
				f.getContentPane().add(p, "grow,push");
				f.setSize(300, 100);
				f.setVisible(true);
			}
		});
	}

	public DurationBySpinners() {
		initGUI();
	}

	@Override
	protected void modelDurationChanged(DurationChangedEvent evt) {

	}

	private void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0, gap 0 0 0 0 ,hidemode 2"));
	}

	@Override
	public void addDurationUnit(DurationUnit unit) {
		UnitPanel panel = new UnitPanel(unit, model);
		add(panel, "grow,push");
	}

	public void addDurationUnit(DurationUnit unit, String separator) {
		addDurationUnit(unit);
		if (separator != null && separator.length() > 0) {
			add(new JLabel(separator));
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Component c : getComponents()) {
			c.setEnabled(enabled);
		}
	}

	public void setEnabled(Component c, boolean enabled) {
		c.setEnabled(enabled);
		if (c instanceof Container) {
			for (Component child : ((Container) c).getComponents()) {
				setEnabled(child, enabled);
			}
		}
	}

	public static class UnitPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		protected DurationUnit unit;

		protected SpinnerBottomButton buttonBottom;
		protected SpinnerTopButton buttonTop;
		protected JLabel labelValue;

		protected DurationSelectorModel model;

		public UnitPanel(DurationUnit unit, DurationSelectorModel model) {
			this.unit = unit;
			this.model = model;
			this.model.addDurationChangedListener(new IDurationChangedListener() {

				@Override
				public void durationChanged(DurationChangedEvent evt) {
					modelDurationChanged(evt);
				}
			});
			initGUI();
		}

		protected void modelDurationChanged(DurationChangedEvent evt) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getLabelValue().setText(getStringValue());
					if (isEnabled()) {
						getButtonTop().setEnabled(model.getDuration(unit) < getMax());
						getButtonBottom().setEnabled(model.getDuration(unit) > getMin());
					}
				}
			});
		}

		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			getButtonTop().setEnabled(enabled);
			getButtonBottom().setEnabled(enabled);
			modelDurationChanged(null);
		}

		private void initGUI() {
			setLayout(new MigLayout("fill, inset 0 0 0 0, gap 0 0 0 0 ,hidemode 2"));
			add(getButtonTop(), "grow, pushy,wrap");
			add(getLabelValue(), "center,pushx,wrap");
			add(getButtonBottom(), "grow,pushy");
		}

		public SpinnerTopButton getButtonTop() {
			if (this.buttonTop == null) {
				this.buttonTop = new SpinnerTopButton();
				this.buttonTop.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						increaseDuration(1);
					}
				});
				this.buttonTop.addMouseListener(new ProgressiveMousePressedListener(new Runnable() {
					@Override
					public void run() {
						increaseDuration(1);
					}
				}));
			}
			return this.buttonTop;
		}

		public SpinnerBottomButton getButtonBottom() {
			if (this.buttonBottom == null) {
				this.buttonBottom = new SpinnerBottomButton();
				this.buttonBottom.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						increaseDuration(-1);
					}
				});
				this.buttonBottom.addMouseListener(new ProgressiveMousePressedListener(new Runnable() {
					@Override
					public void run() {
						increaseDuration(-1);
					}
				}));
			}
			return this.buttonBottom;
		}

		public JLabel getLabelValue() {
			if (this.labelValue == null) {
				this.labelValue = new JLabel("-");
				this.labelValue.addMouseMotionListener(new MouseDateSetter(this));
			}
			return this.labelValue;
		}

		public String getStringValue() {
			return String.valueOf(model.getDuration(unit));
		}

		public void increaseDuration(int amount) {

			long current = model.getDuration(unit);
			long next = current + amount;

			if (next < getMin() || next > getMax()) {
				return;
			}
			model.add(unit, amount);
		}

		protected int getMax() {
			return unit.getMax();
		}

		protected int getMin() {
			return 0;
		}
	}

	public static class MouseDateSetter extends MouseAdapter {

		int y = -1;
		final UnitPanel field;

		public MouseDateSetter(final UnitPanel field) {
			this.field = field;
		}

		@Override
		public void mouseDragged(final MouseEvent e) {

			if (this.y == -1) {
				this.y = e.getY();
				return;
			}

			int delta = e.getY() - this.y > 0 ? -1 : 1;
			field.increaseDuration(delta);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			this.y = -1;
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			this.y = e.getY();
		}
	}
}
