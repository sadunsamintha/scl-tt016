package com.sicpa.standard.gui.plaf;

import java.awt.Color;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceConstants;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.SubstanceConstants.MenuGutterFillKind;
import org.pushingpixels.substance.api.SubstanceConstants.TabContentPaneBorderKind;
import org.pushingpixels.substance.api.combo.WidestComboPopupPrototype;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.SubstanceDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.substance.api.painter.highlight.SubstanceHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.api.shaper.SubstanceButtonShaper;
import org.pushingpixels.substance.api.watermark.SubstanceWatermark;

import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardType;

public class SicpaSkin extends SubstanceSkin {
	public final static DecorationAreaType WORK_DECORATION_AREA_TYPE = new DecorationAreaType("WORK");
	public final static DecorationAreaType BUTTON_DECORATION_AREA_TYPE = new DecorationAreaType("BUTTON");
	public final static DecorationAreaType HEADER_FOOTER = new DecorationAreaType("HEADER_FOOTER");

	public final static ColorSchemeAssociationKind HIGHLIGHT = new ColorSchemeAssociationKind("HIGHLIGHT",
			ColorSchemeAssociationKind.FILL);
	public final static ColorSchemeAssociationKind TABLE_DECORATION_AREA_TYPE = new ColorSchemeAssociationKind("TABLE",
			ColorSchemeAssociationKind.BORDER);

	@Override
	public String getDisplayName() {
		return "SICPA Skin";
	}

	/**
	 * should only be used in the schemes editor
	 * 
	 * @param schemes
	 */
	public SicpaSkin(final Map<String, SubstanceColorScheme> schemes) {
		super();
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		installWaterMark();

		installShapper();

		installColorScheme(schemes);

		installPainter();

		installProperties();
	}

	public SicpaSkin() {
		this(null);
	}

	private void installShapper() {
		this.buttonShaper = new ClassicButtonShaper();
	}

