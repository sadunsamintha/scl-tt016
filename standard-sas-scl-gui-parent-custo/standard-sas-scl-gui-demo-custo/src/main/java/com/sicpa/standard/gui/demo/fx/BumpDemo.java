package com.sicpa.standard.gui.demo.fx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.callback.UIThreadTimelineCallbackAdapter;

import com.jhlabs.image.CausticsFilter;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.component.light.Light;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class BumpDemo extends JPanel {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new BumpDemoFrame();
				f.setVisible(true);
			}
		});
	}

	private final static int NODE_SIZE = 50;

	private static class BumpDemoFrame extends JFrame {
		public BumpDemoFrame() {
			setSize(500, 500);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			BumpDemo demoPanel = new BumpDemo();
			JPanel background = demoPanel.getBackgroundPanel();

			background.setLayout(new MigLayout("fill"));
			background.add(demoPanel, "grow, pad 100 100 0 0, gap right 100, gap bottom 100");

			getContentPane().add(background);

			WindowsUtils.setOpaque(this, false);
			RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);

			Draggable.makeDraggable(demoPanel, this);
		}
	}

	/**
	 * panel where the bump ghost are rendered
	 */
	private JPanel backgroundPanel;
	/**
	 * ghost bump to be rendered
	 */
	private ArrayList<GhostBump> ghosts = new ArrayList<GhostBump>();

	private BufferedImage backgroundImage;

	private ArrayList<Node> nodes = new ArrayList<Node>();

	public BumpDemo() {
		initGUI();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				BumpDemo.this.backgroundImage = null;
				repaint();
			}
		});
	}

	private void initGUI() {
		setLayout(null);
		setOpaque(false);
		Node b = new Node();
		b.setBounds(50, 50, NODE_SIZE, NODE_SIZE);
		add(b);
		this.nodes.add(b);

		b = new Node();
		b.setBounds(150, 150, NODE_SIZE, NODE_SIZE);
		add(b);
		this.nodes.add(b);

		b = new Node();
		b.setBounds(150, 100, NODE_SIZE, NODE_SIZE);
		add(b);
		this.nodes.add(b);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					Node n = new Node();
					n.setBounds(e.getX() - NODE_SIZE / 2, e.getY() - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);
					add(n);
					BumpDemo.this.nodes.add(n);
				}
			}
		});

	}

	public BufferedImage getBackgroundImage() {
		if (this.backgroundImage == null) {
			this.backgroundImage = GraphicsUtilities.createCompatibleImage(getWidth(), getHeight());

			new CausticsFilter().filter(this.backgroundImage, this.backgroundImage);
			new ColorTintFilter(SicpaColor.BLUE_DARK, 0.9f).filter(this.backgroundImage, this.backgroundImage);
		}
		return this.backgroundImage;
	}

	public JPanel getBackgroundPanel() {
		if (this.backgroundPanel == null) {
			this.backgroundPanel = new JPanel() {
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);
					g.drawImage(getBackgroundImage(), BumpDemo.this.getX(), BumpDemo.this.getY(), null);

					if (BumpDemo.this.ghosts != null) {
						for (int i = 0; i < BumpDemo.this.ghosts.size(); i++) {
							BumpDemo.this.ghosts.get(i).paint((Graphics2D) g);
						}
					}
				}
			};
			this.backgroundPanel.setOpaque(false);
		}
		return this.backgroundPanel;
	}

	public class Node extends Light {
		private Point first;
		private Point last;
		private Point next = new Point();
		private double dx;
		private double dy;
		private double a;
		private double b;
		// private double angle;

		private boolean topBottom;
		private Timeline timeline;

		private Ellipse2D elli;

		private long lastColision = -1;

		@Override
		public void setLocation(final Point p) {

			if (this.elli == null) {
				this.elli = new Ellipse2D.Float();
			}

			this.elli.setFrame(getBounds());

			if (System.currentTimeMillis() - this.lastColision > 100 || this.lastColision == -1) {
				for (Node n : BumpDemo.this.nodes) {
					if (n != this) {
						if (this.elli.intersects(n.getBounds())) {
							this.timeline.abort();
							if (n.timeline != null) {
								// n.timeline.abort();
							}

							Point tmp = this.first;
							this.first = this.last;
							this.last = tmp;

							// tmp=n.first;
							// n.first=n.last;
							// n.last=tmp;

							this.lastColision = System.currentTimeMillis();
							compute();

							// return;

							addBump();

						}
					}
				}
			}
			super.setLocation(p);
		}

		private void addBump() {
			// create ghost bump
			final GhostBump ghostBump = new GhostBump();
			BumpDemo.this.ghosts.add(ghostBump);
			ghostBump.location = Node.this.getLocation();
			ghostBump.location = SwingUtilities.convertPoint(Node.this.getParent(), ghostBump.location,
					BumpDemo.this.backgroundPanel);
			Timeline t = new Timeline(ghostBump);
			t.addPropertyToInterpolate("anim", 0f, 1f);
			t.addCallback(new TimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
						final float durationFraction, final float timelinePosition) {
					if (newState == TimelineState.DONE) {
						BumpDemo.this.ghosts.remove(ghostBump);
						BumpDemo.this.repaint();
					}
				}
			});
			t.play();
		}

		public Node() {
			super(Color.green);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(final MouseEvent e) {

					Node.this.first = SwingUtilities.convertPoint(Node.this, e.getPoint(), BumpDemo.this);
				}

				@Override
				public void mouseReleased(final MouseEvent e) {
					if (Node.this.timeline != null) {
						Node.this.timeline.abort();
					}
					Node.this.last = SwingUtilities.convertPoint(Node.this, e.getPoint(), BumpDemo.this);
					compute();
				}
			});
		}

		private void compute() {
			// System.out.println("###################################");

			if (this.last == null || this.first == null) {
				System.out.println(this.last + "  " + this.first);
			}
			this.dx = this.last.x - this.first.x;
			this.dy = this.last.y - this.first.y;

			// System.out.println("dx=" + this.dx);
			// System.out.println("dy=" + this.dy);

			this.a = this.dy / this.dx;
			this.b = this.first.y - this.a * this.first.x;

			// System.out.println("a=" + this.a);
			// System.out.println("b=" + this.b);

			if (this.dx == 0) {// top or bottom
				if (this.last.y > this.first.y) {
					computeBottom();
				} else {
					computeTop();
				}
			} else if (this.a == 0) {// left or right
				if (this.last.x > this.first.x) {
					computeRight();
				} else {
					computeLeft();
				}
			} else if (this.dx > 0) {// go to right
				if (this.a < 0) {// top or right
					double x3 = (-this.b / this.a);
					if (x3 > BumpDemo.this.getWidth()) {
						computeRight();
					} else {// top
						computeTop();
					}
				} else if (this.a > 0) {// bottom or right
					double x3 = (BumpDemo.this.getHeight() - this.b) / this.a;
					if (x3 > BumpDemo.this.getWidth()) {
						computeRight();
					} else {
						computeBottom();
					}
				}
			} else {// go to left
				if (this.a > 0) {// top or left
					double x3 = (-this.b / this.a);
					if (x3 > 0) {
						computeTop();
					} else {
						computeLeft();
					}
				} else
				// a<0
				{// left or bottom
					double x3 = (BumpDemo.this.getHeight() - this.b) / this.a;
					if (x3 < 0) {
						computeLeft();
					} else {
						computeBottom();
					}
				}
			}

			// System.out.println(this.next);
			// System.out.println("angle=" + Math.toDegrees(this.angle));

			if (this.timeline != null) {
				this.timeline.abort();
			}
			this.timeline = new Timeline(Node.this);
			Point to = new Point(this.next.x - Node.this.getWidth() / 2, this.next.y - Node.this.getHeight() / 2);
			Point from = Node.this.getLocation();
			this.timeline.addPropertyToInterpolate("location", from, to);

			double len = Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
			long duration = (long) ((len / 500) * 1000);
			this.timeline.setDuration(duration);
			this.timeline.addCallback(new UIThreadTimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
						final float durationFraction, final float timelinePosition) {
					if (newState == TimelineState.DONE) {

						addBump();
						moveNext();
					}
				}
			});
			this.timeline.play();
		}

		private void moveNext() {
			// find point next computation;
			if (this.topBottom) {
				this.last.x = (int) (this.next.x + this.dx);
				this.last.y = this.first.y;

				this.first.x = this.next.x;
				this.first.y = this.next.y;
			} else {
				this.last.y = (int) (this.next.x + this.dy);
				this.last.x = this.first.x;

				this.first.x = this.next.x;
				this.first.y = this.next.y;
			}
			// System.out.println("LAST=" + this.last);
			this.next = new Point();
			compute();
		}

		private void computeTop() {
			this.topBottom = true;
			// System.out.println("top");
			// double tan = this.dy / this.dx;
			// this.angle = Math.atan(tan);

			this.next.y = 0;
			if (Double.isInfinite(this.a)) {
				this.next.x = this.first.x;
			} else {
				this.next.x = (int) (-this.b / this.a);
			}
		}

		private void computeBottom() {
			this.topBottom = true;
			// System.out.println("bottom");

			// double tan = this.dy / this.dx;
			// this.angle = Math.atan(tan);

			this.next.y = BumpDemo.this.getHeight();
			if (Double.isInfinite(this.a)) {
				this.next.x = this.first.x;
			} else {
				this.next.x = (int) ((BumpDemo.this.getHeight() - this.b) / this.a);
			}
		}

		private void computeLeft() {
			this.topBottom = false;
			// System.out.println("left");

			// double tan = this.dy / this.dx;
			// this.angle = Math.atan(tan);

			this.next.x = 0;
			if (this.a == 0) {
				this.next.y = this.first.y;
			} else {
				this.next.y = (int) (this.b);
			}
		}

		private void computeRight() {
			this.topBottom = false;
			// System.out.println("right");

			// double tan = this.dy / this.dx;
			// this.angle = Math.atan(tan);

			this.next.x = BumpDemo.this.getWidth();
			if (this.a == 0) {
				this.next.y = this.first.y;
			} else {
				this.next.y = (int) (this.a * BumpDemo.this.getWidth() + this.b);
			}
		}
	}

	private static BufferedImage imageBump;

	public class GhostBump {
		Point location;
		float anim;

		private void paint(final Graphics2D g2) {
			g2.setComposite(AlphaComposite.SrcOver.derive((1f - this.anim) / 2));

			int w = (int) ((1f + this.anim * 3) * NODE_SIZE);
			int h = (int) ((1f + this.anim * 3) * NODE_SIZE);

			int x = this.location.x - (w - NODE_SIZE) / 2;
			int y = this.location.y - (h - NODE_SIZE) / 2;

			g2.drawImage(getImageBump(), x, y, w, h, null);

			g2.setComposite(AlphaComposite.SrcOver);
		}

		public BufferedImage getImageBump() {
			if (imageBump == null) {
				imageBump = GraphicsUtilities.createCompatibleTranslucentImage(NODE_SIZE, NODE_SIZE);

				int w = NODE_SIZE;

				BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(NODE_SIZE, NODE_SIZE);

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

				g2 = (Graphics2D) imageBump.createGraphics();
				g2.drawImage(img, 0, 0, null);
				g2.dispose();
			}
			return imageBump;
		}

		public void setAnim(final float alpha) {
			this.anim = alpha;
			BumpDemo.this.backgroundPanel.repaint();
		}
	}
}
