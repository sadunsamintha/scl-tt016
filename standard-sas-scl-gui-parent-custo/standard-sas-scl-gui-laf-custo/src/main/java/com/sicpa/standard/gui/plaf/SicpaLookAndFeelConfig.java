package com.sicpa.standard.gui.plaf;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.SubstanceDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.substance.api.painter.highlight.SubstanceHighlightPainter;

import com.sicpa.standard.gui.components.layeredComponents.lock.ui.LockableLayerUIPainter;
import com.sicpa.standard.gui.painter.SicpaBorderPainter;
import com.sicpa.standard.gui.painter.SicpaDecorationPainter;
import com.sicpa.standard.gui.painter.SicpaFillPainter;
import com.sicpa.standard.gui.painter.SicpaHighlightPainter;
import com.sicpa.standard.gui.plaf.utils.SicpaLookAndFeelRessourceUtils;

public class SicpaLookAndFeelConfig {
	// skin
	public final static String COLOR_SCHEMES_FILE_EXT = "com.sicpa.standard.gui.plaf.SicpaSkin.ColorSchemesFile";
	public final static String FILL_PAINTER = "com.sicpa.standard.gui.plaf.SicpaSkin.fillPainter";
	public final static String BORDER_PAINTER = "com.sicpa.standard.gui.plaf.SicpaSkin.borderPainter";
	public final static String DECORATION_PAINTER = "com.sicpa.standard.gui.plaf.SicpaSkin.decorationPainter";
	public final static String HIGHLIGHT_PAINTER = "com.sicpa.standard.gui.plaf.SicpaSkin.highlightPainter";

	// padded button
	public static final String PADDED_BUTTON_ENABLED_COLOR = "com.sicpa.standard.gui.components.paddedButton.color.enabled";
	public static final String PADDED_BUTTON_DISABLED_COLOR = "com.sicpa.standard.gui.components.paddedButton.color.disabled";
	public static final String PADDED_BUTTON_BORDER_WIDTH = "com.sicpa.standard.gui.components.paddedButton.border.width";
	public static final String PADDED_BUTTON_CORNER_ARC = "com.sicpa.standard.gui.components.paddedButton.corner.arc";

	// PCCFrame
	public static final String SICPA_LOGO_PCC_FRAME = "com.sicpa.standard.gui.screen.PCC.PCCFrame.SicpaLogo";
	public static final String LOGO_POWER_BY = "com.sicpa.standard.gui.screen.PCC.PCCFrame.SicpaPowerByLogo";

	// MasterPccCLientFrame
	public static final String SICPA_LOGO_MASTER_PCC_CLIENT_FRAME = "com.sicpa.standard.gui.screen.DMS.MasterPCCClientFrame.SicpaLogo";

	// Lock Effect
	public static final String LOCK_EFFECT_BACKGROUND = "com.sicpa.standard.gui.components.lock.background";
	public static final String LOCK_EFFECT_ALPHA = "com.sicpa.standard.gui.components.lock.alpha";
	public static final String LOCK_EFFECT_PAINTER = "com.sicpa.standard.gui.components.lock.painter";

	// Error lock effect
	public static final String ERROR_LOCK_EFFECT_BACKGROUND = "com.sicpa.standard.gui.components.lock.error.background";
	public static final String ERROR_LOCK_EFFECT_PAINTER = "com.sicpa.standard.gui.components.lock.error.painter";
	public static final String ERROR_LOCK_EFFECT_TEXT_INNER_COLOR = "com.sicpa.standard.gui.components.lock.error.text.innerColor";
	public static final String ERROR_LOCK_EFFECT_TEXT_OUTTER_COLOR = "com.sicpa.standard.gui.components.lock.error.text.outerColor";
	public static final String ERROR_LOCK_EFFECT_TEXT_FONT_SIZE = "com.sicpa.standard.gui.components.lock.error.text.size";

	// warning list
	public static final String PCC_FRAME_WARNING_CELL_BACKGROUND = "com.sicpa.standard.gui.screen.machineFrame.warning.cell.defaultPainter.background";
	public static final String PCC_FRAME_WARNING_LAST_CELL_BACKGROUND = "com.sicpa.standard.gui.screen.machineFrame.warning.cell.last.defaultPainter.background";
	public static final String PCC_FRAME_WARNING_CELL_FOREGROUND = "com.sicpa.standard.gui.screen.machineFrame.warning.cell.defaultPainter.foreground";
	public static final String PCC_FRAME_WARNING_LAST_CELL_FOREGROUND = "com.sicpa.standard.gui.screen.machineFrame.warning.cell.last.defaultPainter.foreground";
	public static final String PCC_FRAME_WARNING_ADD_ANIM_DURATION = "com.sicpa.standard.gui.screen.machineFrame.warning.add.animation.duration";
	public static final String PCC_FRAME_WARNING_REMOVE_ANIM_DURATION = "com.sicpa.standard.gui.screen.machineFrame.warning.remove.animation.duration";
	public static final String PCC_FRAME_WARNING_PAINTER = "com.sicpa.standard.gui.screen.machineFrame.warning.cell.painter";

