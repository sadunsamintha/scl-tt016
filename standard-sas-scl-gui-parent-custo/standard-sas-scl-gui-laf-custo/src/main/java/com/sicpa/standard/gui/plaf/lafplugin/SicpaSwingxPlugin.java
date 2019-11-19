package com.sicpa.standard.gui.plaf.lafplugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.plaf.PainterUIResource;
import org.pushingpixels.lafplugin.LafComponentPlugin;
import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.fonts.FontSet;
import org.pushingpixels.substance.internal.painter.BackgroundPaintingUtils;
import org.pushingpixels.substance.internal.painter.DecorationPainterUtils;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceImageCreator;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;
import org.pushingpixels.substance.internal.utils.border.SubstanceBorder;

public class SicpaSwingxPlugin implements LafComponentPlugin {
	public SicpaSwingxPlugin() {
	}

	public Object[] getDefaults(final Object mSkin) {
		SubstanceSkin skin = (SubstanceSkin) mSkin;
		final SubstanceColorScheme mainActiveScheme = skin.getActiveColorScheme(DecorationAreaType.NONE);
		SubstanceColorScheme mainEnabledScheme = skin.getEnabledColorScheme(DecorationAreaType.NONE);
		SubstanceColorScheme mainDisabledScheme = skin.getDisabledColorScheme(DecorationAreaType.NONE);
		Color foregroundColor = SubstanceColorUtilities.getForegroundColor(mainEnabledScheme);
		Color backgroundColor = new ColorUIResource(mainActiveScheme.getBackgroundFillColor());
		Color disabledForegroundColor = SubstanceColorUtilities.getForegroundColor(mainDisabledScheme);

		SubstanceColorScheme highlightColorScheme = skin.getColorScheme((Component) null,
				ColorSchemeAssociationKind.HIGHLIGHT, ComponentState.SELECTED);
		if (highlightColorScheme == null) {
			highlightColorScheme = skin.getColorScheme(null, ComponentState.ROLLOVER_SELECTED);
		}

		Color selectionBackgroundColor = new ColorUIResource(highlightColorScheme.getSelectionBackgroundColor());

		Color selectionForegroundColor = new ColorUIResource(highlightColorScheme.getSelectionForegroundColor());

		FontSet fontSet = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null);

		Font controlFont = fontSet.getControlFont();

		Painter titlePanelPainter = new Painter() {
			public void paint(final Graphics2D g, final Object jxTitledPanel, final int width, final int height) {
				JComponent titledPanel = (JComponent) jxTitledPanel;
				Graphics2D g2d = (Graphics2D) g.create();
				// g2d.translate(10, 10);
				DecorationAreaType decorationType = SubstanceLookAndFeel.getDecorationType(titledPanel);
				if ((decorationType != null)
						&& (SubstanceCoreUtilities.getSkin(titledPanel).isRegisteredAsDecorationArea(decorationType))) {
					DecorationPainterUtils.paintDecorationBackground(g2d, titledPanel, false);
				} else {
					BackgroundPaintingUtils.fillAndWatermark(g2d, titledPanel, SubstanceCoreUtilities.getSkin(
							titledPanel).getBackgroundColorScheme(decorationType).getBackgroundFillColor(),
							new Rectangle(0, 0, titledPanel.getWidth(), titledPanel.getHeight()));
				}
				g2d.dispose();
			}
		};

		SubstanceColorScheme headerDecorationTheme = skin.getActiveColorScheme(DecorationAreaType.HEADER);
		Color titlePaneForeground = new ColorUIResource(SubstanceColorUtilities
				.getForegroundColor(headerDecorationTheme));

		final String UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE = "org.pushingpixels.substance.swingx.Substance";
		final String UI_CLASSNAME_PREFIX_SICPA = "com.sicpa.standard.gui.plaf.ui.Sicpa";

