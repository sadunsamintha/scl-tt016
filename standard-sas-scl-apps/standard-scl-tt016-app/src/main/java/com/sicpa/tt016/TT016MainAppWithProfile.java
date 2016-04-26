package com.sicpa.tt016;

import com.sicpa.standard.sasscl.MainAppWithProfile;

import java.util.List;

public class TT016MainAppWithProfile extends MainAppWithProfile {

	@Override
	protected List<String> createSpringFilesToLoad() {
		List<String> springFilesToLoad = super.createSpringFilesToLoad();

		springFilesToLoad.add("spring/offlineCounting.xml");

		return springFilesToLoad;
	}
}