	private void installColorScheme(final Map<String, SubstanceColorScheme> map) {
		SubstanceColorScheme activeScheme;
		SubstanceColorScheme enabledScheme;
		SubstanceColorScheme disabledScheme;
		SubstanceColorScheme pressedScheme;

		SubstanceColorScheme buttonActiveScheme;
		SubstanceColorScheme buttonEnabledScheme;
		SubstanceColorScheme buttonPressedScheme;

		SubstanceColorScheme tableScheme;
		SubstanceColorScheme tableSchemeDisabled;

		SubstanceColorScheme enabledBorderScheme;
		SubstanceColorScheme disabledBorderScheme;
		SubstanceColorScheme enabledTitleBorderScheme;

		SubstanceColorScheme selectedScheme;

		SubstanceColorScheme backgroundHeaderScheme;
		SubstanceColorScheme backgroundWorkScheme;
		SubstanceColorScheme backgroundDefaultScheme;

		SubstanceColorScheme separatorScheme;

		SubstanceColorScheme markScheme;

		SubstanceColorScheme highlightPainterScheme;

		if (map == null) {
			ColorSchemes schemes = SubstanceSkin.getColorSchemes(SicpaLookAndFeelConfig.getUrlColorSchemeFile());
			activeScheme = schemes.get("SicpaSkin Active");
			enabledScheme = schemes.get("SicpaSkin Enabled");
			disabledScheme = schemes.get("SicpaSkin Disabled");
			pressedScheme = schemes.get("SicpaSkin Pressed");

			buttonActiveScheme = schemes.get("SicpaSkin Button Active");
			buttonEnabledScheme = schemes.get("SicpaSkin Button Enabled");
			buttonPressedScheme = schemes.get("SicpaSkin Button Pressed");

			tableScheme = schemes.get("SicpaSkin Table");
			tableSchemeDisabled = schemes.get("SicpaSkin Table Disabled");

			enabledBorderScheme = schemes.get("SicpaSkin Enabled Border");
			disabledBorderScheme = schemes.get("SicpaSkin Disabled Border");
			enabledTitleBorderScheme = schemes.get("SicpaSkin Title Enabled Border");

			selectedScheme = schemes.get("SicpaSkin Selected");

			backgroundHeaderScheme = schemes.get("SicpaSkin Background Header");
			backgroundWorkScheme = schemes.get("SicpaSkin Background Work");
			backgroundDefaultScheme = schemes.get("SicpaSkin Background");

			separatorScheme = schemes.get("SicpaSkin Separator");

			markScheme = schemes.get("SicpaSkin Mark");

			highlightPainterScheme = schemes.get("SicpaSkin Highlight Painter");
		} else {// from editor
			activeScheme = map.get("SicpaSkin Active");
			enabledScheme = map.get("SicpaSkin Enabled");
			disabledScheme = map.get("SicpaSkin Disabled");
			pressedScheme = map.get("SicpaSkin Pressed");

			buttonActiveScheme = map.get("SicpaSkin Button Active");
			buttonEnabledScheme = map.get("SicpaSkin Button Enabled");
			buttonPressedScheme = map.get("SicpaSkin Button Pressed");

			tableScheme = map.get("SicpaSkin Table");
			tableSchemeDisabled = map.get("SicpaSkin Table Disabled");

			enabledBorderScheme = map.get("SicpaSkin Enabled Border");
			disabledBorderScheme = map.get("SicpaSkin Disabled Border");
			enabledTitleBorderScheme = map.get("SicpaSkin Title Enabled Border");

			selectedScheme = map.get("SicpaSkin Selected");

			backgroundHeaderScheme = map.get("SicpaSkin Background Header");
			backgroundWorkScheme = map.get("SicpaSkin Background Work");
			backgroundDefaultScheme = map.get("SicpaSkin Background");

			separatorScheme = map.get("SicpaSkin Separator");

			markScheme = map.get("SicpaSkin Mark");

			highlightPainterScheme = map.get("SicpaSkin Highlight Painter");
		}

		// --------------------GENERAL
		{
			SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(activeScheme,
					enabledScheme, disabledScheme);
			// borders
			defaultSchemeBundle.registerColorScheme(enabledBorderScheme, ColorSchemeAssociationKind.BORDER,
					ComponentState.DEFAULT);
			defaultSchemeBundle.registerColorScheme(disabledBorderScheme, ColorSchemeAssociationKind.BORDER,
					ComponentState.DISABLED_DEFAULT, ComponentState.DISABLED_SELECTED,
					ComponentState.DISABLED_UNSELECTED);

			defaultSchemeBundle.registerColorScheme(separatorScheme, ColorSchemeAssociationKind.SEPARATOR,
					ComponentState.DEFAULT);

			defaultSchemeBundle.registerColorScheme(tableScheme, TABLE_DECORATION_AREA_TYPE, ComponentState.DEFAULT);
			defaultSchemeBundle.registerColorScheme(tableSchemeDisabled, TABLE_DECORATION_AREA_TYPE,
					ComponentState.DISABLED_DEFAULT);

			defaultSchemeBundle.registerColorScheme(highlightPainterScheme, HIGHLIGHT, ComponentState.DEFAULT);

			// ------MARK
			defaultSchemeBundle.registerColorScheme(markScheme, ColorSchemeAssociationKind.MARK,
					ComponentState.ROLLOVER_SELECTED, ComponentState.SELECTED);
			// -----

			// states
			defaultSchemeBundle.registerColorScheme(pressedScheme, ComponentState.PRESSED_SELECTED,
					ComponentState.PRESSED_UNSELECTED);
			defaultSchemeBundle.registerColorScheme(selectedScheme, ComponentState.SELECTED,
					ComponentState.ROLLOVER_SELECTED);
			// use for text highlight
			defaultSchemeBundle.registerColorScheme(selectedScheme.saturate(0.5),
					ColorSchemeAssociationKind.TEXT_HIGHLIGHT, ComponentState.SELECTED,
					ComponentState.ROLLOVER_SELECTED);

			this.registerDecorationAreaSchemeBundle(defaultSchemeBundle, backgroundDefaultScheme,
					DecorationAreaType.NONE, DecorationAreaType.GENERAL);
		}
		// ------------------------FOR BUTTONS ONLY
		{
			SubstanceColorSchemeBundle buttonSchemeBundle = new SubstanceColorSchemeBundle(buttonActiveScheme,
					buttonEnabledScheme, disabledScheme);

			// borders
			buttonSchemeBundle.registerColorScheme(enabledBorderScheme, ColorSchemeAssociationKind.BORDER,
					ComponentState.DEFAULT);
			buttonSchemeBundle.registerColorScheme(disabledBorderScheme, ColorSchemeAssociationKind.BORDER,
					ComponentState.DISABLED_DEFAULT, ComponentState.DISABLED_SELECTED,
					ComponentState.DISABLED_UNSELECTED);
			// states
			buttonSchemeBundle.registerColorScheme(buttonPressedScheme, ComponentState.PRESSED_SELECTED,
					ComponentState.PRESSED_UNSELECTED);
			buttonSchemeBundle.registerColorScheme(selectedScheme, ComponentState.SELECTED,
					ComponentState.ROLLOVER_SELECTED);

			this.registerDecorationAreaSchemeBundle(buttonSchemeBundle, backgroundDefaultScheme,
					BUTTON_DECORATION_AREA_TYPE);
		}
		// --------title pane
		{
			SubstanceColorSchemeBundle titleSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, enabledScheme,
					disabledScheme);

			// borders
			titleSchemeBundle.registerColorScheme(enabledTitleBorderScheme, ColorSchemeAssociationKind.BORDER,
					ComponentState.DEFAULT);
			this.registerDecorationAreaSchemeBundle(titleSchemeBundle, backgroundHeaderScheme,
					DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE,
					DecorationAreaType.TOOLBAR);
		}

