package com.sicpa.standard.gui.screen.machine.component.progress;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXBusyLabel;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultProgressView extends AbstractProgressView {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				DefaultProgressView view = new DefaultProgressView();
				view.modelProgressChanged(true);
				f.getContentPane().add(view);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.pack();
				f.setVisible(true);

			}
		});
	}

	// private Timeline timeline;
	// private double angle;
	private JXBusyLabel label;

	public DefaultProgressView() {

		iniGUI();
	}

	private void iniGUI() {
		setLayout(new BorderLayout());
		add(getLabel());
		setOpaque(false);
	}

	public JXBusyLabel getLabel() {
		if (this.label == null) {
			this.label = new JXBusyLabel(new Dimension(100, 100));
			this.label.getBusyPainter().setHighlightColor(SicpaColor.BLUE_DARK);
			this.label.getBusyPainter().setBaseColor(new Color(0, 0, 0, 0));
			this.label.setDelay(100);
			this.label.getBusyPainter().setPoints(10);
			this.label.getBusyPainter().setTrailLength(8);
		}
		return this.label;
	}

	@Override
	protected void modelProgressChanged(final boolean isRunning) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabel().setBusy(isRunning);
				setVisible(isRunning);
			}
		});

		// if (!isRunning && this.timeline != null) {
		// this.timeline.abort();
		// setVisible(false);
		// } else if (isRunning) {
		// if (this.timeline == null) {
		// this.timeline = new Timeline(this);
		// this.timeline.setDuration(1000);
		// this.timeline.addPropertyToInterpolate("angle", 0d, -2 * Math.PI);
		// }
		// this.timeline.playLoop(RepeatBehavior.LOOP);
		// }
	}

	// @Override
	// protected void paintComponent(final Graphics g) {
	//
	// Graphics2D g2 = (Graphics2D) g.create();
	// PaintUtils.turnOnQualityRendering(g2);
	//
	// g2.rotate(this.angle, getWidth() / 2, getHeight() / 2);
	//
	// g2.setColor(Color.RED);
	//
	// g2.drawLine(20, 20, getWidth() - 20, 20);
	//
	// g2.drawLine(20, 20, 20, getHeight() - 20);
	//
	// g2.dispose();
	//
	// }

	// public void setAngle(final double angle) {
	// this.angle = angle;
	// repaint();
	// }

	@Override
	public void setModel(final ProgressModel model) {
		super.setModel(model);
		modelProgressChanged(model.isRunning());
	}
}
