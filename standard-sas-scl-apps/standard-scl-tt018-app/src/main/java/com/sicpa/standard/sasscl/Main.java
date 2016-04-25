package com.sicpa.standard.sasscl;

import com.sicpa.tt018.scl.TT018Bootstrap;

public class Main {

	public static void main(String[] args) {
		TT018Bootstrap.addCustoPlcVariable();
		new MainAppWithProfile().selectProfile();
	}

}
