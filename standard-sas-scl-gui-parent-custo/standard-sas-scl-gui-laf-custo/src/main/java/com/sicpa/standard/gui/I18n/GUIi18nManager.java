package com.sicpa.standard.gui.I18n;

import java.lang.reflect.Method;
import java.util.Properties;

import com.sicpa.standard.gui.components.dialog.login.LoginDialog;
import com.sicpa.standard.gui.components.panels.NavigablePanels;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.components.table.CopyToClipboardDialog;
import com.sicpa.standard.gui.components.virtualKeyboard.SMVirtualKeyboardDialog;
import com.sicpa.standard.gui.components.virtualKeyboard.SpinnerNumericVirtualKeyboard;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;

public class GUIi18nManager {

	public static final String SUFFIX = "std.gui.";
	public static boolean DEBUG = false;

	private static Properties prop;

	static {
		populateDefault();
	}

	public static String get(final String key) {
		Object s = prop.get(key);
		return s != null ? s.toString() : key;
	}

	public static void populateI18n(final String key, final String value) {
		prop.put(key, value);
	}

	public static void populateI18n(final Properties prop) {
		for (Object key : prop.keySet()) {
			populateI18n(key + "", prop.getProperty(key + ""));
		}
	}

	private static void populateDefault() {

		if (prop != null) {
			return;
		}
		prop = new Properties();

		prop.put(CopyToClipboardDialog.I18N_LABEL_TITLE, "Copy to clipboard");
		prop.put(CopyToClipboardDialog.I18N_RADIO_CELL, "cell");
		prop.put(CopyToClipboardDialog.I18N_RADIO_ROW, "row");
		prop.put(CopyToClipboardDialog.I18N_RADIO_COLUMN, "column");
		prop.put(CopyToClipboardDialog.I18N_CHECK_HEADER, "Include header");
		prop.put(CopyToClipboardDialog.I18N_BUTTON_OK, "Copy");
		prop.put(CopyToClipboardDialog.I18N_BUTTON_CANCEL, "Cancel");

		prop.put(NavigablePanels.I18N_PREVIEW, "PREVIEW");

		prop.put(VirtualKeyboardPanel.I18N_BACK, "del");
		prop.put(VirtualKeyboardPanel.I18N_CLEAR, "clr");
		prop.put(VirtualKeyboardPanel.I18N_ALT, "alt");
		prop.put(VirtualKeyboardPanel.I18N_CTRL, "ctrl");

		prop.put(SpinnerNumericVirtualKeyboard.I18N_CLEAR, "CLEAR");
		prop.put(SpinnerNumericVirtualKeyboard.I18N_CANCEL, "CANCEL");
		prop.put(SpinnerNumericVirtualKeyboard.I18N_OK, "OK");

		prop.put(SMVirtualKeyboardDialog.I18N_OK, "OK");
		prop.put(SMVirtualKeyboardDialog.I18N_CANCEL, "CANCEL");

		prop.put(SicpaTableCellRenderer.I18N_DATE_FORMATTER, "yyyy/MM/dd");
		prop.put(SicpaTableCellRenderer.I18N_DATE_TIME_FORMATTER, "yyyy/MM/dd HH:mm");
		prop.put(SicpaTableCellRenderer.I18N_TIME_FORMATTER, "HH:mm:ss");

		prop.put(LoginDialog.I18N_CANCEL, "CANCEL");
		prop.put(LoginDialog.I18N_LOGIN, "LOGIN");

		try {
			Class<?> c = Class.forName("com.sicpa.standard.gui.screen.ScreenGUIi18nManager");
			Object o = c.newInstance();
			Method m = c.getMethod("populateDefault");
			m.invoke(o);
		} catch (Exception e) {
		}
	}
}
