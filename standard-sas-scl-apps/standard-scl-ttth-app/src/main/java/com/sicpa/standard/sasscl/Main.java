package com.sicpa.standard.sasscl;

import com.sicpa.ttth.scl.TTTHBootstrap;

public class Main {

	public static void main(final String[] args) {
		TTTHBootstrap.addEjectorPlcVariables();
		new MainAppWithProfile().selectProfile();
	}
}
