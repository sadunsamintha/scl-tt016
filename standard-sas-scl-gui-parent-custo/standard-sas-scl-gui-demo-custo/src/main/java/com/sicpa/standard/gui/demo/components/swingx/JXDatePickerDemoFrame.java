package com.sicpa.standard.gui.demo.components.swingx;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.CalendarUtils;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class JXDatePickerDemoFrame extends javax.swing.JFrame {

	private JXDatePicker datePicker;

	public static void main(final String[] args) {

		Locale.setDefault(new Locale("en"));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXDatePickerDemoFrame inst = new JXDatePickerDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXDatePickerDemoFrame() {
		super();
		setLayout(new FlowLayout());
		getContentPane().add(getDatePicker());
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			pack();
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JXDatePicker getDatePicker() {
		if (this.datePicker == null) {
			this.datePicker = new JXDatePicker();

			Calendar calendar = this.datePicker.getMonthView().getCalendar();
			// Today
			calendar.setTime(new Date());
			this.datePicker.getMonthView().setLowerBound(calendar.getTime());
			// end of next week
			CalendarUtils.endOfWeek(calendar);
			calendar.add(Calendar.WEEK_OF_YEAR, 6);
			this.datePicker.getMonthView().setUpperBound(calendar.getTime());

			this.datePicker.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					datePickerActionPerformed(e);
				}
			});
		}
		return this.datePicker;
	}

	private void datePickerActionPerformed(final ActionEvent e) {
		if (JXDatePicker.COMMIT_KEY.equals(e.getActionCommand())) {
			System.out.println("Commit option:" + this.datePicker.getDate());
		}

	}
}
