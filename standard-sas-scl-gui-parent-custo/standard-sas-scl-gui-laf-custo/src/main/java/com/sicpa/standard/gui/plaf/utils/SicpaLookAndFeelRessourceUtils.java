package com.sicpa.standard.gui.plaf.utils;

import java.awt.Color;
import java.util.Properties;

public class SicpaLookAndFeelRessourceUtils {
	public static Color getColor(final String key, final Properties prop, final Color defaultColor) {
		Color c = defaultColor;

		String text = prop.getProperty(key);

		if (text != null) {
			int index = text.indexOf("Color(");
			if (index != -1) {
				index += "Color(".length();
				int index2 = text.indexOf(")");
				if (index2 != -1) {
					try {
						String n = text.substring(index, index2);
						String[] tab = n.split(",");
						int red = Integer.parseInt(tab[0].trim());
						int green = Integer.parseInt(tab[1].trim());
						int blue = Integer.parseInt(tab[2].trim());
						c = new Color(red, green, blue);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return c;
	}
}
