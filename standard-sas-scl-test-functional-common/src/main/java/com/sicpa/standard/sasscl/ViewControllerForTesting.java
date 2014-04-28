package com.sicpa.standard.sasscl;

import java.awt.Frame;

import javax.swing.JFrame;

import com.sicpa.standard.gui.components.layeredComponents.lock.lockingError.LockingErrorModel;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatus;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatusModel;
import com.sicpa.standard.gui.screen.machine.component.warning.Message;
import com.sicpa.standard.gui.screen.machine.component.warning.MessagesModel;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.sasscl.view.messages.I18nableLockingErrorModel;

public class ViewControllerForTesting extends MainFrameController {

	public ViewControllerForTesting(SkuListProvider skuListProvider) {
		super(skuListProvider);

		setApplicationStatusModel(new ApplicationStatusModel());
		setLockingErrorModel(new I18nableLockingErrorModel());
		setMessagesModel(new MessagesModel());
	}

	public boolean containsWarning(String warning) {
		for (Message s : messagesModel.getMessages()) {
			if (s.getMessage().equals(warning)) {
				return true;
			}
		}
		return false;
	}

	public LockingErrorModel getLockingErrorModel() {
		return this.lockingErrorModel;
	}

	public boolean isShowingErrorMainPanel(String message) {
		return this.lockingErrorModel.getMessages().contains(message);
	}


	@Override
	public void setApplicationStatusModel(ApplicationStatusModel applicationStatusModel) {
		super.setApplicationStatusModel(applicationStatusModel);
	}

	public void setApplicationStatus(final ApplicationStatus status) {
		if (applicationStatusModel != null)
			this.applicationStatusModel.setApplicationStatus(status);
	}

	@Override
	protected AbstractMachineFrame getView() {
		// return the last one
		// because of the test suite we get more than 1 mainFrame
		MainFrame frame = null;
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof MainFrame) {
				frame = (MainFrame) f;
			}
		}
		return frame;
	}

	@Override
	public void setLockingErrorModel(LockingErrorModel lockingErrorModel) {
		super.setLockingErrorModel(lockingErrorModel);
	}

	@Override
	public void setMessagesModel(MessagesModel messagesModel) {
		super.setMessagesModel(messagesModel);
	}
}
