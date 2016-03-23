package com.sicpa.tt018.scl;

import com.sicpa.standard.sasscl.MainAppWithProfile;

public class Main {

	public static void main(String[] args) {
		TT018Bootstrap.addCustoPlcVariable();
		new MainAppWithProfile().selectProfile();
	}

}
