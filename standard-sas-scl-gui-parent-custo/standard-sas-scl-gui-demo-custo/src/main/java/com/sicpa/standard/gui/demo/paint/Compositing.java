package com.sicpa.standard.gui.demo.paint;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

public class Compositing extends JPanel {

	private AlphaComposite[] rules = { AlphaComposite.Dst, AlphaComposite.DstOut, AlphaComposite.DstAtop,
			AlphaComposite.DstIn, AlphaComposite.DstOver, AlphaComposite.Src, AlphaComposite.SrcAtop,
			AlphaComposite.SrcOut, AlphaComposite.SrcIn, AlphaComposite.SrcOver, AlphaComposite.Xor };

	private String[] rulesNames = { "DST", "DST_OUT", "DST_ATOP", "DST_IN", "DST_OVER", "SRC", "SRC_ATOP", "SRC_OUT",
			"SRC_IN", "SRC_OVER", "XOR" };

	public Compositing() {
		initGUI();
	}

	private JLabel labelAlpha;

	private void initGUI() {
		setLayout(new MigLayout());

		add(getLabelAlpha(), "wrap");

		final JSlider slider = new JSlider();
		slider.setOpaque(false);
		slider.setMinimum(0);
		slider.setMaximum(100);
		slider.setValue(100);
		add(slider);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				Compositing.this.alpha = (float) slider.getValue() / 100;
				getLabelAlpha().setText("Alpha:" + (Compositing.this.alpha));
				repaint();
			}
		});
	}

	private float alpha = 1f;

	public JLabel getLabelAlpha() {
		if (this.labelAlpha == null) {
			this.labelAlpha = new JLabel("Alpha:1");
		}
		return this.labelAlpha;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.gray);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		for (int x = 20, i = 0; i < this.rules.length; x += 80, i++) {

			BufferedImage buffImg = GraphicsUtilities.createCompatibleTranslucentImage(60, 60);
			{
				Graphics2D gbi = buffImg.createGraphics();

				gbi.setPaint(Color.blue);
				gbi.fillRect(0, 0, 40, 40);

				gbi.setComposite(this.rules[i].derive(this.alpha));

				gbi.setPaint(Color.green);
				gbi.fillRect(5, 5, 40, 40);
			}

			g2d.drawImage(buffImg, x, 50, null);
			g2d.setColor(Color.BLACK);
			g2d.drawString(this.rulesNames[i], x, 115);
		}
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Composition");
				frame.add(new Compositing());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(900, 200);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});

	}
}
