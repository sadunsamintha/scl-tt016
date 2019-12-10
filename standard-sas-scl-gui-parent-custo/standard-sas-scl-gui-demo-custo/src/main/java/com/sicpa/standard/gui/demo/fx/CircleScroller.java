package com.sicpa.standard.gui.demo.fx;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ReflectionRenderer;

import com.sicpa.standard.gui.components.transition.impl.PinchTransition;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class CircleScroller extends JPanel {
	private static final int ItemWidthSize = 50;
	private static final int ItemHeighSize = 50;

	private float animProgress = 0;
	private ArrayList<Item> items;
	private JPanel scrollPanel;

	private static final double step = PI / 50;
	private double offset;

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	public CircleScroller() {
		// not needed for java 6_11+ , only needed for 6_10
		RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
		this.items = new ArrayList<Item>();
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0"));
		add(getScrollPanel(), "grow,push,wrap");
		setOpaque(false);
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(final MouseWheelEvent e) {
				thisMouseWheelMoved(e);
			}
		});
	}

	private void thisMouseWheelMoved(final MouseWheelEvent evt) {
		if (evt.getWheelRotation() > 0) {
			moveToNext();
		} else {
			moveToPrevious();
		}
	}

	private JPanel getScrollPanel() {
		if (this.scrollPanel == null) {
			this.scrollPanel = new JPanel();
			this.scrollPanel.setLayout(null);
			this.scrollPanel.setOpaque(false);
		}
		return this.scrollPanel;
	}

	public void addItem(final Item i) {
		i.setSize(ItemWidthSize, ItemHeighSize);
		this.items.add(i);
		final int index = this.items.size() - 1;
		i.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				moveTo(index);
			}
		});

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getScrollPanel().add(i);
			}
		});
	}

	public void moveTo(final int index) {
		if (CircleScroller.this.nearest == index) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean previous = isPreviousFaster(index);

				while (CircleScroller.this.nearest != index) {
					if (previous) {
						moveToPrevious();
					} else {
						moveToNext();
					}
					ThreadUtils.sleepQuietly(33);
				}

				if (previous) {
					moveToPrevious();
					ThreadUtils.sleepQuietly(33);
					moveToPrevious();
					ThreadUtils.sleepQuietly(33);
					moveToPrevious();
					ThreadUtils.sleepQuietly(33);
				} else {
					moveToNext();
					ThreadUtils.sleepQuietly(33);
					moveToNext();
					ThreadUtils.sleepQuietly(33);
					moveToNext();
					ThreadUtils.sleepQuietly(33);
				}
			}
		}).start();
	}

	private boolean isPreviousFaster(final int index) {
		int d = 0;

		int current = index;

		for (int i = 0; i < this.items.size(); i++) {
			if (current >= CircleScroller.this.items.size()) {
				current = 0;
			}
			if (current == this.nearest) {
				break;
			}
			d++;
			current++;
		}
		return d > this.items.size() / 2;
	}

	int nearest = -1;
	int nearestY = -1;

	private void placeOneitem(final int i) {
		int size = getSize(i);
		int y = (int) getPosY(i);
		this.items.get(i).setBounds((int) getPosX(i), y, size, size * 2);

		if (this.nearest == -1 || this.nearestY < y) {
			this.nearest = i;
			this.nearestY = y;
		}

	}

	private int getSize(final int i) {
		// double s=0.5d+sin( (PI/this.items.size())*i );
		// return ItemWidthSize *2 -(int) (s*ItemWidthSize );

		double s = sin(getStep() * i + PI / 2 + step * this.offset);
		s += 1.5f;
		s = s / 2;
		s += 0.5;

		return (int) (s * ItemWidthSize);

	}

	private double getStep() {
		return 2 * PI / (this.items.size());
	}

	private double getPosX(final int i) {
		return cos(getStep() * i + PI / 2 + step * this.offset) * 200 + 250 - ItemWidthSize / 2;
	}

	private double getPosY(final int i) {
		double res = sin(getStep() * i + PI / 2 + step * this.offset) * 50 + 200 - ItemHeighSize / 2;
		return res;
	}

	public void placeItems() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				CircleScroller.this.nearest = -1;
				CircleScroller.this.nearestY = -1;
				for (int i = 0; i < CircleScroller.this.items.size(); i++) {
					placeOneitem(i);
				}

				int z = 1;
				int half = (CircleScroller.this.items.size() - 1) / 2;
				getScrollPanel().setComponentZOrder(CircleScroller.this.items.get(CircleScroller.this.nearest), 0);
				int current = CircleScroller.this.nearest + 1;
				// start for the left item
				for (int i = 0; i < half; i++) {
					if (current >= CircleScroller.this.items.size()) {
						current = 0;
					}
					// System.out.println(current + "=>" + z);
					getScrollPanel().setComponentZOrder(CircleScroller.this.items.get(current), z);
					z += 2;
					current++;
				}

				// for the right items
				z = 0;
				current = CircleScroller.this.nearest - 1;
				for (int i = 0; i < half; i++) {
					if (current < 0) {
						current = CircleScroller.this.items.size() - 1;
					}
					// System.out.println(current + "=>" + z);
					getScrollPanel().setComponentZOrder(CircleScroller.this.items.get(current), z);
					z += 2;
					current--;
				}

				// for the top ones => the farest one
				if ((CircleScroller.this.items.size() - 1) % 2 == 1) {
					int n;
					if (CircleScroller.this.nearest + half + 1 < CircleScroller.this.items.size()) {
						n = CircleScroller.this.nearest + half + 1;
					} else {
						n = CircleScroller.this.nearest - half - 1;
					}
					z = CircleScroller.this.items.size() - 1;
					// System.out.println(n + "=>" + (z));
					getScrollPanel().setComponentZOrder(CircleScroller.this.items.get(n), z);
				}

				repaint();
			}
		});
	}

	public void moveToNext() {
		this.offset++;
		placeItems();
	}

	public void moveToPrevious() {
		this.offset--;
		placeItems();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				final JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill,inset 0 0 0 0"));
				WindowsUtils.setOpaque(f, false);

				final CircleScroller scroller = new CircleScroller();
				f.getContentPane().add(scroller, "push,grow,wrap");
				Draggable.makeDraggable(f);
				JPanel p = new JPanel();
				p.setBackground(new Color(0, 0, 0, 128));
				f.add(p, "grow,h 5");

				JButton closeButton = new JButton("Close");
				closeButton.setContentAreaFilled(false);
				closeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						new PinchTransition(f).startCloseTransition();
					}
				});
				p.add(closeButton, BorderLayout.EAST);

				for (int i = 0; i < 10; i++) {
					BufferedImage img = GraphicsUtilities
							.createCompatibleTranslucentImage(ItemWidthSize, ItemHeighSize);
					Graphics2D g = (Graphics2D) img.createGraphics();
					PaintUtils.turnOnAntialias(g);
					g.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
							(int) (Math.random() * 255)));
					g.fillRect(0, 0, img.getWidth(), img.getHeight());
					// g.setColor(Color.WHITE);
					// g.drawRect(0, 0, img.getWidth() - 1, img.getHeight() -
					// 1);
					g.setFont(g.getFont().deriveFont(50f));
					g.setColor(Color.BLACK);
					g.drawString("" + i, 0, 50);
					g.dispose();

					scroller.addItem(new Item(img));
				}
				f.setSize(500, 450);
				f.setVisible(true);
				scroller.placeItems();
			}
		});
	}

	public static class Item extends JPanel {
		private BufferedImage img;
		private BufferedImage shadowImg;
		private BufferedImage mirrorImg;

		public Item(final BufferedImage buff) {
			this.img = buff;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(final Graphics g) {
			if (this.shadowImg == null) {
				this.shadowImg = ImageUtils.getShadowedImage(this.img);
			}
			if (this.mirrorImg == null) {
				ReflectionRenderer rr = new ReflectionRenderer();
				this.mirrorImg = rr.createReflection(this.img);
			}
			this.img = null;

			int originalShadowSize = 7;
			float percent = ((float) getWidth()) / ItemWidthSize;
			int shadowSize = (int) (percent * originalShadowSize);

			g.drawImage(this.shadowImg, 0, 0, getWidth(), getHeight() / 2, null);
			g.drawImage(this.mirrorImg, 0, getHeight() / 2, getWidth() - shadowSize, getHeight() / 4, null);
		}
	}
}
