package com.sicpa.standard.sasscl.view;

import java.awt.Color;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatus;

public class STDSASSCLApplicationStatus extends ApplicationStatus {

	public STDSASSCLApplicationStatus(final Color color, final String labelStatus) {
		super(color, labelStatus);
	}

	public String getLabelStatus() {
		return Messages.get(super.getLabelStatus());
	}

}
