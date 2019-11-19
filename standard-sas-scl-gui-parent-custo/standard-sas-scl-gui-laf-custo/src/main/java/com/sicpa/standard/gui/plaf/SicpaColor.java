package com.sicpa.standard.gui.plaf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.plaf.utils.SicpaLookAndFeelRessourceUtils;

public class SicpaColor {

	public final static Color CLOUD_GREY = getColor("CLOUD_GREY", new Color(246, 246, 247));
	public final static Color RED = getColor("RED", new Color(238, 28, 35));
	public final static Color ORANGE = new Color(243, 153, 0);

	public final static Color BLUE_DARK = getColor("BLUE_DARK", new Color(0, 31, 55));
	public final static Color BLUE_MEDIUM = getColor("BLUE_MEDIUM", new Color(0, 152, 219));
	public final static Color BLUE_LIGHT = getColor("BLUE_LIGHT", new Color(180, 220, 255));
	public final static Color BLUE_ULTRA_LIGHT = getColor("BLUE_ULTRA_LIGHT", new Color(226, 244, 253));
	public final static Color SICPA_BLUE = getColor("SICPA_BLUE", new Color(3, 59, 90));

	public static final Color GREEN_DARK = getColor("GREEN_DARK", new Color(80, 143, 32));
	public static final Color GREEN_LIGHT = getColor("GREEN_LIGHT", new Color(171, 195, 139));

	public static final Color YELLOW = getColor("YELLOW", new Color(255, 255, 149));
	// public static final Color YELLOW_SIGNAL = new Color(240, 171, 0);

	// public static final Color PURPLE_SIGNAL = new Color(147, 80, 158);

	public static final Color GRAY = getColor("GRAY", new Color(137, 150, 160));

	public static final Color BROWN = getColor("BROWN", new Color(190, 143, 110));

	private static Properties prop;

	private static void initColorsProperties() {
		prop = new Properties();
		prop.putAll(SicpaLookAndFeelCusto.getExtProperties());
	}

	@SuppressWarnings("serial")
	private static class LabelColorRenderer extends JLabel {
		public LabelColorRenderer(final Color c, final String name) {
			setIcon(getColorIcon(c));
			String t = "<html>" + name + "<br>(R=" + c.getRed();
			t += " G=" + c.getGreen();
			t += " B=" + c.getBlue() + ")";
			setText(t);
		}

		private static Icon getColorIcon(final Color c) {
			BufferedImage buff = GraphicsUtilities.createCompatibleImage(50, 50);
			Graphics g = buff.getGraphics();
			g.setColor(c);
			g.fillRect(0, 0, 50, 50);
			g.dispose();
			return new ImageIcon(buff);
		}
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setSize(800, 600);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JPanel p = new JPanel(new MigLayout("wrap 3"));
				p.setOpaque(true);
				p.setBackground(Color.WHITE);
				f.getContentPane().setLayout(new BorderLayout());
				JScrollPane jsp = new JScrollPane(p);

				f.getContentPane().add(jsp);

				Field[] fields = SicpaColor.class.getFields();
				for (Field field : fields) {
					if (field.getType() == Color.class) {
						try {
							p.add(new LabelColorRenderer((Color) field.get(null), field.getName()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				f.setVisible(true);
			}
		});
	}

	private static Color getColor(final String key, final Color defaultColor) {
		if (prop == null) {
			initColorsProperties();
		}
		return SicpaLookAndFeelRessourceUtils.getColor("com.sicpa.standard.gui.color." + key, prop, defaultColor);
	}
}
