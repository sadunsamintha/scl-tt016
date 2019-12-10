package com.sicpa.standard.gui.plaf;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

import com.sicpa.standard.gui.utils.PaintUtils;

public class PulseSourceCalibrationFrame extends JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				PulseSourceCalibrationFrame f = new PulseSourceCalibrationFrame();
				f.setAlwaysOnTop(true);
				f.setVisible(true);

			}
		});
	}

	public PulseSourceCalibrationFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new MigLayout("fill"));

		getContentPane().add(getPanelAnim(), "grow,push,wrap");
		getContentPane().add(getSpinner(), "");
		setSize(500, 500);
		Timeline t = new Timeline(this);
		t.setDuration(1000);
		t.addPropertyToInterpolate("animProgress", 0f, 1f);
		t.playLoop(RepeatBehavior.REVERSE);

	}

	JSpinner spinner;
	float animProgress;
	JPanel panelAnim;

	public JSpinner getSpinner() {
		if (this.spinner == null) {
			this.spinner = new JSpinner(new SpinnerNumberModel(SicpaLookAndFeelCusto.SicpaPulseSource.DELAY, 1, 9999, 1));
			this.spinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					SicpaLookAndFeelCusto.SicpaPulseSource.DELAY = (Integer) PulseSourceCalibrationFrame.this.spinner
							.getValue();
				}
			});
		}

		return this.spinner;
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		repaint();
	}

	public JPanel getPanelAnim() {
		if (this.panelAnim == null) {
			this.panelAnim = new JPanel() {
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);
					Graphics2D g2 = (Graphics2D) g.create();
					PaintUtils.turnOnQualityRendering(g2);
					g2.setColor(SicpaColor.RED);
					g2.fillOval(0, 0, (int) (getAnimProgress() * getWidth()), (int) (getAnimProgress() * getHeight()));
					g2.dispose();
				}
			};
		}
		return this.panelAnim;
	}

}