	public static final String SICPA_FONT = "com.sicpa.standard.gui.font.name";

	// --------------------------------------------------------------------------
	// ----------
	public static SubstanceFillPainter getFillPainter() {
		SubstanceFillPainter painter = null;
		try {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(SicpaLookAndFeelConfig.FILL_PAINTER);
			if (o != null) {
				painter = (SubstanceFillPainter) Class.forName(o.toString()).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (painter == null) {
			painter = new SicpaFillPainter();
		}
		return painter;
	}

	public static SubstanceDecorationPainter getDecorationPainter() {
		SubstanceDecorationPainter painter = null;
		try {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(SicpaLookAndFeelConfig.DECORATION_PAINTER);
			if (o != null) {
				painter = (SubstanceDecorationPainter) Class.forName(o.toString()).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (painter == null) {
			painter = new SicpaDecorationPainter();
		}
		return painter;
	}

	public static SubstanceBorderPainter getBorderPainter() {
		SubstanceBorderPainter painter = null;
		try {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(SicpaLookAndFeelConfig.BORDER_PAINTER);
			if (o != null) {
				painter = (SubstanceBorderPainter) Class.forName(o.toString()).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (painter == null) {
			painter = new SicpaBorderPainter();
		}
		return painter;
	}

	public static SubstanceHighlightPainter getHighlightPainter() {
		SubstanceHighlightPainter painter = null;
		try {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(SicpaLookAndFeelConfig.HIGHLIGHT_PAINTER);
			if (o != null) {
				painter = (SubstanceHighlightPainter) Class.forName(o.toString()).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (painter == null) {
			painter = new SicpaHighlightPainter();
		}
		return painter;
	}

	public static URL getUrlColorSchemeFile() {
		Object schemesExt = SicpaLookAndFeelCusto.getExtProperties().get(COLOR_SCHEMES_FILE_EXT);
		URL urlSchemes;
		if (schemesExt == null) {
			urlSchemes = SicpaSkin.class.getResource("SicpaSkin.colorschemes");
		} else {
			urlSchemes = ClassLoader.getSystemResource(schemesExt.toString());
			if (urlSchemes == null) {
				System.out.println("file not found:" + schemesExt);
				urlSchemes = SicpaSkin.class.getResource("SicpaSkin.colorschemes");
			}
		}
		return urlSchemes;
	}

	public static Color getPaddedButtonEnabledColor() {
		return SicpaLookAndFeelRessourceUtils.getColor(PADDED_BUTTON_ENABLED_COLOR,
				SicpaLookAndFeelCusto.getExtProperties(), SicpaColor.BLUE_MEDIUM);
	}

	public static Color getPaddedButtonDisabledColor() {
		return SicpaLookAndFeelRessourceUtils.getColor(PADDED_BUTTON_DISABLED_COLOR,
				SicpaLookAndFeelCusto.getExtProperties(), Color.GRAY);
	}

	public static BufferedImage getPowerByImage() {
		URL url = null;
		if (SicpaLookAndFeelCusto.getExtProperties() != null) {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(LOGO_POWER_BY);
			if (o != null) {
				String filename = o.toString();
				try {
					url = ClassLoader.getSystemResource(filename);
					if (url == null) {
						System.out.println("logo not found:");
						System.out.println("  logo file name in ext.ini:" + filename);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (url == null) {
			try {
				url = Class.forName("com.sicpa.standard.gui.screen.machine.AbstractMachineFrame").getResource(
						"powerBy.png");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		BufferedImage img;
		try {
			img = GraphicsUtilities.loadCompatibleImage(url);
		} catch (IOException e) {
			e.printStackTrace();
			img = GraphicsUtilities.createCompatibleTranslucentImage(1, 1);
		}
		return img;
	}

	public static BufferedImage getSicpaLogoPccFrame() {
		URL url = null;
		if (SicpaLookAndFeelCusto.getExtProperties() != null) {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(SICPA_LOGO_PCC_FRAME);
			if (o != null) {
				String filename = o.toString();
				try {
					url = ClassLoader.getSystemResource(filename);
					if (url == null) {
						System.out.println("logo not found:");
						System.out.println("  logo file name in ext.ini:" + filename);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		BufferedImage img = null;
		if (url != null) {
			try {
				img = GraphicsUtilities.loadCompatibleImage(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return img;
	}

	public static BufferedImage getSicpaLogoMasterPccClientFrame() {
		BufferedImage res = null;
		if (SicpaLookAndFeelCusto.getExtProperties() != null) {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(SICPA_LOGO_MASTER_PCC_CLIENT_FRAME);
			if (o != null) {
				String filename = o.toString();
				URL url = null;
				try {
					url = ClassLoader.getSystemResource(filename);
					if (url != null) {
						res = GraphicsUtilities.loadCompatibleImage(url);
					} else {
						System.out.println("logo not found:");
						System.out.println("  logo file name in ext.ini:" + filename);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (res == null) {
			try {
				URL url = Class.forName("com.sicpa.standard.gui.screen.machine.AbstractMachineFrame").getResource(
						"powerBy.png");
				if (url != null) {
					try {
						res = GraphicsUtilities.loadCompatibleImage(url);
					} catch (IOException e) {
						System.out.println("logo not found:");
						System.out.println("  logo file name in ext.ini:" + url.getFile());
						System.out.println("  logo url:" + url);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public static int getPaddedButtonBorderWidth() {
		int res = 5;

		Object o = SicpaLookAndFeelCusto.getExtProperties().get(PADDED_BUTTON_BORDER_WIDTH);
		if (o != null) {
			try {
				res = Integer.parseInt(o.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	public static int getPaddedButtonCornerArc() {
		return getNumber(PADDED_BUTTON_CORNER_ARC, 15).intValue();
	}

	private static Number getNumber(final String key, final Number defaultValue) {
		Object o = SicpaLookAndFeelCusto.getExtProperties().get(key);
		if (o != null) {
			try {
				return Double.parseDouble(o.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	public static Color getLockEffectBackground() {
		return SicpaLookAndFeelRessourceUtils.getColor(LOCK_EFFECT_BACKGROUND, SicpaLookAndFeelCusto.getExtProperties(),
				Color.GRAY);
	}

	public static Color getLockEffectErrorBackground() {
		return SicpaLookAndFeelRessourceUtils.getColor(ERROR_LOCK_EFFECT_BACKGROUND,
				SicpaLookAndFeelCusto.getExtProperties(), new Color(55, 25, 25));
	}

	public static Color getLockEffectTextOutterColor() {
		return SicpaLookAndFeelRessourceUtils.getColor(ERROR_LOCK_EFFECT_TEXT_OUTTER_COLOR,
				SicpaLookAndFeelCusto.getExtProperties(), Color.BLACK);
	}

	public static Color getLockEffectTextInnerColor() {
		return SicpaLookAndFeelRessourceUtils.getColor(ERROR_LOCK_EFFECT_TEXT_INNER_COLOR,
				SicpaLookAndFeelCusto.getExtProperties(), Color.RED);
	}

	public static float getLockEffectAlpha() {
		return getNumber(LOCK_EFFECT_ALPHA, 0.7f).floatValue();
	}

	public static int getLockEffectTextFontSize() {
		return getNumber(ERROR_LOCK_EFFECT_TEXT_FONT_SIZE, 35).intValue();
	}

	public static LockableLayerUIPainter getLockEffectErrorPainter() {
		LockableLayerUIPainter painter = null;
		try {
			Object o = SicpaLookAndFeelCusto.getExtProperties()
					.get(SicpaLookAndFeelConfig.ERROR_LOCK_EFFECT_PAINTER);
			if (o != null) {
				painter = (LockableLayerUIPainter) Class.forName(o.toString()).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return painter;
	}

	public static LockableLayerUIPainter getLockEffectPainter() {
		LockableLayerUIPainter painter = null;
		try {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(SicpaLookAndFeelConfig.LOCK_EFFECT_PAINTER);
			if (o != null) {
				painter = (LockableLayerUIPainter) Class.forName(o.toString()).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return painter;
	}

	public static Color getPCCFrameWarningCellbackground() {
		return SicpaLookAndFeelRessourceUtils.getColor(PCC_FRAME_WARNING_CELL_BACKGROUND,
				SicpaLookAndFeelCusto.getExtProperties(), SicpaColor.ORANGE.darker());
	}

	public static Color getPCCFrameWarningLastCellbackground() {
		return SicpaLookAndFeelRessourceUtils.getColor(PCC_FRAME_WARNING_LAST_CELL_BACKGROUND,
				SicpaLookAndFeelCusto.getExtProperties(), SicpaColor.ORANGE);
	}

	public static Color getPCCFrameWarningCellForeground() {
		return SicpaLookAndFeelRessourceUtils.getColor(PCC_FRAME_WARNING_CELL_FOREGROUND,
				SicpaLookAndFeelCusto.getExtProperties(), Color.BLACK);
	}

	public static Color getPCCFrameWarningLastCellForeground() {
		return SicpaLookAndFeelRessourceUtils.getColor(PCC_FRAME_WARNING_LAST_CELL_FOREGROUND,
				SicpaLookAndFeelCusto.getExtProperties(), Color.BLACK);
	}

	public static int getPCCFrameWarningAddAnimationDuration() {
		return getNumber(PCC_FRAME_WARNING_ADD_ANIM_DURATION, 300).intValue();
	}

	public static int getPCCFrameWarningRemoveAnimationDuration() {
		return getNumber(PCC_FRAME_WARNING_REMOVE_ANIM_DURATION, 200).intValue();
	}

	public static Object getPCCFrameWarningPainter() {
		Object painter = null;
		try {
			Object o = SicpaLookAndFeelCusto.getExtProperties().get(PCC_FRAME_WARNING_PAINTER);
			if (o instanceof String) {
				painter = Class.forName(o.toString()).newInstance();
			} else {
				return o;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return painter;
	}

	public static String getFontName() {
		Object res;

		res = SicpaLookAndFeelCusto.getExtProperties().get(SICPA_FONT);
		if (res == null) {
			res = "PT Sans";
		}
		return res.toString();
	}
}
