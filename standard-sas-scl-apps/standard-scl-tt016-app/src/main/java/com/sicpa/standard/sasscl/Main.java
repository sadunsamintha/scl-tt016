package com.sicpa.standard.sasscl;

import com.sicpa.tt016.scl.TT016Bootstrap;

public class Main {

	public static void main(final String[] args) {
		TT016Bootstrap.addPlcVariableJavaEjectionCounter();
		new MainAppWithProfile().selectProfile();
	}
}