		Object[] defaults = new Object[] {

				JXDatePicker.uiClassID,
				UI_CLASSNAME_PREFIX_SICPA + "DatePickerUI",

				JXMonthView.uiClassID,
				UI_CLASSNAME_PREFIX_SICPA + "MonthViewUI",

				JXTaskPaneContainer.uiClassID,
				UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "TaskPaneContainerUI",

				JXTaskPane.uiClassID,
				UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "TaskPaneUI",

				JXStatusBar.uiClassID,
				UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "StatusBarUI",

				JXHeader.uiClassID,
				UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "HeaderUI",

				JXLoginPane.uiClassID,
				UI_CLASSNAME_PREFIX_SICPA + "LoginPaneUI",

				JXTipOfTheDay.uiClassID,
				UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "TipOfTheDayUI",

				JXTitledPanel.uiClassID,
				UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "TitledPanelUI",

				JXErrorPane.uiClassID,
				UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "ErrorPaneUI",

				JXHyperlink.uiClassID,
				UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "HyperlinkUI",

				"ColumnHeaderRenderer.upIcon",
				new UIDefaults.LazyValue() {
					@Override
					public Object createValue(final UIDefaults table) {
						return new IconUIResource(SubstanceImageCreator.getArrowIcon(SubstanceSizeUtils
								.getControlFontSize(), SwingConstants.NORTH, mainActiveScheme));
					}
				},

				"ColumnHeaderRenderer.downIcon",
				new UIDefaults.LazyValue() {
					@Override
					public Object createValue(final UIDefaults table) {
						return new IconUIResource(SubstanceImageCreator.getArrowIcon(SubstanceSizeUtils
								.getControlFontSize(), SwingConstants.SOUTH, mainActiveScheme));
					}
				},

				"JXDatePicker.arrowIcon",
				new UIDefaults.LazyValue() {
					@Override
					public Object createValue(final UIDefaults table) {
						return new IconUIResource(SubstanceImageCreator.getArrowIcon(SubstanceSizeUtils
								.getControlFontSize(), SwingConstants.SOUTH, mainActiveScheme));
					}
				},

				"JXLoginPane.bannerFont",
				new FontUIResource(controlFont.deriveFont(3.0f * controlFont.getSize())),

				"JXLoginPane.bannerForeground",
				new ColorUIResource(SubstanceColorUtilities.getNegativeColor(foregroundColor)),

				"JXLoginPane.bannerLightBackground",
				new ColorUIResource(mainActiveScheme.isDark() ? mainActiveScheme.getUltraLightColor()
						: mainActiveScheme.getLightColor()),

				"JXLoginPane.bannerDarkBackground",
				new ColorUIResource(mainActiveScheme.isDark() ? mainActiveScheme.getLightColor() : mainActiveScheme
						.getMidColor()),

				"JXMonthView.background", backgroundColor,

				"JXMonthView.foreground", foregroundColor,

				"JXMonthView.monthStringBackground", backgroundColor,

				"JXMonthView.monthStringForeground", foregroundColor,

				"JXMonthView.daysOfTheWeekForeground", foregroundColor,

				"JXMonthView.weekOfTheYearForeground", foregroundColor,

				"JXMonthView.unselectableDayForeground", new ColorUIResource(Color.RED),

				"JXMonthView.selectedBackground", selectionBackgroundColor,

				"JXMonthView.selectedForeground", selectionForegroundColor,

				"JXMonthView.flaggedDayForeground", new ColorUIResource(Color.RED),

				"JXMonthView.leadingDayForeground", disabledForegroundColor,

				"JXMonthView.trailingDayForeground", disabledForegroundColor,

				"TaskPane.titleForeground", foregroundColor,

				"TaskPane.titleOver", foregroundColor,

				"TaskPane.specialTitleForeground", foregroundColor,

				"TaskPane.specialTitleOver", foregroundColor,

				"TaskPaneContainer.background", backgroundColor,
				// new ColorUIResource(new SubstanceComplexTheme("dummy",
				// activeTitleTheme.getKind(), null, activeTitleTheme,
				// null, null).getBackgroundColor()),

				"TaskPaneContainer.backgroundPainter", null,

				"TaskPane.background", backgroundColor,

				"TaskPane.foreground", foregroundColor,

				"TaskPane.font", new FontUIResource(controlFont.deriveFont(Font.BOLD)),

				"TipOfTheDay.background", backgroundColor.darker(),

				"TipOfTheDay.foreground", foregroundColor,

				"TipOfTheDay.font", new FontUIResource(controlFont),

				"TipOfTheDay.tipFont", new FontUIResource(controlFont.deriveFont(4.0f + controlFont.getSize())),

				"TipOfTheDay.border", new SubstanceBorder(),

				"TitledBorder.font", new FontUIResource(controlFont),

				"TitledBorder.titleColor", foregroundColor,

				"JXTitledPanel.titleForeground", titlePaneForeground,

				"JXTitledPanel.titleFont", new FontUIResource(controlFont.deriveFont(Font.BOLD)),

				"JXTitledPanel.titlePainter", new PainterUIResource(titlePanelPainter),

				"JXHeader.titleForeground", titlePaneForeground,

				"JXHeader.descriptionForeground", titlePaneForeground, };

		return defaults;
	}

	public void initialize() {
	}

	public void uninitialize() {
	}
}
