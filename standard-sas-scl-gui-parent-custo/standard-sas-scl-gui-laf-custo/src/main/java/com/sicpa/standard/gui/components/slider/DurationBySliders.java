package com.sicpa.standard.gui.components.slider;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.spinner.duration.DurationChangedEvent;
import com.sicpa.standard.gui.components.spinner.duration.DurationSelector;
import com.sicpa.standard.gui.components.spinner.duration.DurationSelectorModel;
import com.sicpa.standard.gui.components.spinner.duration.DurationUnit;
import com.sicpa.standard.gui.components.spinner.duration.IDurationChangedListener;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DurationBySliders extends DurationSelector {

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

				DurationBySliders p = new DurationBySliders();
				p.addDurationUnit(new DurationUnit(DurationUnit.DAY.getMultiplier(), 10), "day");
				p.addDurationUnit(DurationUnit.HOUR, "hour");
				p.addDurationUnit(DurationUnit.MINUTE, "min");
				p.addDurationUnit(DurationUnit.SECOND, "sec");
				p.addDurationUnit(DurationUnit.MILLISECOND, "ms");

				p.getModel().setDuration(60 * 1000 + 5000 + 55);

				p.getModel().addDurationChangedListener(new IDurationChangedListener() {

					@Override
					public void durationChanged(DurationChangedEvent evt) {
						System.out.println(evt.getDuration());
					}
				});
				f.getContentPane().add(p, "grow,push");
				f.setSize(300, 150);
				f.setVisible(true);
			}
		});
	}

	public DurationBySliders() {
		initGUI();
	}

	@Override
	protected void modelDurationChanged(DurationChangedEvent evt) {

	}

	@Override
	public void addDurationUnit(DurationUnit unit) {
		add(new MySliderDuration(unit, model), "wrap,grow,push");
	}

	public void addDurationUnit(DurationUnit unit, String text) {
		add(new JLabel(text));
		addDurationUnit(unit);
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0, gap 0 0 0 0"));
	}

	public static class MySliderDuration extends JPanel {

		private static final long serialVersionUID = 1L;
		protected DurationUnit unit;
		protected SicpaSlider slider;
		protected DurationSelectorModel model;

		private MySliderDuration(DurationUnit unit, DurationSelectorModel model) {
			this.unit = unit;
			this.model = model;
			initGUI();
			model.addDurationChangedListener(new IDurationChangedListener() {
				@Override
				public void durationChanged(DurationChangedEvent evt) {
					modelDurationChanged(evt);
				}
			});
		}

		boolean modelisChanging = false;

		protected void modelDurationChanged(final DurationChangedEvent evt) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					modelisChanging = true;
					getSlider().setValue((int) model.getDuration(unit));
					modelisChanging = false;
				}
			});
		}

		private void initGUI() {
			setLayout(new MigLayout("fill, inset 0 0 0 0, gap 0 0 0 0"));
			add(getSlider(), "grow,push");
		}

		public SicpaSlider getSlider() {
			if (slider == null) {
				slider = new SicpaSlider(0, unit.getMax(), 0);
				slider.addPropertyChangeListener("value", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						sliderValueChanged(evt);
					}
				});
			}
			return slider;
		}

		protected void sliderValueChanged(PropertyChangeEvent evt) {
			if (modelisChanging) {
				return;
			}
			int oldValue = (Integer) evt.getOldValue();
			int newValue = (Integer) evt.getNewValue();
			int delta = newValue - oldValue;

			model.add(unit, delta);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Component c : getComponents()) {
			setEnabled(c, enabled);
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

}