		// /--------------------------------------

		// HEADER
		// this.registerAsDecorationArea(backgroundHeaderScheme,
		// DecorationAreaType.HEADER);
		this.registerAsDecorationArea(backgroundWorkScheme, WORK_DECORATION_AREA_TYPE);
		this.registerAsDecorationArea(backgroundHeaderScheme, HEADER_FOOTER);

	}

	private void installPainter() {
		//
		// BrushedMetalDecorationPainter decorationPainter = new
		// BrushedMetalDecorationPainter();
		// decorationPainter.setPaintingSeparators(false);
		// decorationPainter.setTextureAlpha(0.1f);
		// this.decorationPainter = decorationPainter;

		this.decorationPainter = SicpaLookAndFeelConfig.getDecorationPainter();

		this.fillPainter = SicpaLookAndFeelConfig.getFillPainter();

		this.borderPainter = SicpaLookAndFeelConfig.getBorderPainter();

		this.highlightPainter = SicpaLookAndFeelConfig.getHighlightPainter();
	}

	private void installProperties() {
		UIManager.put(SubstanceLookAndFeel.COMBO_BOX_POPUP_FLYOUT_ORIENTATION, SwingConstants.SOUTH);
		UIManager.put(SubstanceLookAndFeel.FLAT_PROPERTY, false);
		UIManager.put(SubstanceLookAndFeel.SCROLL_PANE_BUTTONS_POLICY,
				SubstanceConstants.ScrollPaneButtonPolicyKind.OPPOSITE);

		UIManager.put(SubstanceLookAndFeel.CORNER_RADIUS, Float.valueOf(1));

		UIManager.put(SubstanceLookAndFeel.FOCUS_KIND, SubstanceConstants.FocusKind.NONE);

		UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, TabContentPaneBorderKind.SINGLE_PLACEMENT);

		UIManager.put(SubstanceLookAndFeel.MENU_GUTTER_FILL_KIND, MenuGutterFillKind.NONE);

		UIManager.put(SubstanceLookAndFeel.COMBO_POPUP_PROTOTYPE, new WidestComboPopupPrototype());

		UIManager.put(SubstanceLookAndFeel.WATERMARK_VISIBLE, Boolean.FALSE);

		UIManager.put(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1));

		UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);

		this.selectedTabFadeStart = 0.8;
		this.selectedTabFadeEnd = 0.99;

		SicpaLookAndFeelCusto.turnOnAutoScroll();
		// SicpaLookAndFeel.turnOnTabbedPanePreview();
		// SicpaLookAndFeel.turnOnScrollPreview();

		SicpaLookAndFeelCusto.turnOffLockIcon();

		SicpaLookAndFeelCusto.turnOffGhostingEffectOnButton();

		SicpaLookAndFeelCusto.setDefaultVirtualKeyboard(VirtualKeyboardType.KEYBOARD_QWERTY);

		UIManager.put("IMAGE_WITH_SHADOW", Boolean.TRUE);

	}

	private void installWaterMark() {
		/*
		 * this.watermark = new SubstanceImageWatermark(getClass().getResourceAsStream ("1024-iris.png"));
		 * ((SubstanceImageWatermark) this.watermark).setKind(ImageWatermarkKind.APP_ANCHOR); ((SubstanceImageWatermark)
		 * this.watermark).setOpacity(0.1f); // this.watermark = new SubstancePlanktonWatermark(); // this.watermark =
		 * new SubstanceMagneticFieldWatermark(); // this.watermark=new SubstanceCopperplateEngravingWatermark(); //
		 * this.watermark=new SubstanceWoodWatermark();
		 */

	}

	public void setWatermark(final SubstanceWatermark watermark) {
		this.watermark = watermark;
	}

	public void setDecorationPainter(final SubstanceDecorationPainter painter) {
		this.decorationPainter = painter;
	}

	public void setButtonShaper(final SubstanceButtonShaper shaper) {
		this.buttonShaper = shaper;
	}

	public void setBorderPainter(final SubstanceBorderPainter painter) {
		this.borderPainter = painter;
	}

	public void setFillPainter(final SubstanceFillPainter painter) {
		this.fillPainter = painter;
	}

	public void setHighlightPainter(final SubstanceHighlightPainter painter) {
		this.highlightPainter = painter;
	}

	public static void main(final String[] args) {
		// ava.awt.Color[r=0,g=44,b=78]02c4e
		// java.awt.Color[r=0,g=88,b=158]0589e

		Color c = new Color(0, 21, 36);
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();

		System.out.println(Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b));
	}
}
