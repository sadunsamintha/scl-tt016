package com.sicpa.standard.gui.plaf;

import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.JTextComponent;

import org.pushingpixels.lafwidget.LafWidget;
import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.animation.AnimationFacet;
import org.pushingpixels.lafwidget.preview.DefaultPreviewPainter;
import org.pushingpixels.lafwidget.tabbed.DefaultTabPreviewPainter;
import org.pushingpixels.lafwidget.utils.LafConstants.TabOverviewKind;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceConstants.SubstanceWidgetType;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.fonts.FontPolicy;
import org.pushingpixels.substance.api.fonts.FontSet;
import org.pushingpixels.trident.TridentConfig;
import org.pushingpixels.trident.TridentConfig.PulseSource;
import org.pushingpixels.trident.UIToolkitHandler;
import org.pushingpixels.trident.swing.SwingToolkitHandler;

import com.jidesoft.swing.AutoCompletion;
import com.sicpa.standard.gui.components.renderers.SicpaListCellRenderer;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardType;
import com.sicpa.standard.gui.plaf.ui.SicpaButtonUI;
import com.sicpa.standard.gui.plaf.ui.SicpaOptionPaneUI;
import com.sicpa.standard.gui.plaf.ui.SicpaPasswordFieldUI;
import com.sicpa.standard.gui.plaf.ui.SicpaSpinnerUI;
import com.sicpa.standard.gui.plaf.ui.utils.ButtonVisualStateTracker;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class SicpaLookAndFeelCusto {

	private static final String RESSOURCE_EXT = "com.sicpa.standard.gui.plaf.SicpaLookAndFeel.ext";

	private static final String IMAGE_WITH_SHADOW = "IMAGE_WITH_SHADOW";

	private static Properties extProperties;

	public SubstanceLookAndFeel laf;

	public SicpaLookAndFeelCusto() {
		this(new SicpaSkin());
	}

	public SicpaLookAndFeelCusto(final SubstanceSkin skin) {
		this.laf = new SubstanceLookAndFeel(skin) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDescription() {
				return "Sicpa Look and Feel";
			}

			@Override
			protected void initClassDefaults(final UIDefaults table) {
				super.initClassDefaults(table);

				final String UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE = "org.pushingpixels.substance.internal.ui.Substance";
				final String UI_CLASSNAME_PREFIX_SICPA = "com.sicpa.standard.gui.plaf.ui.Sicpa";

				Object[] uiDefaults = {

				"ButtonUI", UI_CLASSNAME_PREFIX_SICPA + "ButtonUI",

				"CheckBoxUI", UI_CLASSNAME_PREFIX_SICPA + "CheckBoxUI",

				"ComboBoxUI", UI_CLASSNAME_PREFIX_SICPA + "ComboBoxUI",

				"CheckBoxMenuItemUI", UI_CLASSNAME_PREFIX_SICPA + "CheckBoxMenuItemUI",

				"DesktopIconUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "DesktopIconUI",

				"DesktopPaneUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "DesktopPaneUI",

				"EditorPaneUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "EditorPaneUI",

				"FileChooserUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "FileChooserUI",

				"FormattedTextFieldUI", UI_CLASSNAME_PREFIX_SICPA + "FormattedTextFieldUI",

				"InternalFrameUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "InternalFrameUI",

				"LabelUI", UI_CLASSNAME_PREFIX_SICPA + "LabelUI",

				"ListUI", UI_CLASSNAME_PREFIX_SICPA + "ListUI",

				"MenuUI", UI_CLASSNAME_PREFIX_SICPA + "MenuUI",

				"MenuBarUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "MenuBarUI",

				"MenuItemUI", UI_CLASSNAME_PREFIX_SICPA + "MenuItemUI",

				"OptionPaneUI", UI_CLASSNAME_PREFIX_SICPA + "OptionPaneUI",

				"PanelUI", UI_CLASSNAME_PREFIX_SICPA + "PanelUI",

				"PasswordFieldUI", UI_CLASSNAME_PREFIX_SICPA + "PasswordFieldUI",

				"PopupMenuUI", UI_CLASSNAME_PREFIX_SICPA + "PopupMenuUI",

				"PopupMenuSeparatorUI", UI_CLASSNAME_PREFIX_SICPA + "PopupMenuSeparatorUI",

				"ProgressBarUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "ProgressBarUI",

				"RadioButtonUI", UI_CLASSNAME_PREFIX_SICPA + "RadioButtonUI",

				"RadioButtonMenuItemUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "RadioButtonMenuItemUI",

				"RootPaneUI", UI_CLASSNAME_PREFIX_SICPA + "RootPaneUI",

				"ScrollBarUI", UI_CLASSNAME_PREFIX_SICPA + "ScrollBarUI",

				"ScrollPaneUI", UI_CLASSNAME_PREFIX_SICPA + "ScrollPaneUI",

				"SeparatorUI", UI_CLASSNAME_PREFIX_SICPA + "SeparatorUI",

				"SliderUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "SliderUI",

				"SpinnerUI", UI_CLASSNAME_PREFIX_SICPA + "SpinnerUI",

				"SplitPaneUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "SplitPaneUI",

				"TabbedPaneUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "TabbedPaneUI",

				"TableUI", UI_CLASSNAME_PREFIX_SICPA + "TableUI",

				"TableHeaderUI", UI_CLASSNAME_PREFIX_SICPA + "TableHeaderUI",

				"TextAreaUI", UI_CLASSNAME_PREFIX_SICPA + "TextAreaUI",

				"TextFieldUI", UI_CLASSNAME_PREFIX_SICPA + "TextFieldUI",

				"TextPaneUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "TextPaneUI",

				"ToggleButtonUI", UI_CLASSNAME_PREFIX_SICPA + "ToggleButtonUI",

				"ToolBarUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "ToolBarUI",

				"ToolBarSeparatorUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "ToolBarSeparatorUI",

				"ToolTipUI", UI_CLASSNAME_PREFIX_DEFAULT_SUBSTANCE + "ToolTipUI",

				"TreeUI", UI_CLASSNAME_PREFIX_SICPA + "TreeUI",

				};
				table.putDefaults(uiDefaults);

				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						UIManager.put("List.cellRenderer", new SicpaListCellRenderer());
					}
				});
				UIManager.put("Spinner.editorAlignment", JTextField.CENTER);
			}
		};
	}

	// ------------------------ Tab preview
	/**
	 * Enable the tab preview widget
	 * 
	 * @param tabOverviewKind
	 *            the kind of overview wanted
	 */
	public static void turnOnTabbedPanePreview(final TabOverviewKind tabOverviewKind) {
		UIManager.put(LafWidget.TABBED_PANE_PREVIEW_PAINTER, new DefaultTabPreviewPainter() {
			@Override
			public TabOverviewKind getOverviewKind(final JTabbedPane tabPane) {
				return tabOverviewKind;
			}
		});
	}

	/**
	 * Enable the tab preview widget, Gridlayout by default
	 */
	public static void turnOnTabbedPanePreview() {
		turnOnTabbedPanePreview(TabOverviewKind.GRID);
	}

	/**
	 * Disable the tab preview widget
	 */
	public static void turnOffTabbedPanePreview() {
		UIManager.put(LafWidget.TABBED_PANE_PREVIEW_PAINTER, null);
	}

	/**
	 * Enable the tab preview widget
	 * 
	 * @param tab
	 *            The specific JTabbedPane
	 */
	public static void turnOnTabbedPanePreview(final JTabbedPane tab) {
		turnOnTabbedPanePreview(tab, TabOverviewKind.GRID);
	}

	/**
	 * Enable the tab preview widget
	 * 
	 * @param tabOverviewKind
	 *            the kind of overview wanted
	 * @param tab
	 *            The specific JTabbedPane
	 */
	public static void turnOnTabbedPanePreview(final JTabbedPane tab, final TabOverviewKind tabOverviewKind) {
		tab.putClientProperty(LafWidget.TABBED_PANE_PREVIEW_PAINTER, new DefaultTabPreviewPainter() {
			@Override
			public TabOverviewKind getOverviewKind(final JTabbedPane tabPane) {
				return tabOverviewKind;
			}
		});
	}

	/**
	 * Disable the tab preview widget
	 * 
	 * @param tab
	 *            The specific JTabbedPane
	 */
	public static void turnOffTabbedPanePreview(final JTabbedPane tab) {
		tab.putClientProperty(LafWidget.TABBED_PANE_PREVIEW_PAINTER, null);
	}

	// --------------------------- scroll preview
	/**
	 * Enable the scroll preview widget
	 */
	public static void turnOnScrollPreview() {
		UIManager.put(LafWidget.COMPONENT_PREVIEW_PAINTER, new DefaultPreviewPainter());
	}

	/**
	 * Disable the scroll preview widget
	 */
	public static void turnOffScrollPreview() {
		UIManager.put(LafWidget.COMPONENT_PREVIEW_PAINTER, null);
	}

	/**
	 * Enable the scroll preview widget on a specific JScrollPane
	 * 
	 * @param scroll
	 *            the specific JScrollPane
	 */
	public static void turnOnScrollPreview(final JScrollPane scroll) {
		scroll.putClientProperty(LafWidget.COMPONENT_PREVIEW_PAINTER, new DefaultPreviewPainter());
	}

	/**
	 * Disable the scroll preview widget on a specific JScrollPane
	 * 
	 * @param scroll
	 *            the specific JScrollPane
	 */
	public static void turnOffScrollPreview(final JScrollPane scroll) {
		scroll.putClientProperty(LafWidget.COMPONENT_PREVIEW_PAINTER, null);
	}

	// ----------------------auto scroll
	/**
	 * Enable the auto scroll on all JScrollPane
	 */
	public static void turnOnAutoScroll() {
		UIManager.put(LafWidget.AUTO_SCROLL, Boolean.TRUE);
	}

	/**
	 * Disable the auto scroll on all JScrollPane
	 */
	public static void turnOffAutoScroll() {
		UIManager.put(LafWidget.AUTO_SCROLL, Boolean.TRUE);
	}

	/**
	 * Enable the auto scroll on a specific JScrollPane
	 * 
	 * @param scroll
	 *            the Specific JScrollPane
	 */
	public static void turnOnAutoScroll(final JScrollPane scroll) {
		scroll.putClientProperty(LafWidget.AUTO_SCROLL, Boolean.TRUE);
	}

	/**
	 * Disable the auto scroll on a specific JScrollPane
	 * 
	 * @param scroll
	 *            the Specific JScrollPane
	 */
	public static void turnOffAutoScroll(final JScrollPane scroll) {
		scroll.putClientProperty(LafWidget.AUTO_SCROLL, Boolean.TRUE);
	}

	// ---------------------- layout transition
	// private static Map<JPanel, ContainerListener> mapListeners = new
	// Hashtable<JPanel, ContainerListener>();

	// public static void turnOnLayoutTransition(final JPanel comp)
	// {
	// TransitionLayoutManager.getInstance().track(comp, false, true);
	// ContainerListener lis = (new ContainerListener()
	// {
	// @Override
	// public void componentRemoved(final ContainerEvent e)
	// {
	// }
	//
	// @Override
	// public void componentAdded(final ContainerEvent e)
	// {
	// comp.revalidate();
	// }
	// });
	// comp.addContainerListener(lis);
	// mapListeners.put(comp, lis);
	// }
	//
	// public static void turnOffLayoutTransition(final JPanel comp)
	// {
	// TransitionLayoutManager.getInstance().untrack(comp);
	// mapListeners.remove(comp);
	// }

	// --------------------Select text on focus
	/**
	 * Enable the auto selecting text on focus feature on all JTextComponent
	 */
	public static void turnOnSelectTextOnFocus() {
		UIManager.put(LafWidget.TEXT_SELECT_ON_FOCUS, Boolean.TRUE);
	}

	/**
	 * Enable the auto selecting text on focus feature on a specific JTextComponent
	 * 
	 * @param textComp
	 *            the specific JTextComponent
	 */
	public static void turnOnSelectTextOnFocus(final JTextComponent textComp) {
		textComp.putClientProperty(LafWidget.TEXT_SELECT_ON_FOCUS, Boolean.TRUE);
	}

	/**
	 * Disable the auto selecting text on focus feature on all JTextComponent
	 */
	public static void turnOffSelectTextOnFocus() {
		UIManager.put(LafWidget.TEXT_SELECT_ON_FOCUS, Boolean.FALSE);
	}

	/**
	 * Disable the auto selecting text on focus feature on a specific JTextComponent
	 * 
	 * @param textComp
	 *            the specific JTextComponent
	 */
	public static void turnOffSelectTextOnFocus(final JTextComponent textComp) {
		textComp.putClientProperty(LafWidget.TEXT_SELECT_ON_FOCUS, Boolean.FALSE);
	}

	// ----------------- Edit edit menu on text comp
	/**
	 * Enable the creation of a right click edit popup menu on all JTextComponent
	 */
	public static void turnOnEditMenuOnTextComponent() {
		UIManager.put(LafWidget.TEXT_EDIT_CONTEXT_MENU, Boolean.TRUE);
	}

	/**
	 * Disable the creation of a right click edit popup menu on all JTextComponent
	 */
	public static void turnOffEditMenuOnTextComponent() {
		UIManager.put(LafWidget.TEXT_EDIT_CONTEXT_MENU, Boolean.FALSE);
	}

	/**
	 * Enable the creation of a right click edit popup menu on a specific JTextComponent
	 * 
	 * @param textComp
	 *            the specific JTextComponent
	 */
	public static void turnOnEditMenuOnTextComponent(final JTextComponent textComp) {
		textComp.putClientProperty(LafWidget.TEXT_EDIT_CONTEXT_MENU, Boolean.TRUE);
	}

	/**
	 * Disable the creation of a right click edit popup menu on a specific JTextComponent
	 * 
	 * @param textComp
	 *            the specific JTextComponent
	 */
	public static void turnOffEditMenuOnTextComponent(final JTextComponent textComp) {
		textComp.putClientProperty(LafWidget.TEXT_EDIT_CONTEXT_MENU, Boolean.FALSE);
	}

	// ---------------------- watermark
	/**
	 * Make a specific component opaque to the watermark
	 * 
	 * @param comp
	 *            the specific JComponent
	 */
	public static void turnOffWatermark(final JComponent comp) {
		comp.putClientProperty(SubstanceLookAndFeel.WATERMARK_VISIBLE, Boolean.FALSE);
	}

	/**
	 * Make a specific component non opaque to the watermark
	 * 
	 * @param comp
	 *            the specific JComponent
	 */
	public static void turnOnWatermark(final JComponent comp) {
		comp.putClientProperty(SubstanceLookAndFeel.WATERMARK_VISIBLE, Boolean.TRUE);
	}

	// ----------------------decoration area
	/**
	 * Flag a component to be seen as a Header/Footer so its background is painted with the appropriate color scheme
	 * 
	 * @param comp
	 *            the JComponent to flag
	 */
	public static void flagAsHeaderOrFooter(final JComponent comp) {
		SubstanceLookAndFeel.setDecorationType(comp, SicpaSkin.HEADER_FOOTER);
	}

	public static void flagAsButton(final JComponent comp) {
		SubstanceLookAndFeel.setDecorationType(comp, SicpaSkin.BUTTON_DECORATION_AREA_TYPE);
	}

	/**
	 * Flag a component to be seen as a Work so its background is painted with the appropriate color scheme
	 * 
	 * @param comp
	 *            the JComponent to flag
	 */
	public static void flagAsWorkArea(final JComponent comp) {
		SubstanceLookAndFeel.setDecorationType(comp, SicpaSkin.WORK_DECORATION_AREA_TYPE);
	}

	/**
	 * Flag a component , so it background get painted with the default background,<br>
	 * and then the color background can be set
	 * 
	 * @param comp
	 */
	public static void flagAsDefaultArea(final JComponent comp) {
		SubstanceLookAndFeel.setDecorationType(comp, DecorationAreaType.NONE);
	}

	// ---------------------memory widget
	/**
	 * Show the memory widget on specific frame title bar
	 * 
	 * @param frame
	 *            the JFrame where the memory widget will be located at
	 */
	public static void turnOnMemoryManagementWidget(final JFrame frame) {
		SubstanceLookAndFeel.setWidgetVisible(frame.getRootPane(), true, SubstanceWidgetType.TITLE_PANE_HEAP_STATUS);
	}

	/**
	 * Hide the memory widget on specific frame title bar
	 * 
	 * @param frame
	 *            the JFrame where from the memory widget will be removed
	 */
	public static void turnOffMemoryManagementWidget(final JFrame frame) {
		SubstanceLookAndFeel.setWidgetVisible(frame.getRootPane(), false, SubstanceWidgetType.TITLE_PANE_HEAP_STATUS);
	}

	// ---------------- menu search
	/**
	 * Show the quick search menu widget on the Jframe menu bar
	 * 
	 * @param frame
	 *            the JFrame where the quick search menu will be located at
	 */
	public static void turnOnMenuQuickSearchWidget(final JFrame frame) {
		SubstanceLookAndFeel.setWidgetVisible(frame.getRootPane(), true, SubstanceWidgetType.MENU_SEARCH);
	}

	/**
	 * Hide the quick search menu widget on the Jframe menu bar
	 * 
	 * @param frame
	 *            the JFrame where from the quick search menu will removed
	 */
	public static void turnOffMenuQuickSearchWidget(final JFrame frame) {
		SubstanceLookAndFeel.setWidgetVisible(frame.getRootPane(), false, SubstanceWidgetType.MENU_SEARCH);
	}

	// ------------- font
	public static enum EFontComponent {
		List("List.font"), TableHeader("TableHeader.font"), Panel("Panel.font"), TextArea("TextArea.font"), ToggleButton(
				"ToggleButton.font"), ComboBox("ComboBox.font"), ScrollPane("ScrollPane.font"), Spinner("Spinner.font"), RadioButtonMenuItem(
				"RadioButtonMenuItem.font"), Slider("Slider.font"), EditorPane("EditorPane"), OptionPane(
				"OptionPane.font"), ToolBar("ToolBar.font"), Tree("Tree.font"), CheckBoxMenuItem(
				"CheckBoxMenuItem.font"), TitledBorder("TitledBorder.font"), Table("Table.font"), MenuBar("Table.font"), PopupMenu(
				"PopupMenu.font"), DesktopIcon("DesktopIcon.font"), Label("Label.font"), MenuItem("MenuItem.font"), TextField(
				"TextField.font"), TextPane("TextPane.font"), CheckBox("CheckBox.font"), ProgressBar("ProgressBar.font"), FormattedTextField(
				"FormattedTextField.font"), ColorChooser("ColorChooser.font"), Menu("Menu.font"), PasswordField(
				"PasswordField.font"), Viewport("Viewport.font"), TabbedPane("TabbedPane.font"), RadioButton(
				"RadioButton.font"), ToolTip("ToolTip.font"), Button("Button.font"), XDataPicker("XDataPicker.font"), SelectionBackground(
				"selectionBackground");

		private final String value;

		private EFontComponent(final String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	};

	/**
	 * Set a Font to a specific kind of components
	 * 
	 * @param efont
	 *            The type of component that will get its font set
	 * @param font
	 *            The Font to be set
	 */
	public static void setFont(final EFontComponent efont, final Font font) {
		UIManager.put(efont.value, font);
	}

	/**
	 * Set a Font to a specific kind of components
	 * 
	 * @param efont
	 *            The type of component that will get its font set
	 * @param fontName
	 *            The Font name
	 * @param style
	 *            The font style
	 * @param size
	 *            The font size
	 * 
	 */
	public static void setFont(final EFontComponent efont, final String fontName, final int style, final int size) {
		setFont(efont, new Font(fontName, style, size));
	}

	/**
	 * change the font throughout the whole application
	 * 
	 * @param font
	 *            the font used in the application
	 */
	public static void setFont(final Font font) {
		if (SubstanceLookAndFeel.getCurrentSkin() == null) {
			return;
		}
		Runnable r = new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setFontPolicy(null);

				instancefontSet.setDefaultFont(font);

				FontPolicy newFontPolicy = new FontPolicy() {
					public FontSet getFontSet(final String lafName, final UIDefaults table) {
						return instancefontSet;
					}
				};
				SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
			}
		};

		ThreadUtils.invokeLater(r);

	}

	/**
	 * change the font size throughout the whole application
	 * 
	 * @param size
	 *            the font size
	 */
	public static void setFontSize(final int size) {
		setFont(SicpaFont.getFont(size));
	}

	// ----------------- combo box
	/**
	 * Add the auto-completion feature to a combobox
	 * 
	 * @param combo
	 *            the JCombobox where to apply the auto-completion
	 */
	public static void turnOnAutoCompletion(final JComboBox combo) {
		combo.setEditable(true);
		AutoCompletion ac = new AutoCompletion(combo);
		ac.setStrict(true);
	}

	private static SicpaFontSet instancefontSet = new SicpaFontSet();

	private static class SicpaFontSet implements FontSet {

		private FontUIResource controlFontRessource;
		private FontUIResource menuFontRessource;
		private FontUIResource messageFontRessource;
		private FontUIResource smallFontRessource;
		private FontUIResource titleFontRessource;
		private FontUIResource windowTitleFontRessource;

		public SicpaFontSet() {
			Font defaultFont = SicpaFont.getFont(10);
			setDefaultFont(defaultFont);
		}

		public void setDefaultFont(final Font defaultFont) {
			FontUIResource defaultFontRessource = new FontUIResource(defaultFont);

			this.controlFontRessource = defaultFontRessource;
			this.menuFontRessource = defaultFontRessource;
			this.messageFontRessource = defaultFontRessource;
			this.smallFontRessource = defaultFontRessource;
			this.titleFontRessource = defaultFontRessource;
			this.windowTitleFontRessource = defaultFontRessource;

		}

		public SicpaFontSet(final Font font) {
			setDefaultFont(font);
		}

		@Override
		public FontUIResource getControlFont() {
			return this.controlFontRessource;
		}

		@Override
		public FontUIResource getMenuFont() {
			return this.menuFontRessource;
		}

		@Override
		public FontUIResource getMessageFont() {
			return this.messageFontRessource;
		}

		@Override
		public FontUIResource getSmallFont() {
			return this.smallFontRessource;
		}

		@Override
		public FontUIResource getTitleFont() {
			return this.titleFontRessource;
		}

		@Override
		public FontUIResource getWindowTitleFont() {
			return this.windowTitleFontRessource;
		}
	}

	/**
	 * Set the font used for all standard components.
	 * 
	 * @param the
	 *            font used for all standard components.
	 */
	public static void setControlFont(final Font controlFont) {
		Runnable r = new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setFontPolicy(null);
				instancefontSet.controlFontRessource = new FontUIResource(controlFont);

				FontPolicy newFontPolicy = new FontPolicy() {
					public FontSet getFontSet(final String lafName, final UIDefaults table) {
						return instancefontSet;
					}
				};
				SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
			}
		};
		ThreadUtils.invokeLater(r);
	}

	/**
	 * Set the font used for the menu.
	 * 
	 * @param the
	 *            font used for the menu.
	 */
	public static void setMenuFont(final Font menuFont) {
		Runnable r = new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setFontPolicy(null);
				instancefontSet.menuFontRessource = new FontUIResource(menuFont);

				FontPolicy newFontPolicy = new FontPolicy() {
					public FontSet getFontSet(final String lafName, final UIDefaults table) {
						return instancefontSet;
					}
				};
				SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
			}
		};
		ThreadUtils.invokeLater(r);
	}

	/**
	 * Set the font used for message dialogs.
	 * 
	 * @param the
	 *            font used for message dialogs.
	 */
	public static void setMessageFont(final Font messageFont) {
		Runnable r = new Runnable() {
			public void run() {
				// SubstanceLookAndFeel.setFontPolicy(null);
				instancefontSet.messageFontRessource = new FontUIResource(messageFont);

				FontPolicy newFontPolicy = new FontPolicy() {
					public FontSet getFontSet(final String lafName, final UIDefaults table) {
						return instancefontSet;
					}
				};
				SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
			}
		};
		ThreadUtils.invokeLater(r);
	}

	/**
	 * Set the font used for tool tips.
	 * 
	 * @param the
	 *            font used for tool tips.
	 */
	public static void setSmallFont(final Font smallFont) {
		Runnable r = new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setFontPolicy(null);
				instancefontSet.smallFontRessource = new FontUIResource(smallFont);

				FontPolicy newFontPolicy = new FontPolicy() {
					public FontSet getFontSet(final String lafName, final UIDefaults table) {
						return instancefontSet;
					}
				};
				SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
			}
		};
		ThreadUtils.invokeLater(r);
	}

	/**
	 * Set the font used for the title label in TitledBorders. This font is also used by JGoodies Forms titles, and
	 * titled separators.
	 * 
	 * @param the
	 *            font used for TitledBorder titles.
	 */
	public static void setTitleFont(final Font titleFont) {
		Runnable r = new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setFontPolicy(null);
				instancefontSet.titleFontRessource = new FontUIResource(titleFont);

				FontPolicy newFontPolicy = new FontPolicy() {
					public FontSet getFontSet(final String lafName, final UIDefaults table) {
						return instancefontSet;
					}
				};
				SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
			}
		};
		ThreadUtils.invokeLater(r);
	}

	/**
	 * set the font used for internal frame titles.
	 * 
	 * @param the
	 *            font used for internal frame titles
	 * */
	public static void setWindowTitleFont(final Font windowTitleFont) {
		Runnable r = new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setFontPolicy(null);
				instancefontSet.windowTitleFontRessource = new FontUIResource(windowTitleFont);

				FontPolicy newFontPolicy = new FontPolicy() {
					public FontSet getFontSet(final String lafName, final UIDefaults table) {
						return instancefontSet;
					}
				};
				SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
			}
		};
		ThreadUtils.invokeLater(r);
	};

	/**
	 * specify the colorization amount applied to the background and foreground of the current color scheme and the
	 * specific control. By default, when the application does not use any custom colors, all the controls are painted
	 * with the colors of the current theme / skin. The colors coming from the look-and-feel implement the marker
	 * UIResource interface which allows the UI delegates to differentiate between application-specific colors which are
	 * not changed, and the LAF-provide colors that are changed on LAF switch.
	 * 
	 * This new client property installs the "smart colorization" mode which uses the colors of the current color scheme
	 * and the custom background / foreground colors (when installed by application) to colorize the relevant portions
	 * of the control. For example, on checkbox the custom background color will be used to colorize the check box
	 * itself, while the custom foreground color will be applied to the check box text and the check mark.
	 * 
	 * The value of this property specifies the actual colorization amount. Value of 0.0 results in no colorization at
	 * all. Values closer to 1.0 result in controls being colorized in almost full amount of the custom colors. Note
	 * that in order to maintain the gradients (fill, border, etc), even value of 1.0 does not result in full custom
	 * color being applied to the relevant visuals of the control.
	 * 
	 * @param comp
	 * @param factor
	 *            , range from 0..1
	 */
	public static void setColorizationFactorOnComponent(final JComponent comp, final double factor) {
		comp.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(factor));
		comp.repaint();
	}

	public static class SicpaPulseSource implements PulseSource {
		public static int DELAY = 20;

		public SicpaPulseSource() {
			String o = System.getProperty("sicpaLaF.SicpaPulseSource.delay");
			if (o != null) {
				DELAY = Integer.parseInt(o);
			}
		}

		@Override
		public void waitUntilNextPulse() {
			ThreadUtils.sleepQuietly(DELAY);
		}
	}

	public static void install() {

		if (!(TridentConfig.getInstance().getPulseSource() instanceof SicpaPulseSource)) {
			TridentConfig.getInstance().setPulseSource(new SicpaPulseSource());
		}

		// override default behavior
		// even if the component is not displayable the animation will start.
		for (UIToolkitHandler h : TridentConfig.getInstance().getUIToolkitHandlers()) {
			TridentConfig.getInstance().removeUIToolkitHandler(h);
		}

		TridentConfig.getInstance().addUIToolkitHandler(new SwingToolkitHandler() {
			@Override
			public boolean isInReadyState(final Object mainTimelineObject) {
				return true;
			}
		});

		turnOnVirtualKeyboard();

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					getExtProperties();
					UIManager.setLookAndFeel(new SicpaLookAndFeelCusto().laf);
					initExt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				SicpaLookAndFeelCusto.setFont(SicpaFont.getFont(18));
				SicpaLookAndFeelCusto.setMessageFont(SicpaFont.getFont(25));

				ToolTipManager.sharedInstance().setDismissDelay(50000);
			}
		});

		// ---show tweak frame if prop is true
		String o = System.getProperty("sicpaLaF.tweakFrame");
		boolean debug = false;
		if (o != null) {
			debug = Boolean.parseBoolean(o);
			if (debug) {
				LookAndFeelTweak f = new LookAndFeelTweak();
				f.setVisible(true);
			}
		}
	}

	/**
	 * turn off the virtual keyboard for the given JSpinner
	 * 
	 * @param spinner
	 */
	public static void turnOffVirtualKeyboardOnSpinner(final JSpinner spinner) {
		spinner.putClientProperty(SicpaSpinnerUI.VIRTUAL_NUMERIC_KEYBOARD, Boolean.FALSE);
	}

	/**
	 * turn on the virtual keyboard for the given JSpinner
	 * 
	 * @param spinner
	 */
	public static void turnOnVirtualKeyboardOnSpinner(final JSpinner spinner) {
		spinner.putClientProperty(SicpaSpinnerUI.VIRTUAL_NUMERIC_KEYBOARD, Boolean.TRUE);
	}

	/**
	 * turn off the virtual keyboard for all JSpinner
	 */
	public static void turnOffVirtualKeyboardOnSpinner() {
		UIManager.put(SicpaSpinnerUI.VIRTUAL_NUMERIC_KEYBOARD, Boolean.FALSE);
	}

	/**
	 * turn on the virtual keyboard for all JSpinner
	 */
	public static void turnOnVirtualKeyboardOnSpinner() {
		UIManager.put(SicpaSpinnerUI.VIRTUAL_NUMERIC_KEYBOARD, Boolean.TRUE);
	}

	public static void turnOffLockIcon(final JTextComponent comp) {
		comp.putClientProperty(LafWidget.HAS_LOCK_ICON, Boolean.FALSE);
	}

	public static void turnOnLockIcon(final JTextComponent comp) {
		comp.putClientProperty(LafWidget.HAS_LOCK_ICON, Boolean.TRUE);
	}

	public static void turnOffLockIcon() {
		UIManager.put(LafWidget.HAS_LOCK_ICON, Boolean.FALSE);
	}

	public static void turnOnLockIcon() {
		UIManager.put(LafWidget.HAS_LOCK_ICON, Boolean.TRUE);
	}

	/**
	 * turn off the ghosting effect on the given button
	 * 
	 * @param button
	 */
	public static void turnOffGhostingEffectOnButton(final AbstractButton button) {
		AnimationConfigurationManager.getInstance().disallowAnimations(AnimationFacet.GHOSTING_BUTTON_PRESS, button);
	}

	public static void turnOnGhostingEffectOnButton(final AbstractButton button) {
		AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.GHOSTING_BUTTON_PRESS, button);
	}

	public static void turnOnGhostingEffectOnButton() {
		AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.GHOSTING_BUTTON_PRESS);
	}

	public static void turnOffGhostingEffectOnButton() {
		AnimationConfigurationManager.getInstance().disallowAnimations(AnimationFacet.GHOSTING_BUTTON_PRESS);
	}

	public static void setMaxCharPerLineForOptionPane(final int maxChar) {
		UIManager.put(SicpaOptionPaneUI.CHAR_BY_LINE, maxChar);
	}

	private static void initExt() {
		String initMethod;
		try {
			Object o = extProperties.getProperty(RESSOURCE_EXT);
			if (o != null) {
				initMethod = o.toString();
				String className = initMethod.substring(0, initMethod.lastIndexOf("."));
				String methodName = initMethod.substring(initMethod.lastIndexOf(".") + 1);

				Class.forName(className).getMethod(methodName, new Class[] {}).invoke(null, new Object[] {});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Properties getExtProperties() {
		if (extProperties == null) {
			extProperties = new Properties();
			InputStream in = SicpaLookAndFeelCusto.class.getResourceAsStream("ext.ini");
			if (in != null) {
				try {
					extProperties.load(in);

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return extProperties;
	}

	private static final String KEY_UI_VIRTUAL_KEYBOARD = "KEY_UI_VIRTUAL_KEYBOARD";

	public static void turnOnVirtualKeyboard() {
		UIManager.put(KEY_UI_VIRTUAL_KEYBOARD, Boolean.TRUE);
	}

	public static void turnOffVirtualKeyboard() {
		UIManager.put(KEY_UI_VIRTUAL_KEYBOARD, Boolean.FALSE);
	}

	public static boolean isVirtualKeyboardOn() {
		Object o = UIManager.get(KEY_UI_VIRTUAL_KEYBOARD);
		if (o != null) {
			if (o instanceof Boolean) {
				return (Boolean) o;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static void setDefaultVirtualKeyboard(final VirtualKeyboardType type) {
		UIManager.put(VirtualKeyboardPanel.DEFAULT_KEYBOARD_KEY, type);
	}

	public static Image maybeGetShadowedImage(final Image image) {
		Object o = UIManager.get(IMAGE_WITH_SHADOW);
		if ((o instanceof Boolean) && (Boolean) o) {
			return ImageUtils.getShadowedImage(image);
		}
		return image;
	}

	public static void turnOffBasicComponentsAnimation() {
		AnimationConfigurationManager.getInstance().setTimelineDuration(0);
	}

	public static void turnOnPasswordFieldDisplayLastCharacter(JPasswordField f) {
		f.putClientProperty(SicpaPasswordFieldUI.DISPLAY_LAST_CHAR, true);
	}

	public static void turnOffPasswordFieldDisplayLastCharacter(JPasswordField f) {
		f.putClientProperty(SicpaPasswordFieldUI.DISPLAY_LAST_CHAR, false);
	}

	public static void turnOnPasswordFieldDisplayLastCharacter() {
		UIManager.put(SicpaPasswordFieldUI.DISPLAY_LAST_CHAR, true);
	}

	public static void turnOffPasswordFieldDisplayLastCharacter() {
		UIManager.put(SicpaPasswordFieldUI.DISPLAY_LAST_CHAR, false);
	}

	public static void turnOffButtonStateTracking() {
		ButtonVisualStateTracker.STATE_TRACKING_ENABLE = false;
	}

	public static void turnOffRollOverEffect() {
		UIManager.put(SicpaButtonUI.ROLLOVER_EFFECT, false);
	}

	public static void turnOffRollOverEffect(JButton button) {
		button.putClientProperty(SicpaButtonUI.ROLLOVER_EFFECT, false);
	}
}
