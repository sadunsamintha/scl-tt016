package com.sicpa.tt018;

import com.sicpa.standard.sasscl.MainAppWithProfile;

public class Main {

	public static void main(final String[] args) {
		new MainAppWithProfile().selectProfile();
		// SpringConfig customConfig = createTT018Configuration();
		// PropertyPlaceholderResourcesSASSCL.addFile("config/printer/extended-code-configuration.properties");
	}

	// private static SpringConfig createTT018Configuration() {
	// SpringConfig config = new AlbaniaSpringConfig();
	// //otherwise STD will crash..
	// addBRSConfiguration(config);
	// return config;
	// }
	//
	// private static void addBRSConfiguration(SpringConfig config) {
	// config.put("brs", "spring/brs.xml");
	// }

}
