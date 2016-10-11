package com.sicpa.standard.sasscl.common.utils;

import java.awt.HeadlessException;
import java.io.UnsupportedEncodingException;
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

	private  static final String ISO_8859_1 = "iso_8859-1";

	private static final String UTF_8 = "UTF-8";

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
			Messages.load("language/sasscl", language);

			ResourceBundle bundle = Messages.messages.get("language/sasscl");
			Enumeration<String> e = bundle.getKeys();
			while (e.hasMoreElements()) {
				String key = e.nextElement();
				//utf-8 workaround
				String value = getMessage(bundle, key);
				GUIi18nManager.populateI18n(key, value);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public static String getMessage(ResourceBundle bundle,String key) {
		String value = bundle.getString(key);
		if(value == null || value.isEmpty()) {
			return "<" + key + ">"; // default value
		}
		try {
			//utf-8 workaround
			return new String(bundle.getString(key).getBytes(ISO_8859_1), UTF_8);
		} catch (UnsupportedEncodingException e) {
			return "<" + key + "> (UnsupportedEncoding)";
		}
	}


}
