package com.sicpa.standard.gui.demo.fx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

import com.jhlabs.image.CausticsFilter;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

public class AndroidLikePasswordPanel extends javax.swing.JPanel {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame frame = new JFrame();
				frame.getContentPane().add(new AndroidLikePasswordPanel(3, 3));
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	private static final int ANIM_DURATION = 300;

	private static final int NODE_SIZE = 40;
	private static int LINE_THICKNESS = 20;
	private int rowNumber;
	private int columnNumber;

	private Node[] tabNode;
	private ArrayList<Integer> listSelect;
	private BufferedImage imageNode;
	private BufferedImage imageSelected;

	private int oldIndexSelected = -1;
	private Point mousePos;

	private BufferedImage imageLine;
	private BufferedImage backgroundImage;

	public AndroidLikePasswordPanel() {
		this(3, 3);
	}

	public AndroidLikePasswordPanel(final int row, final int col) {
		super();
		this.rowNumber = row;
		this.columnNumber = col;
		this.listSelect = new ArrayList<Integer>();
		initGUI();

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(final MouseEvent e) {
				thisMouseDragged(e);
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				thisMousePressed();
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				thisMouseReleased();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				thisComponentResized();
			}
		});
	}

	private void thisMouseReleased() {
		AndroidLikePasswordPanel.this.mousePos = null;
		repaint();
	}

	private void thisComponentResized() {
		this.backgroundImage = null;
		repaint();
	}

	private void thisMousePressed() {
		reset();
	}

	private void thisMouseDragged(final MouseEvent e) {
		this.mousePos = e.getPoint();
		for (Node n : getTabNode()) {
			if (n.contains(SwingUtilities.convertPoint(this, e.getPoint(), n))) {
				if (!n.isSelected() && (this.oldIndexSelected == -1 || isPossible(this.oldIndexSelected, n.index))) {
					n.setSelected(true);
					this.listSelect.add(n.index);
					repaint();
					this.oldIndexSelected = n.index;
					return;
				}
			}
		}
		repaint();
	}

	private void initGUI() {
		setBackground(Color.BLACK);

		setLayout(new MigLayout("fill,wrap " + this.columnNumber));

		int i = 0;
		for (int row = 0; row < this.rowNumber; row++) {
			for (int col = 0; col < this.columnNumber; col++) {
				add(getTabNode()[i], "center,h " + NODE_SIZE + "!, w " + NODE_SIZE + "!");
				i++;
			}
		}

		setPreferredSize(new Dimension(400, 400));
	}

	private boolean isPossible(final int start, final int end) {
		int rowa = start / this.columnNumber;
		int cola = start % this.columnNumber;
		int rowb = end / this.columnNumber;
		int colb = end % this.columnNumber;

		// is not on same row or if adjacent node
		boolean row = rowa != rowb || Math.abs(colb - cola) == 1;

		// is not on same col or if adjacent node
		boolean col = cola != colb || Math.abs(rowb - rowa) == 1;

		// is not diagonal of coef director of 1 or if adjacent node
		// ##O ###
		// ### <= wrong #O# <= right
		// O## O##
		boolean diagonal = Math.abs(rowb - rowa) != Math.abs(colb - cola) || Math.abs(rowb - rowa) == 1;

		return row && col && diagonal;
	}

	public BufferedImage getImageNode() {
		if (this.imageNode == null) {
			this.imageNode = GraphicsUtilities.createCompatibleTranslucentImage(NODE_SIZE - 4, NODE_SIZE - 4);
			Graphics2D g = (Graphics2D) this.imageNode.createGraphics();
			PaintUtils.turnOnAntialias(g);

			g.setColor(SicpaColor.BLUE_ULTRA_LIGHT);
			g.fillOval(0, 0, this.imageNode.getWidth(), this.imageNode.getHeight());

			g.dispose();
		}
		return this.imageNode;
	}

	public BufferedImage getImageSelected() {
		if (this.imageSelected == null) {
			this.imageSelected = GraphicsUtilities.createCompatibleTranslucentImage(NODE_SIZE, NODE_SIZE);

			int w = NODE_SIZE;

			BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());

			Graphics2D g2 = (Graphics2D) img.createGraphics();
			PaintUtils.turnOnAntialias(g2);

			g2.setColor(Color.green);
			g2.fillOval(1, 1, w - 2, w - 2);

			g2.setComposite(AlphaComposite.DstIn.derive(1f));

			Point center = new Point(w / 2, w / 2);
			float[] dist = { 0.0f, 0.7f, 1.0f };
			Color[] colors = { new Color(0, 0, 0, 255), new Color(0, 0, 0, 255), new Color(0, 0, 0, 0) };
			int radius = w / 2;

			RadialGradientPaint rgp = new RadialGradientPaint(center, radius, center, dist, colors,
					CycleMethod.NO_CYCLE);
			g2.setPaint(rgp);

			g2.fillOval(0, 0, w, w);
			g2.dispose();

			g2 = (Graphics2D) this.imageSelected.createGraphics();
			g2.drawImage(img, 0, 0, null);
			g2.dispose();
		}
		return this.imageSelected;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.drawImage(getBackgroundImage(), 0, 0, null);

		PaintUtils.turnOnAntialias(g2);

		int old = -1;
		for (Integer i : this.listSelect) {
			if (old != -1) {
				paintLineBetweenNodes(old, i, g2);
			}
			old = i;
		}

		if (this.oldIndexSelected != -1 && this.mousePos != null) {
			g.setColor(Color.red);
			int x1 = getTabNode()[this.oldIndexSelected].getX();
			int y1 = getTabNode()[this.oldIndexSelected].getY();
			int x2 = (int) this.mousePos.getX();
			int y2 = (int) this.mousePos.getY();

			paintLine(x1, y1, x2, y2, g2);
		}

		// paint ghost
		for (Node n : getTabNode()) {
			if (n.animProgress > 0) {
				g2.setComposite(AlphaComposite.SrcOver.derive((1f - n.animProgress) / 2));

				int w = (int) ((1f + n.animProgress * 3) * NODE_SIZE);
				int h = (int) ((1f + n.animProgress * 3) * NODE_SIZE);

				int x = n.getX() - (w - NODE_SIZE) / 2;
				int y = n.getY() - (h - NODE_SIZE) / 2;

				g2.drawImage(getImageSelected(), x, y, w, h, null);
			}
		}
		g2.dispose();
	}

	public BufferedImage getBackgroundImage() {
		if (this.backgroundImage == null) {
			this.backgroundImage = GraphicsUtilities.createCompatibleImage(getWidth(), getHeight());

			new CausticsFilter().filter(this.backgroundImage, this.backgroundImage);
			new ColorTintFilter(SicpaColor.BLUE_DARK, 0.9f).filter(this.backgroundImage, this.backgroundImage);
		}
		return this.backgroundImage;
	}

	private void paintLine(final int x1, final int y1, final int x2, final int y2, final Graphics2D g) {
		// always paint from top to bottom
		if (y1 > y2) {
			paintLine(x2, y2, x1, y1, g);
			return;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		// translate to the first point
		g2.translate(x1 + NODE_SIZE / 2, y1 + NODE_SIZE / 2 - LINE_THICKNESS / 2);

		int w = x2 - x1;
		int h = y2 - y1;

		// compute the line width
		double diagW = Math.sqrt(Math.pow(w, 2d) + Math.pow(h, 2d));

		double cos = (double) w / (double) diagW;
		double angle = Math.acos(cos);
		g2.rotate(angle);
		g2.translate(0, (1d - cos) * -LINE_THICKNESS / 2);

		g2.drawImage(getImageLine(), 0, 0, (int) diagW, LINE_THICKNESS, null);
		g2.dispose();
	}

	private void paintLineBetweenNodes(final int start, final int end, final Graphics2D g) {
		Graphics2D g2 = (Graphics2D) g.create();

		int x1 = this.tabNode[start].getLocation().x;
		int y1 = this.tabNode[start].getLocation().y;
		int x2 = this.tabNode[end].getLocation().x;
		int y2 = this.tabNode[end].getLocation().y;

		paintLine(x1, y1, x2, y2, g2);
	}

	public Node[] getTabNode() {
		if (this.tabNode == null) {
			this.tabNode = new Node[this.columnNumber * this.rowNumber];
			for (int i = 0; i < this.columnNumber * this.rowNumber; i++) {
				this.tabNode[i] = new Node(i);
			}
		}
		return this.tabNode;
	}

	public void reset() {
		for (Node n : getTabNode()) {
			n.setSelected(false);
		}
		this.listSelect.clear();
		this.oldIndexSelected = -1;
		repaint();
	}

	public class Node extends JComponent {
		int index;
		boolean selected;
		float animProgress;

		public Node(final int index) {
			this.selected = false;
			this.index = index;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (!isSelected()) {
				g.drawImage(getImageNode(), 2, 2, NODE_SIZE - 4, NODE_SIZE - 4, null);
			} else {
				g.drawImage(getImageSelected(), 0, 0, null);
			}
			// g.setColor(Color.RED);
			// g.drawString(this.index + "", 3, 20);
		}

		public void setSelected(final boolean selected) {
			this.selected = selected;
			if (selected) {
				startGhost();
			}
			repaint();
		}

		public boolean isSelected() {
			return this.selected;
		}

		public void setAnimProgress(final float animProgress) {
			this.animProgress = animProgress;
			AndroidLikePasswordPanel.this.repaint();
		}

		public float getAnimProgress() {
			return this.animProgress;
		}

		public void startGhost() {
			Timeline anim = new Timeline(this);
			anim.setDuration(ANIM_DURATION);
			anim.addPropertyToInterpolate("animProgress", 0f, 1f);
			anim.addCallback(new TimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
						final float durationFraction, final float timelinePosition) {
					if (newState == TimelineState.DONE) {
						setAnimProgress(-1f);
					}
				}
			});
			anim.play();
		}
	}

	public String getPassword() {
		StringBuilder sb = new StringBuilder();
		for (Integer i : this.listSelect) {
			sb.append(i);
		}
		return sb.toString();
	}

	public BufferedImage getImageLine() {
		if (this.imageLine == null) {
			this.imageLine = GraphicsUtilities.createCompatibleTranslucentImage(1, LINE_THICKNESS);
			Graphics2D g = (Graphics2D) this.imageLine.createGraphics();

			g.setColor(Color.YELLOW);
			g.fillRect(0, 0, this.imageLine.getWidth(), this.imageLine.getHeight());

			Color[] colors = new Color[] { new Color(0, 0, 0, 0), new Color(0, 0, 0, 255), new Color(0, 0, 0, 0) };
			float[] fractions = new float[] { 0, 0.5f, 1 };
			Point start = new Point(this.imageLine.getWidth() / 2, 0);
			Point end = new Point(this.imageLine.getWidth() / 2, this.imageLine.getHeight());
			LinearGradientPaint lgp = new LinearGradientPaint(start, end, fractions, colors);
			g.setPaint(lgp);

			g.setComposite(AlphaComposite.DstIn.derive(0.5f));
			g.fillRect(0, 0, this.imageLine.getWidth(), this.imageLine.getHeight());

			g.dispose();
		}
		return this.imageLine;
	}
}
