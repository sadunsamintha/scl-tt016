package com.sicpa.standard.gui.plaf.colorScheme;

import java.awt.Color;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.colorscheme.BaseDarkColorScheme;
import org.pushingpixels.substance.api.colorscheme.BaseLightColorScheme;

public class SicpaColorScheme {
	public interface SicpaColorSchemeSetter {
		public void setUltraLightColor(final Color ultraLightColor);

		public void setExtraLightColor(final Color extraLightColor);

		public void setLightColor(final Color lightColor);

		public void setMidColor(final Color midColor);

		public void setDarkColor(final Color darkColor);

		public void setUltraDarkColor(final Color ultraDarkColor);

		public void setForegroundColor(final Color foregroundColor);
	}

	public static class SicpaLightColorScheme extends BaseLightColorScheme implements SicpaColorSchemeSetter {
		private Color ultraLightColor;// = new Color(149, 178, 251).brighter();

		private Color extraLightColor;// = new Color(149, 178, 251).brighter();

		private Color lightColor;// = new Color(149, 178, 251);

		private Color midColor;// = new Color(180, 220, 250);

		private Color darkColor;// = new Color(75, 117, 221).darker();

		private Color ultraDarkColor;// = new Color(75, 117, 221).darker();

		private Color foregroundColor;// = Color.BLACK;

		public SicpaLightColorScheme(final String title) {
			super(title);
		}

		public Color getForegroundColor() {
			return this.foregroundColor;
		}

		public Color getUltraLightColor() {
			return this.ultraLightColor;
		}

		public Color getExtraLightColor() {
			return this.extraLightColor;
		}

		public Color getLightColor() {
			return this.lightColor;
		}

		public Color getMidColor() {
			return this.midColor;
		}

		public Color getDarkColor() {
			return this.darkColor;
		}

		public Color getUltraDarkColor() {
			return this.ultraDarkColor;
		}

		public void inject(final SubstanceColorScheme src) {
			this.foregroundColor = src.getForegroundColor();
			this.darkColor = src.getDarkColor();
			this.extraLightColor = src.getExtraLightColor();
			this.lightColor = src.getLightColor();
			this.midColor = src.getMidColor();
			this.ultraDarkColor = src.getUltraDarkColor();
			this.ultraLightColor = src.getUltraLightColor();
		}

		public void setUltraLightColor(final Color ultraLightColor) {
			this.ultraLightColor = ultraLightColor;
		}

		public void setExtraLightColor(final Color extraLightColor) {
			this.extraLightColor = extraLightColor;
		}

		public void setLightColor(final Color lightColor) {
			this.lightColor = lightColor;
		}

		public void setMidColor(final Color midColor) {
			this.midColor = midColor;
		}

		public void setDarkColor(final Color darkColor) {
			this.darkColor = darkColor;
		}

		public void setUltraDarkColor(final Color ultraDarkColor) {
			this.ultraDarkColor = ultraDarkColor;
		}

		public void setForegroundColor(final Color foregroundColor) {
			this.foregroundColor = foregroundColor;
		}
	}

	public static class SicpaDarkColorScheme extends BaseDarkColorScheme implements SicpaColorSchemeSetter {
		private Color ultraLightColor;// = new Color(149, 178, 251).brighter();

		private Color extraLightColor;// = new Color(149, 178, 251).brighter();

		private Color lightColor;// = new Color(149, 178, 251);

		private Color midColor;// = new Color(180, 220, 250);

		private Color darkColor;// = new Color(75, 117, 221).darker();

		private Color ultraDarkColor;// = new Color(75, 117, 221).darker();

		private Color foregroundColor;// = Color.BLACK;

		public SicpaDarkColorScheme(final String title) {
			super(title);
		}

		public Color getForegroundColor() {
			return this.foregroundColor;
		}

		public Color getUltraLightColor() {
			return this.ultraLightColor;
		}

		public Color getExtraLightColor() {
			return this.extraLightColor;
		}

		public Color getLightColor() {
			return this.lightColor;
		}

		public Color getMidColor() {
			return this.midColor;
		}

		public Color getDarkColor() {
			return this.darkColor;
		}

		public Color getUltraDarkColor() {
			return this.ultraDarkColor;
		}

		public void inject(final SubstanceColorScheme src) {
			this.foregroundColor = src.getForegroundColor();
			this.darkColor = src.getDarkColor();
			this.extraLightColor = src.getExtraLightColor();
			this.lightColor = src.getLightColor();
			this.midColor = src.getMidColor();
			this.ultraDarkColor = src.getUltraDarkColor();
			this.ultraLightColor = src.getUltraLightColor();
		}

		public void setUltraLightColor(final Color ultraLightColor) {
			this.ultraLightColor = ultraLightColor;
		}

		public void setExtraLightColor(final Color extraLightColor) {
			this.extraLightColor = extraLightColor;
		}

		public void setLightColor(final Color lightColor) {
			this.lightColor = lightColor;
		}

		public void setMidColor(final Color midColor) {
			this.midColor = midColor;
		}

		public void setDarkColor(final Color darkColor) {
			this.darkColor = darkColor;
		}

		public void setUltraDarkColor(final Color ultraDarkColor) {
			this.ultraDarkColor = ultraDarkColor;
		}

		public void setForegroundColor(final Color foregroundColor) {
			this.foregroundColor = foregroundColor;
		}
	}

	public static SubstanceColorScheme create(final SubstanceColorScheme c) {
		if (c.isDark()) {
			SicpaDarkColorScheme cs = new SicpaDarkColorScheme(c.getDisplayName());
			cs.inject(c);
			return cs;
		} else {
			SicpaLightColorScheme cs = new SicpaLightColorScheme(c.getDisplayName());
			cs.inject(c);
			return cs;
		}
	}
}
