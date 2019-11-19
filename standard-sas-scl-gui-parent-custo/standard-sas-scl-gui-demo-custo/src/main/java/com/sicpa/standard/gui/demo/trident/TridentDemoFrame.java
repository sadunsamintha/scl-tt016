package com.sicpa.standard.gui.demo.trident;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelineScenario;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.UIThreadTimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

public class TridentDemoFrame extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				TridentDemoFrame inst = new TridentDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private DrawingPanel drawingPanel;
	private JButton buttonStart;

	public TridentDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getDrawingPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonStart(), BorderLayout.SOUTH);
		setSize(700, 200);
	}

	public DrawingPanel getDrawingPanel() {
		if (this.drawingPanel == null) {
			this.drawingPanel = new DrawingPanel();
		}
		return this.drawingPanel;
	}

	public JButton getButtonStart() {
		if (this.buttonStart == null) {
			this.buttonStart = new JButton("start");

			this.buttonStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonStartActionPerformed();
				}
			});

		}
		return this.buttonStart;
	}

	private void buttonStartActionPerformed() {
		TridentDemoFrame.this.drawingPanel.startAnimation();
	}

	public static class DrawingPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private int xPos = 0;
		private Color color = Color.red;
		private TimelineScenario scenario;

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			g.translate(this.xPos, 0);
			g.drawImage(getImage(this.color), 0, 0, null);
		}

		private static BufferedImage getImage(final Color color) {
			int w = 50;
			int h = 50;
			int l = Math.min(w, h);

			BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(w, h);

			Graphics2D g2 = (Graphics2D) img.createGraphics();

			PaintUtils.turnOnAntialias(g2);

			g2.setColor(color);
			g2.fillOval(1, 1, l - 2, l - 2);

			g2.setComposite(AlphaComposite.DstIn.derive(0.9f));

			Point center = new Point(l / 2, l / 2);
			float[] dist = { 0.0f, 0.7f, 1.0f };
			Color[] colors = { new Color(0, 0, 0, 255), new Color(0, 0, 0, 255), new Color(0, 0, 0, 0) };
			int radius = w / 2;

			RadialGradientPaint rgp = new RadialGradientPaint(center, radius, center, dist, colors,
					CycleMethod.NO_CYCLE);
			g2.setPaint(rgp);

			g2.fillOval(0, 0, l, l);
			g2.dispose();
			return img;
		}

		public int getXPos() {
			return this.xPos;
		}

		public void setXPos(final int pos) {
			this.xPos = pos;
			repaint();
		}

		public Color getColor() {
			return this.color;
		}

		public void setColor(final Color color) {
			this.color = color;
			repaint();
		}

		public void startAnimation() {

			if (this.scenario != null) {
				this.scenario.cancel();
			}

			// -----------X pos animation
			Timeline animPosition = new Timeline(this);
			animPosition.setDuration(5000);
			animPosition.addPropertyToInterpolate("xPos", 0, 600);
			animPosition.setEase(new Spline(0.8f));
			animPosition.addCallback(new UIThreadTimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
						final float durationFraction, final float timelinePosition) {
					if (newState == TimelineState.READY) {
						setColor(Color.red);
					}
				}
			});

			// ----------color animation
			Timeline animColor = new Timeline(this);
			animColor.setDuration(5000);
			animColor.addPropertyToInterpolate("color", Color.RED, Color.GREEN);

			// define scenario animation
			// do move then color
			this.scenario = new TimelineScenario.Sequence();
			this.scenario.addScenarioActor(animPosition);
			this.scenario.addScenarioActor(animColor);

			this.scenario.play();
		}
	}
}
