package com.sicpa.standard.gui.screen.machine;

import java.awt.Frame;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockablePanel.LockablePanelModel;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockingError.LockingErrorModel;
import com.sicpa.standard.gui.model.BasicMapLikeModel;
import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputmodel;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatus;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatusModel;
import com.sicpa.standard.gui.screen.machine.component.configPassword.ConfigPasswordModel;
import com.sicpa.standard.gui.screen.machine.component.configPassword.IConfigPasswordChecker;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationCallback;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationModel;
import com.sicpa.standard.gui.screen.machine.component.emergency.EmergencyModel;
import com.sicpa.standard.gui.screen.machine.component.error.ErrorItem;
import com.sicpa.standard.gui.screen.machine.component.error.ErrorType;
import com.sicpa.standard.gui.screen.machine.component.error.ScrollingErrorModel;
import com.sicpa.standard.gui.screen.machine.component.lineId.LineIdModel;
import com.sicpa.standard.gui.screen.machine.component.progress.ProgressModel;
import com.sicpa.standard.gui.screen.machine.component.warning.Error;
import com.sicpa.standard.gui.screen.machine.component.warning.MessagesModel;
import com.sicpa.standard.gui.screen.machine.component.warning.Warning;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class MachineViewController {
	protected MessagesModel messagesModel;
	protected ApplicationStatusModel applicationStatusModel;
	@Deprecated
	protected ScrollingErrorModel scrollingMinorErrorModel;
	@Deprecated
	protected ScrollingErrorModel scrollingFatalErrorModel;
	protected ConfirmationModel confirmationModel;
	protected ConfirmationModel unblockableConfirmationModel;
	@Deprecated
	protected EmergencyModel emergencyModel;
	protected LockingErrorModel lockingErrorModel;
	protected LockablePanelModel lockableFooterModel;
	protected ConfigPasswordModel configPasswordModel;
	protected LineIdModel lineIdModel;
	protected BasicMapLikeModel headerInfoModel;
	protected ProgressModel runningModel;
	protected IdInputmodel barcodeModel;
	protected LockablePanelModel lockableFrameModel;

	public void addWarning(final String key, final String message, final boolean removeable) {
		this.messagesModel.addMessage(new Warning(key, message, removeable));
	}

	public void addOrUpdateWarning(final String key, final String message, final boolean removeable) {
		if (this.messagesModel != null) {
			this.messagesModel.addOrUpdateMessage(new Warning(key, message, removeable));
		}
	}

	public void removeWarning(final String key, final String message) {
		if (this.messagesModel != null) {
			this.messagesModel.removeMessage(new Warning(key, message));
		}
	}

	public void removeWarning(final String key) {
		if (this.messagesModel != null) {
			this.messagesModel.removeMessage(key);
		}
	}

	public void addError(final String key, final String message) {
		if (this.messagesModel != null) {
			this.messagesModel.addMessage(new Error(key, message));
		}
	}

	public void addOrUpdateError(final String key, final String message) {
		if (this.messagesModel != null) {
			this.messagesModel.addOrUpdateMessage(new Error(key, message));
		}
	}

	public void removeError(final String key, final String message) {
		if (this.messagesModel != null) {
			this.messagesModel.removeMessage(new Error(key, message));
		}
	}

	public void removeAllWarnings() {
		if (this.messagesModel != null) {
			this.messagesModel.reset();
		}
	}

	public void setApplicationStatus(final ApplicationStatus status) {
		this.applicationStatusModel.setApplicationStatus(status);
	}

	@Deprecated
	public void addFatalError(final String key, final String title, final String text) {
		this.scrollingFatalErrorModel.addError(new ErrorItem(title, text, ErrorType.FATAL, key));
	}

	@Deprecated
	public void addOrUpdateFatalError(final String key, final String title, final String text) {
		this.scrollingFatalErrorModel.addOrUpdateError(new ErrorItem(title, text, ErrorType.FATAL, key));
	}

	@Deprecated
	public void addMinorError(final String key, final String title, final String text) {
		this.scrollingMinorErrorModel.addError(new ErrorItem(title, text, ErrorType.MINOR, key));
	}

	@Deprecated
	public void addOrUpdateMinorError(final String key, final String title, final String text) {
		this.scrollingMinorErrorModel.addOrUpdateError(new ErrorItem(title, text, ErrorType.MINOR, key));
	}

	@Deprecated
	public void removeMinorError(final String key) {
		this.scrollingMinorErrorModel.removeError(key);
	}

	@Deprecated
	public void removeFatalError(final String key) {
		this.scrollingFatalErrorModel.removeError(key);
	}

	@Deprecated
	public void removeAllFatalError() {
		this.scrollingFatalErrorModel.reset();
	}

	@Deprecated
	public void removeAllMinorError() {
		this.scrollingMinorErrorModel.reset();
	}

	@Deprecated
	public void removeError(final String key) {
		this.scrollingFatalErrorModel.removeError(key);
		this.scrollingMinorErrorModel.removeError(key);
	}

	public void askConfirmation(final String text, final String okText, final String cancelText,
			final ConfirmationCallback callback) {

		this.confirmationModel.removeCallbacks();
		this.confirmationModel.setCancelButtonText(cancelText);
		this.confirmationModel.setConfirmButtonText(okText);
		this.confirmationModel.setQuestion(text);
		this.confirmationModel.addCallback(callback);
		this.confirmationModel.ask();
	}

	public void askExit(final ConfirmationCallback callback) {
		askConfirmationUnblockable(GUIi18nManager.get(AbstractMachineFrame.I18N_EXIT_LABEL),
				GUIi18nManager.get(AbstractMachineFrame.I18N_EXIT_YES),
				GUIi18nManager.get(AbstractMachineFrame.I18N_EXIT_NO), callback);
	}

	public void askConfirmationUnblockable(final String text, final String okText, final String cancelText,
			final ConfirmationCallback callback) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				AbstractMachineFrame frame = getView();
				if (frame.getProdPanel().isShowing()) {
					Point p = frame.getProdPanel().getLocationOnScreen();
					SwingUtilities.convertPointFromScreen(p, frame.getLayeredPane());
					frame.getConfirmationUnblockablePanel().setLocation(p);
				} else {
					frame.getConfirmationUnblockablePanel().setLocation(0, frame.getHeader().getHeight() - 5);
				}

				if (frame.getConfirmationUnblockablePanel().getWidth() <= 0) {
					int h = frame.getHeight() - frame.getHeader().getHeight() - frame.getFooter().getHeight();
					frame.getConfirmationUnblockablePanel().setSize(500, h);
				}
			}
		});
		this.unblockableConfirmationModel.removeCallbacks();
		this.unblockableConfirmationModel.setCancelButtonText(cancelText);
		this.unblockableConfirmationModel.setConfirmButtonText(okText);
		this.unblockableConfirmationModel.setConfirmButtonEnabled(true);
		this.unblockableConfirmationModel.setQuestion(text);
		this.unblockableConfirmationModel.addCallback(callback);
		this.unblockableConfirmationModel.ask();
	}

	@Deprecated
	public void showEmergency(final String text) {
		this.emergencyModel.setText(text);
		this.emergencyModel.setVisible(true);
	}

	@Deprecated
	public void hideEmergency() {
		this.emergencyModel.setVisible(false);
	}

	public void removeAllErrorMainPanel() {
		this.lockingErrorModel.removeAllMessages();
	}

	public void removeErrorMainPanel(final String key) {
		this.lockingErrorModel.removeMessage(key);
	}

	public void addErrorMainPanel(final String key, final String message) {
		this.lockingErrorModel.addMessage(key, message);
	}

	public void lockFooter(final boolean flag, final String key) {
		this.lockableFooterModel.lock(flag, key);
	}

	public void lockFooter(final boolean flag) {
		lockFooter(flag, " ");
	}

	public void lockFrame(final boolean flag, final String key) {
		this.lockableFrameModel.lock(flag, key);
	}

	public void releaseAllLockOnFooter() {
		this.lockableFooterModel.releaseAllLocks();
	}

	public void setConfigPasswordChecker(final IConfigPasswordChecker checker) {
		this.configPasswordModel.setChecker(checker);
	}

	public void setLineId(final String lineId) {
		this.lineIdModel.setLineID(lineId);
	}

	public ApplicationStatus getApplicationStatus() {
		return this.applicationStatusModel.getApplicationStatus();
	}

	public void executeTaskInBackground(final Runnable task) {
		this.runningModel.executeInBackground(task);
	}

	public void setBarcodeError(final String error) {
		this.barcodeModel.setError(error);
	}

	public void setBarcode(final String barcode) {
		this.barcodeModel.setId(barcode);
	}

	public void setBarcodeDescription(final String description) {
		this.barcodeModel.setDescription(description);
	}

	// -- public model
	public void setProductionDataSelectionModel(final AbstractSelectionFlowModel model) {
		getView().setProductionDataSelectionModel(model);
	}

	public void setHeaderInfoModel(final BasicMapLikeModel headerInfoModel) {
		this.headerInfoModel = headerInfoModel;
	}

	// --------------MODEL--------------------
	protected void setLockingErrorModel(final LockingErrorModel lockingErrorModel) {
		this.lockingErrorModel = lockingErrorModel;
	}

	protected void setLockableFooterModel(final LockablePanelModel lockableFooterModel) {
		this.lockableFooterModel = lockableFooterModel;
	}

	protected void setApplicationStatusModel(final ApplicationStatusModel applicationStatusModel) {
		this.applicationStatusModel = applicationStatusModel;
	}

	protected void setConfigPasswordModel(final ConfigPasswordModel configPasswordModel) {
		this.configPasswordModel = configPasswordModel;
	}

	protected void setConfirmationModel(final ConfirmationModel confirmationModel) {
		this.confirmationModel = confirmationModel;
	}

	public void setUnblockableConfirmationModel(final ConfirmationModel exitModel) {
		this.unblockableConfirmationModel = exitModel;
	}

	@Deprecated
	protected void setEmergencyModel(final EmergencyModel emergencyModel) {
		this.emergencyModel = emergencyModel;
	}

	protected void setMessagesModel(final MessagesModel messagesModel) {
		this.messagesModel = messagesModel;
	}

	@Deprecated
	protected void setScrollingFatalErrorModel(final ScrollingErrorModel scrollingFatalErrorModel) {
		this.scrollingFatalErrorModel = scrollingFatalErrorModel;
	}

	@Deprecated
	protected void setScrollingMinorErrorModel(final ScrollingErrorModel scrollingMinorErrorModel) {
		this.scrollingMinorErrorModel = scrollingMinorErrorModel;
	}

	protected void setLineIdModel(final LineIdModel lineIdModel) {
		this.lineIdModel = lineIdModel;
	}

	protected void setRunningModel(final ProgressModel runningModel) {
		this.runningModel = runningModel;
	}

	protected void setBarcodeModel(final IdInputmodel barcodeModel) {
		this.barcodeModel = barcodeModel;
	}

	protected void setLockableFrameModel(final LockablePanelModel lockableFrameModel) {
		this.lockableFrameModel = lockableFrameModel;
	}

	protected AbstractMachineFrame getView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof AbstractMachineFrame) {
				return (AbstractMachineFrame) f;
			}
		}
		return null;
	}
}
