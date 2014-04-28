package com.sicpa.standard.sasscl.wizard.view.box;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.sasscl.wizard.utils.BrowserControl;

public class Box extends JPanel {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(200, 200);
				f.getContentPane().setLayout(new MigLayout("fill"));
				Box box = new Box();
				box.text = "OFFLINE\nCOUNTING";
				f.getContentPane().add(box, "grow");
				f.setVisible(true);
			}
		});
	}

	private static final long serialVersionUID = 1L;

	protected int xOffset = 5;
	protected int yOffset = 5;
	protected String text;
	protected Color color = Color.ORANGE;
	protected String helpUrl;

	protected static Icon helpIcon;
	protected static Icon helpIconSelected;
	protected JLabel labelHelp;

	public static Icon getHelpIcon() {
		if (helpIcon == null) {
			helpIcon = new ImageIcon(getIcon(0.4f));
		}
		return helpIcon;
	}

	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
		getLabelHelp().setVisible((this.helpUrl != null));
	}

	public static Icon getHelpIconSelected() {
		if (helpIconSelected == null) {
			helpIconSelected = new ImageIcon(getIcon(1f));
		}
		return helpIconSelected;
	}

	public static BufferedImage getIcon(float alpha) {

		Icon icon = SubstanceCoreUtilities.getIcon("resource/32/help-browser.png");
		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(icon.getIconWidth(),
				icon.getIconHeight());
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setComposite(AlphaComposite.SrcOver.derive(alpha));
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		return ImageUtils.createThumbnailKeepRatio(img, icon.getIconWidth() / 2, icon.getIconHeight() / 2);
	}

	public Box() {
		setOpaque(false);
		setPreferredSize(new Dimension(150, 50));
		setFont(SicpaFont.getFont(14));
		setLayout(new MigLayout("fill,inset 0 0 0 0"));
		add(getLabelHelp(), "bottom,right,gap right " + (xOffset + 1));

	}

	public JLabel getLabelHelp() {
		if (labelHelp == null) {
			labelHelp = new JLabel(getHelpIcon());
			labelHelp.setVisible(false);
			labelHelp.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					labelHelpMouseEntered();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					labelHelpMouseExited();
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					labelHelpMouseClicked();
				}
			});
		}
		return labelHelp;
	}

	private void labelHelpMouseEntered() {
		getLabelHelp().setIcon(getHelpIconSelected());
	}

	private void labelHelpMouseExited() {
		getLabelHelp().setIcon(getHelpIcon());
	}

	private void labelHelpMouseClicked() {
		BrowserControl.displayURL(helpUrl);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnQualityRendering(g2);

		int w = getWidth() - xOffset;
		int h = getHeight() - yOffset;

		// front
		g2.setColor(color);
		g2.fillRect(0, yOffset, w, h - 1);
		g2.setColor(color.darker());
		g2.drawRect(0, yOffset, w, h - 1);

		// top
		Polygon polyTop = new Polygon(new int[] { 0, xOffset, getWidth(), w }, new int[] { yOffset, 0, 0, yOffset }, 4);
		g2.setColor(color);
		g2.fillPolygon(polyTop);
		g2.setColor(color.darker());
		g2.drawPolygon(polyTop);

		// right
		Polygon polyLeft = new Polygon(new int[] { w, getWidth() - 1, getWidth() - 1, w }, new int[] { yOffset, 0, h,
				getHeight() }, 4);
		g2.setColor(color);
		g2.fillPolygon(polyLeft);
		g2.setColor(color.darker());
		g2.drawPolygon(polyLeft);
		if (text != null) {
			PaintUtils.drawMultiLineText(g2, text, w, true, 0, yOffset, Color.BLACK);
		}

	}

}
