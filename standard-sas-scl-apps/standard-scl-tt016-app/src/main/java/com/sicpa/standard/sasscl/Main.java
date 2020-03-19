package com.sicpa.standard.sasscl;

import com.sicpa.tt016.scl.TT016Bootstrap;
import com.sicpa.tt016.scl.TT016MainAppWithProfile;

public class Main {

	public static void main(final String[] args) {
		TT016Bootstrap.addPlcVariableJavaEjectionCounter();
		TT016Bootstrap.addWiperPlcVariable();
		TT016Bootstrap.addMotorizedBeamPlcVariables();
		new TT016MainAppWithProfile().selectProfile();
	}
}