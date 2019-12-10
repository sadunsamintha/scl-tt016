package com.sicpa.standard.gui.components.buttons.taskButton;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.jhlabs.image.GaussianFilter;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;

public class TaskButtonWithBackground extends TaskButton {
	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				final JFrame f = new JFrame();
				SicpaLookAndFeelCusto.turnOnMemoryManagementWidget(f);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new BorderLayout());
				SicpaLookAndFeelCusto.flagAsWorkArea((JComponent) f.getContentPane());

				BufferedImage img = ImageUtils.createRandomStrippedImage(50);

				TaskButtonWithBackground b = new TaskButtonWithBackground("Refresh", "Fetch up to date informations",
						img);
				b.setToolTipText("Refresh ! ");
				f.getContentPane().add(b, BorderLayout.CENTER);

				b.addActionListener(new ActionListener() {

					public void actionPerformed(final ActionEvent e) {
						JOptionPane.showMessageDialog(f, "Clicked !");

					}
				});

				f.getContentPane().add(new JLabel("                 "), BorderLayout.WEST);
				f.getContentPane().add(new JLabel("  "), BorderLayout.NORTH);

				f.setSize(500, 500);
				f.setVisible(true);
			}
		});
	}

	public TaskButtonWithBackground(final String name, final String description, final BufferedImage image) {
		super(name, description, image, true);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		paintBackground((Graphics2D) g);
		super.paintComponent(g);
	}

	private void paintBackground(final Graphics2D g) {
		Rectangle r = getRectangle();
		BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
		Graphics2D gbuff = buff.createGraphics();

		int arc = 15;
		RoundRectangle2D r1 = new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, arc, arc);
		Rectangle r2 = new Rectangle(r.x, r.y, arc, r.height);
		gbuff.setComposite(AlphaComposite.SrcOver.derive(1f));
		gbuff.setColor(SicpaColor.BLUE_ULTRA_LIGHT);
		PaintUtils.turnOnAntialias(g);
		Area a = new Area(r1);
		a.add(new Area(r2));
		gbuff.fill(a);

		gbuff.setComposite(AlphaComposite.SrcAtop.derive(0.5f));
		gbuff.drawImage(getBlurr(), r.x, r.y, null);

		gbuff.dispose();

		g.drawImage(buff, 0, 0, null);
	}

	private BufferedImage blurr;

	public BufferedImage getBlurr() {
		if (this.blurr == null) {
			Rectangle r = getRectangle();

			this.blurr = GraphicsUtilities.createCompatibleTranslucentImage(r.width, r.height);
			Graphics2D g = this.blurr.createGraphics();
			float size = 9;
			g.setStroke(new BasicStroke(size));
			g.setColor(Color.BLACK);

			int arc = 15;
			RoundRectangle2D r1 = new RoundRectangle2D.Float(1, 1, r.width - 2, r.height - 2, arc, arc);
			Rectangle r2 = new Rectangle(0, 0, arc, r.height);
			Area a1 = new Area(r1);
			a1.add(new Area(r2));
			g.draw(a1);

			g.dispose();
			new GaussianFilter(30).filter(this.blurr, this.blurr);

		}
		return this.blurr;
	}

	@Override
	public boolean contains(final int x, final int y) {
		return getRectangle().contains(x, y);
	}

	@Override
	protected Rectangle getRectangle() {
		Rectangle r = new Rectangle(this.textRect);
		r.add(this.imageRect);

		r.add(new Point(r.width + 25, r.y));
		return r;
	}

}
