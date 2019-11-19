package com.sicpa.standard.gui.components.spinner.date;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerBottomButton;
import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerTopButton;
import com.sicpa.standard.gui.listener.ProgressiveMousePressedListener;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class AbstractTimeSpinner extends JPanel {

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
				
				AbstractTimeSpinner p = new AbstractTimeSpinner();
				p.addUnit(SpinnerDateUnit.YEAR, "/");
				p.addUnit(SpinnerDateUnit.MONTH, "/");
				p.addUnit(SpinnerDateUnit.DAY, "      ");
				p.addUnit(SpinnerDateUnit.HOUR, ":");
				p.addUnit(SpinnerDateUnit.MINUTE, ":");
				p.addUnit(SpinnerDateUnit.SECOND, ",");
				p.addUnit(SpinnerDateUnit.MILLISECOND, "");

				p.setDate(new Date());

				p.addPropertyChangeListener("date", new PropertyChangeListener() {
					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						System.out.println(evt.getNewValue());

					}
				});
				f.getContentPane().add(p, "grow,push");
				f.setSize(300, 100);
				f.setVisible(true);
			}
		});
	}

	protected final List<UnitPanel> unitPanels = new ArrayList<UnitPanel>();

	protected final Calendar calendar;

	public AbstractTimeSpinner() {
		this.calendar = Calendar.getInstance();
		initGUI();
		setDate(this.calendar.getTime());
	}

	private void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0, gap 0 0 0 0 ,hidemode 2"));
	}

	public void addUnit(SpinnerDateUnit unit, String separator) {

		UnitPanel panel;
		switch (unit) {
		case MONTH:
			panel = new UnitPanel(unit, calendar) {
				private static final long serialVersionUID = 1L;

				@Override
				public String getStringValue() {
					return new SimpleDateFormat("MMM").format(calendar.getTime());
				}
			};
			break;
		case DAY:
			panel = new UnitPanel(unit, calendar) {
				private static final long serialVersionUID = 1L;

				@Override
				protected int getMax() {
					return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				}
			};
			break;
		default:
			panel = new UnitPanel(unit, calendar);
			break;
		}
		addUnit(panel, separator);
	}

	public void addUnit(UnitPanel panel, String separator) {
		add(panel, "grow,push");
		if (separator != null && separator.length() > 0) {
			add(new JLabel(separator));
		}
		unitPanels.add(panel);
	}

	public Date getDate() {
		return this.calendar.getTime();
	}

	public void setDate(final Date d) {
		this.calendar.setTime(d);

		for (UnitPanel up : unitPanels) {
			up.setDate(d);
		}
		firePropertyChange("date", null, d);
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

			field.increaseDate(delta);
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

	public static enum SpinnerDateUnit {
		YEAR(Calendar.YEAR, 9999), MONTH(Calendar.MONTH, 12, 0), DAY(Calendar.DAY_OF_MONTH, 31, 1), HOUR(
				Calendar.HOUR_OF_DAY, 23), MINUTE(Calendar.MINUTE, 59), SECOND(Calendar.SECOND, 59), MILLISECOND(
				Calendar.MILLISECOND, 999);

		int calendarUnit;
		int max;
		int min;

		SpinnerDateUnit(int calendarUnit, int max) {
			this.calendarUnit = calendarUnit;
			this.max = max;
		}

		SpinnerDateUnit(int calendarUnit, int max, int min) {
			this(calendarUnit, max);
			this.min = min;
		}

	}

	public static class UnitPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		protected SpinnerDateUnit unit;

		protected SpinnerBottomButton buttonBottom;
		protected SpinnerTopButton buttonTop;
		protected JLabel labelValue;
		protected Calendar calendar;

		public UnitPanel(SpinnerDateUnit unit, Calendar calendar) {
			this.unit = unit;
			this.calendar = calendar;
			initGUI();
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
						increaseDate(1);
					}
				});
				this.buttonTop.addMouseListener(new ProgressiveMousePressedListener(new Runnable() {
					@Override
					public void run() {
						increaseDate(1);
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
						increaseDate(-1);
					}
				});
				this.buttonBottom.addMouseListener(new ProgressiveMousePressedListener(new Runnable() {
					@Override
					public void run() {
						increaseDate(-1);
					}
				}));
			}
			return this.buttonBottom;
		}

		public JLabel getLabelValue() {
			if (this.labelValue == null) {
				this.labelValue = new JLabel();
				this.labelValue.addMouseMotionListener(new MouseDateSetter(this));
			}
			return this.labelValue;
		}

		public void setDate(Date date) {
			calendar.setTime(date);
			int h = this.calendar.get(unit.calendarUnit);
			getLabelValue().setText(getStringValue());
			getButtonTop().setEnabled(h < getMax());
			getButtonBottom().setEnabled(h > getMin());
		}

		public String getStringValue() {
			int h = this.calendar.get(unit.calendarUnit);
			return (h < 10 ? "0" + h : h + "");
		}

		public void increaseDate(int amount) {

			int current = calendar.get(unit.calendarUnit);
			int next = current + amount;

			if (next < getMin() || next > getMax()) {
				return;
			}

			calendar.add(unit.calendarUnit, amount);

			Component parent = getParent();
			if (parent instanceof AbstractTimeSpinner) {
				((AbstractTimeSpinner) parent).setDate(calendar.getTime());
			} else {
				setDate(calendar.getTime());
			}
		}

		protected int getMax() {
			return unit.max;
		}

		protected int getMin() {
			return unit.min;
		}
	}
}
