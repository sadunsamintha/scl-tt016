package com.sicpa.standard.sasscl.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.ease.Spline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.utils.PaintUtils;

public class ScreenShotTaker extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger logger= LoggerFactory.getLogger(ScreenShotTaker.class);

	protected JLabel labelIcon;
	protected int iconW;
	protected int iconH;
	protected Runnable callback;
	protected JFrame frame;

	public ScreenShotTaker(int iconW, int iconH, Runnable callback, JFrame frame) {
		this.iconH = iconH;
		this.iconW = iconW;
		initGUI();
		setOpaque(false);
		this.callback = callback;
		this.frame = frame;
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getLabelIcon(), "center");

	}

	public JLabel getLabelIcon() {
		if (labelIcon == null) {
			labelIcon = new JLabel(new ImageIcon(createScreenShotImage(iconW, iconH)));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							takeScreenShot();
						}
					}).start();
					displayAnim();
				}
			});
		}
		return labelIcon;
	}

	protected void takeScreenShot() {
		try {
			Robot robot = new Robot();
			BufferedImage screenshot = robot.createScreenCapture(frame.getBounds());

			File folder = new File(getFolder());
			folder.mkdirs();

			saveimage(screenshot, folder.getPath() + "/" + createFileName());
			if (callback != null) {
				callback.run();
			}

		} catch (Exception e) {
			logger.error("fail to take screenshot", e);
		}
	}

	protected void saveimage(BufferedImage img, String file) throws IOException {
		ImageIO.write(img, "PNG", new File(file));
	}

	protected String createFileName() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".png";
	}

	protected String getFolder() {
		return "screenshot";
	}

	protected void displayAnim() {
		Timeline timeline = new Timeline(this);
		timeline.addPropertyToInterpolate("animProgess", 0f, 1f);
		timeline.setDuration(150);
		timeline.setEase(new Spline(0.7f));
		timeline.playLoop(2, RepeatBehavior.REVERSE);
	}

	protected float animProgess;

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

	protected static BufferedImage createScreenShotImage(int w, int h) {
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

				ScreenShotTaker sst = new ScreenShotTaker(60, 40, null, f);

				f.getContentPane().add(sst);

				f.setVisible(true);

			}
		});
	}
}
