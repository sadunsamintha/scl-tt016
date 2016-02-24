package com.sicpa.standard.sasscl;

import java.io.File;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.screen.loader.LoadApplicationScreen;

public class Main {

	static {
		File log = new File("./log");
		if (!log.exists()) {
			log.mkdir();
		}
	}

	public static void main(final String[] args) {
		LoadApplicationScreen.DOUBLE_BUFFERING_OFF = false;
		SicpaLookAndFeel.install();
		MainAppWithProfile app = new MainAppWithProfile();
		app.selectProfile();
	}
}
