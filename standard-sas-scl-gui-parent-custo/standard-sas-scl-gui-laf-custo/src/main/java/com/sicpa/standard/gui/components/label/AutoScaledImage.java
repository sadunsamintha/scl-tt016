package com.sicpa.standard.gui.components.label;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class AutoScaledImage extends JLabel {

	protected static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();

				JFrame f = new JFrame();
				final JPanel panel = new JPanel(new MigLayout("fill,wrap 1"));
				JScrollPane scroll = new JScrollPane(panel);
				f.getContentPane().add(scroll);

				new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < 3; i++) {
							ThreadUtils.sleepQuietly(100);
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									panel.add(new AutoScaledImage(ImageUtils.createRandomStrippedImage(900, 300)),
											"grow,pushx");
									panel.revalidate();
								}
							});
						}
					}
				}).start();
				f.setVisible(true);
				f.setSize(300, 300);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			}
		});
	}

	protected Image image;
	protected BufferedImage scaledImage;

	public AutoScaledImage(final Image image) {
		this.image = image;
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				AutoScaledImage.this.scaledImage = null;
				repaint();
			}
		});
	}

	public void setImage(Image image) {
		this.image = image;
		scaledImage = null;
		repaint();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (getWidth() > 0 && getHeight() > 0) {
			if (this.scaledImage == null) {
				generateScaleImage();
			}
			if (scaledImage != null) {
				g.drawImage(scaledImage, 0, 0, null);
			}
		}
	}

	protected void generateScaleImage() {
		try {
			this.scaledImage = ImageUtils.createThumbnailKeepRatio(this.image, getWidth(), getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getScaledImage() {
		return scaledImage;
	}
}
