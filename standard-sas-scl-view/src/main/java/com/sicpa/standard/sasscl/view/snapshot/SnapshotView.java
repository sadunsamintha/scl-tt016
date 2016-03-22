package com.sicpa.standard.sasscl.view.snapshot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.utils.PaintUtils;

@SuppressWarnings("serial")
public class SnapshotView extends AbstractView<ISnapshotViewListener, SnapshotViewModel> {

	private JLabel labelIcon;
	private int iconW = 30;
	private int iconH = 20;
	private JXBusyLabel labelBusy;
	private float animProgess;

	public SnapshotView() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,hidemode 3"));
		add(getLabelIcon(), "center");
		add(getLabelBusy(), "center");
		setOpaque(false);
	}

	@Override
	public void modelChanged() {
		getLabelBusy().setBusy(model.isBusy());
		getLabelIcon().setVisible(!model.isBusy());
		getLabelBusy().setVisible(model.isBusy());
	}

	public JLabel getLabelIcon() {
		if (labelIcon == null) {
			labelIcon = new JLabel(new ImageIcon(createScreenShotImage(iconW, iconH)));
			labelIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					thisMousePressed();
				}
			});
		}
		return labelIcon;
	}

	private void thisMousePressed() {
		TaskExecutor.execute(() -> takeApplicationSnapshot());
		displayAnim();
	}

	private void takeApplicationSnapshot() {
		if (!model.isBusy()) {
			fireTakeApplicationSnapshot();
		}
	}

	private void fireTakeApplicationSnapshot() {
		synchronized (listeners) {
			for (ISnapshotViewListener l : listeners) {
				l.takeSnapshot();
			}
		}
	}

	public JXBusyLabel getLabelBusy() {
		if (labelBusy == null) {
			labelBusy = new JXBusyLabel();
			labelBusy.setVisible(false);
		}
		return labelBusy;
	}

	private void displayAnim() {
		Timeline timeline = new Timeline(this);
		timeline.addPropertyToInterpolate("animProgess", 0f, 1f);
		timeline.setDuration(50);
		timeline.setEase(new Spline(0.7f));
		timeline.playLoop(2, RepeatBehavior.REVERSE);
	}

	public void setAnimProgess(float animProgess) {
		this.animProgess = animProgess;
		repaint();
	}

	public float getAnimProgess() {
		return animProgess;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnQualityRendering(g2);
		g2.setComposite(AlphaComposite.SrcOver.derive(animProgess));
		g2.setColor(SicpaColor.YELLOW);
		g2.fillOval(0, 0, getWidth(), getHeight());
	}

	private static BufferedImage createScreenShotImage(int w, int h) {
		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(w, h);
		Graphics2D g = img.createGraphics();

		PaintUtils.turnOnQualityRendering(g);

		int tgap = h / 4, bgap = 1, lgap = h / 5, rgap = 1;

		g.setColor(Color.WHITE);
		// cadre
		int cx = lgap;
		int cy = tgap;
		int cw = w - lgap - rgap;
		int ch = h - tgap - bgap;
		g.drawRect(cx, cy, cw, ch);

		// flash
		int fx = cx + 1;
		int fy = tgap / 2;
		int fw = w / 8;
		int fh = h - (h - tgap - bgap) - tgap / 2 - bgap;
		g.drawRect(fx, fy, fw, fh);

		// objectif
		int oh = w / 4, ow = w / 4;
		int ox = lgap + cw / 2 - ow / 2;
		int oy = cy + ch / 2 - oh / 2;
		g.drawOval(ox, oy, ow, oh);

		// visor
		int vw = w / 8;
		int vh = h / 8;
		int vx = cx + cw - vw - 1;
		int vy = cy + 1;

		g.drawRect(vx, vy, vw, vh);

		g.dispose();
		return img;
	}

	public static void main(String[] args) {

		SicpaLookAndFeel.install();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(200, 200);

				SicpaLookAndFeel.flagAsHeaderOrFooter((JComponent) f.getContentPane());

				SnapshotView sst = new SnapshotView();

				f.getContentPane().add(sst);

				f.setVisible(true);

			}
		});
	}
}
