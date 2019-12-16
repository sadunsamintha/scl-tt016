package com.sicpa.standard.gui.demo.trident;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class BlinkingLabelDemo extends JFrame {
	private static final long serialVersionUID = 1L;
	private JLabel labelBlinkingWithTrident;
	private JLabel labelBlinkingWithSwingTimer;

	public BlinkingLabelDemo() {
		initGUI();
		startAnimTrident();
		startAnimWithSwingTimer();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("fill"));
		getContentPane().add(getLabelBlinkingWithTrident(), "north");
		getContentPane().add(getLabelBlinkingWithSwingTimer(), "south");
	}

	public JLabel getLabelBlinkingWithTrident() {
		if (this.labelBlinkingWithTrident == null) {
			this.labelBlinkingWithTrident = new JLabel("blinking label powered by TRIDENT");
		}
		return this.labelBlinkingWithTrident;
	}

	public void startAnimTrident() {
		Timeline timeline = new Timeline(this.labelBlinkingWithTrident);
		timeline.addPropertyToInterpolate("foreground", new Color(255, 0, 0, 0), Color.RED);
		timeline.setDuration(500);
		timeline.setEase(new Spline(0.8f));
		timeline.playLoop(RepeatBehavior.REVERSE);
	}

	public JLabel getLabelBlinkingWithSwingTimer() {
		if (this.labelBlinkingWithSwingTimer == null) {
			this.labelBlinkingWithSwingTimer = new JLabel("blinking label with basic swing timer");
		}
		return this.labelBlinkingWithSwingTimer;
	}

	public void startAnimWithSwingTimer() {
		Timer timer = new Timer(50, new ActionListener() {
			long startTime = System.currentTimeMillis();

			@Override
			public void actionPerformed(final ActionEvent e) {
				long delta = System.currentTimeMillis() - this.startTime;
				delta = delta % 1000;
				float freq = (float) delta / 1000;
				if (freq > 1) {
					freq = 1;
				}
				int alpha;
				if (freq <= 0.5) {
					alpha = (int) (freq * 2 * 255);
				} else {
					alpha = (int) ((1 - (freq - 0.5) * 2) * 255);
				}
				getLabelBlinkingWithSwingTimer().setForeground(new Color(255, 0, 0, alpha));
			}
		});
		timer.setCoalesce(true);
		timer.start();
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				BlinkingLabelDemo f = new BlinkingLabelDemo();
				f.setSize(400, 200);
				f.setVisible(true);
			}
		});
	}
}
