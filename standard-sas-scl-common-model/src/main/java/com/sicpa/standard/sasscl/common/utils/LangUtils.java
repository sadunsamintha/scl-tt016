package com.sicpa.standard.sasscl.common.utils;

import java.awt.HeadlessException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXLoginPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;

public class LangUtils {

	private static final Logger logger= LoggerFactory.getLogger(LangUtils.class);

	public static void initLanguageFiles(final String language) {
		try {
			if (!AppUtils.isHeadless()) {
				Locale.setDefault(new Locale(language));
				// refresh the ui to use the new local
				SicpaLookAndFeel.install();
				JOptionPane.setDefaultLocale(Locale.getDefault());
				JXDatePicker.setDefaultLocale(Locale.getDefault());
				JXLoginPane.setDefaultLocale(Locale.getDefault());
			}
		} catch (HeadlessException e) {
		} catch (Exception e) {
			logger.error("", e);
		}
		try {
			Messages.remove("language/sasscl");
			Messages.load("language/sasscl", language.toLowerCase());

			ResourceBundle bundle = Messages.messages.get("language/sasscl");
			Enumeration<String> e = bundle.getKeys();
			while (e.hasMoreElements()) {
				String key = e.nextElement();
				String value = Messages.get(key);
				GUIi18nManager.populateI18n(key, value);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
