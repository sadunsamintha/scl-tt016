package com.sicpa.standard.gui.components.label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class UptimeLabel extends JLabel {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();

				JFrame f = new JFrame();
				f.getContentPane().add(new UptimeLabel());
				f.pack();
				f.setVisible(true);
			}
		});
	}

	private long startTime;
	private DecimalFormat df;
	private Timer timer = new Timer(1000, new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			timerActionPerformed();
		}
	});

	private void timerActionPerformed() {
		long count = (System.currentTimeMillis() - this.startTime) / 1000;
		int hour = (int) (count / 3600);
		int minute = (int) ((count - (hour * 3600)) / 60);
		int seconde = (int) (count - (hour * 3600 + minute * 60));

		setText(hour, minute, seconde);
	}

	private void setText(final int hour, final int minute, final int seconde) {
		setText(this.df.format(hour) + ":" + this.df.format(minute) + ":" + this.df.format(seconde));
	}

	public UptimeLabel() {
		this.df = new DecimalFormat();
		this.df.setMinimumIntegerDigits(2);
		reset();
	}

	public void start() {
		if (!this.timer.isRunning()) {
			reset();
			this.timer.start();
		}
	}

	public void stop() {
		this.timer.stop();
	}

	public void reset() {
		this.startTime = System.currentTimeMillis();
		ThreadUtils.invokeLater(new Runnable() {
			public void run() {
				setText(0, 0, 0);
			}
		});
	}
}
