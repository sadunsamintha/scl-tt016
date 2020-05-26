package com.sicpa.tt065.scl;

import com.sicpa.standard.sasscl.MainAppWithProfile;

import java.util.List;

public class TT065MainAppWithProfile extends MainAppWithProfile {

	@Override
	protected List<String> createSpringFilesToLoad() {
		List<String> config =  super.createSpringFilesToLoad();
		config.remove("spring/plc/plc-import.groovy");
		return config;
	}
}
