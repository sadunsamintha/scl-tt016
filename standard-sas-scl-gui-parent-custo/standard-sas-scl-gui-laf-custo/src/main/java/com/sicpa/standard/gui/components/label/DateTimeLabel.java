package com.sicpa.standard.gui.components.label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class DateTimeLabel extends JLabel {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				SicpaLookAndFeelCusto.install();

				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().add(new DateTimeLabel());
				f.pack();
				f.setVisible(true);
			}
		});
	}

	private DateFormat dateFormat;

	private Timer timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			tick();
		}
	});

	public DateTimeLabel(final String dateFormat) {
		this.dateFormat = new SimpleDateFormat(dateFormat);
		tick();
		startTicking();
	}

	public DateTimeLabel() {
		this("yyyy/MM/dd HH:mm:ss");
	}

	public void setDateFormat(final DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		repaint();
	}

	private void tick() {
		setText(this.dateFormat.format(new Date(System.currentTimeMillis())));
	}

	public void startTicking() {
		if (!this.timer.isRunning()) {
			this.timer.start();
		}
	}

	public void stopTicking() {
		this.timer.stop();
	}
}
