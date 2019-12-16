package com.sicpa.standard.gui.demo.paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.BlendComposite;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.jhlabs.image.GaussianFilter;

public class CompositingSwingX extends JPanel {
	private BlendComposite[] rules = { BlendComposite.Add, BlendComposite.Average, BlendComposite.Blue,
			BlendComposite.Color, BlendComposite.ColorBurn, BlendComposite.ColorDodge, BlendComposite.Darken,
			BlendComposite.Difference, BlendComposite.Exclusion, BlendComposite.Freeze, BlendComposite.Glow,
			BlendComposite.Green, BlendComposite.HardLight, BlendComposite.Heat, BlendComposite.Hue,
			BlendComposite.InverseColorBurn, BlendComposite.InverseColorDodge, BlendComposite.Lighten,
			BlendComposite.Luminosity, BlendComposite.Multiply, BlendComposite.Negation, BlendComposite.Overlay,
			BlendComposite.Red, BlendComposite.Reflect, BlendComposite.Saturation, BlendComposite.Screen,
			BlendComposite.SoftBurn, BlendComposite.SoftDodge, BlendComposite.SoftLight, BlendComposite.Stamp,
			BlendComposite.Subtract };

	private String[] rulesNames = { "Add", "Average", "Blue", "Color", "ColorBurn", "ColorDodge", "Darken",
			"Difference", "Exclusion", "Freeze", "Glow", "Green", "HardLight", "Heat", "Hue", "InverseColorBurn",
			"InverseColorDodge", "Lighten", "Luminosity", "Multiply", "Negation", "Overlay", "Red", "Reflect",
			"Saturation", "Screen", "SoftBurn", "SoftDodge", "SoftLight", "Stamp", "Subtract" };

	private Color c1 = Color.BLUE;
	private Color c2 = Color.GREEN;

	private JButton buttonColor1;
	private JButton buttonColor2;

	public CompositingSwingX() {
		initGUI();
	}

	private JLabel labelAlpha;
	private JSlider slider;

	private void initGUI() {
		setLayout(new MigLayout());

		add(getLabelAlpha(), "wrap");
		add(getSlider());
		add(getButtonColor1());
		add(getButtonColor2());
	}

	public JSlider getSlider() {
		if (this.slider == null) {
			this.slider = new JSlider();
			this.slider.setOpaque(false);
			this.slider.setMinimum(0);
			this.slider.setMaximum(100);
			this.slider.setValue(100);

			this.slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					CompositingSwingX.this.alpha = (float) CompositingSwingX.this.slider.getValue() / 100;
					getLabelAlpha().setText("Alpha:" + (CompositingSwingX.this.alpha));
					repaint();
				}
			});
		}
		return this.slider;
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
		int x = 20;
		int y = 30;
		for (int i = 0; i < this.rules.length; x += 80, i++) {
			if (i % 10 == 0) {
				x = 50;
				y += 100;
			}
			BufferedImage buffImg = GraphicsUtilities.createCompatibleTranslucentImage(60, 60);
			{
				Graphics2D gbi = buffImg.createGraphics();

				gbi.setPaint(this.c1);
				gbi.fillRect(0, 0, 40, 40);

				gbi.setComposite(this.rules[i].derive(this.alpha));

				gbi.setPaint(this.c2);
				// gbi.fillRect(5, 5, 40, 40);
				gbi.drawImage(getGlowImage(), 5, 5, null);
			}

			g2d.drawImage(buffImg, x, y, null);
			g2d.setColor(Color.BLACK);
			g2d.drawString(this.rulesNames[i], x, y + 70);
		}
	}

	public JButton getButtonColor1() {
		if (this.buttonColor1 == null) {
			this.buttonColor1 = new JButton("...");
			this.buttonColor1.setBackground(this.c1);
			this.buttonColor1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonColor1ActionPerformed();
				}
			});
		}
		return this.buttonColor1;
	}

	public JButton getButtonColor2() {
		if (this.buttonColor2 == null) {
			this.buttonColor2 = new JButton("...");
			this.buttonColor2.setBackground(this.c2);
			this.buttonColor2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonColor2ActionPerformed();
				}
			});
		}
		return this.buttonColor2;
	}

	private void buttonColor1ActionPerformed() {
		Color c = JColorChooser.showDialog(this.buttonColor1, "", this.c1);
		if (c != null) {
			this.c1 = c;
			this.buttonColor1.setBackground(this.c1);
			repaint();
		}
	}

	private void buttonColor2ActionPerformed() {
		Color c = JColorChooser.showDialog(this.buttonColor1, "", this.c2);
		if (c != null) {
			this.c2 = c;
			this.buttonColor2.setBackground(this.c2);
			this.glowImage = null;
			repaint();
		}
	}

	private BufferedImage glowImage;
	int blurRadius = 15;

	public BufferedImage getGlowImage() {
		if (this.glowImage == null) {
			this.glowImage = GraphicsUtilities.createCompatibleTranslucentImage(this.blurRadius * 2 + 55,
					this.blurRadius * 2 + 55);
			Graphics2D g = (Graphics2D) this.glowImage.createGraphics();
			g.setColor(this.c2);
			g.fillOval(this.blurRadius, this.blurRadius, 55, 55);

			g.drawRect(0, 0, this.glowImage.getWidth() - 1, this.glowImage.getHeight() - 1);

			g.dispose();
			new GaussianFilter(this.blurRadius).filter(this.glowImage, this.glowImage);
		}
		return this.glowImage;
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Composition");
				frame.add(new CompositingSwingX());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(900, 600);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
