package com.sicpa.standard.gui.screen.DMS.model;

import javax.swing.JOptionPane;

import com.sicpa.standard.gui.screen.DMS.MasterPCCClientFrame;
import com.sicpa.standard.gui.screen.DMS.mvc.DefaultModel;
import com.sicpa.standard.gui.screen.DMS.mvc.utils.JOptionPaneMessageWrapper;
import com.sicpa.standard.gui.screen.DMS.mvc.utils.JoptionPaneMessageAutoCloseWrapper;

public class MasterPccClientModel extends DefaultModel {

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public void initDefault() {

	}

	@Override
	public boolean isDirty() {
		return false;
	}

	public void setViewBusy(final boolean flag) {
		firePropertyChange(MasterPCCClientFrame.PROPERTY_BUSY, null, Boolean.valueOf(flag));
	}

	protected void showMessage(final String body) {
		showMessage(body, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	protected void showMessage(final String body, final String title, final int messageType) {
		firePropertyChange(MasterPCCClientFrame.PROPERTY_MESSAGE, null, new JOptionPaneMessageWrapper(body, title,
				messageType));
	}

	protected void showMessageAutoClose(final String body, final String title, final int messageType, final int seconds) {
		firePropertyChange(MasterPCCClientFrame.PROPERTY_MESSAGE, null, new JoptionPaneMessageAutoCloseWrapper(body,
				title, messageType, seconds));
	}
}
