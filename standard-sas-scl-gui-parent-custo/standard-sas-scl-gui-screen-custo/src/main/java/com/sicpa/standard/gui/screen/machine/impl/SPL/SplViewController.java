package com.sicpa.standard.gui.screen.machine.impl.SPL;

import com.sicpa.standard.gui.components.layeredComponents.lock.lockablePanel.LockablePanelModel;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockingError.LockingErrorModel;
import com.sicpa.standard.gui.model.BasicMapLikeModel;
import com.sicpa.standard.gui.screen.machine.MachineViewController;
import com.sicpa.standard.gui.screen.machine.component.applicationStatus.ApplicationStatusModel;
import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationModel;
import com.sicpa.standard.gui.screen.machine.component.devicesStatus.DeviceStatus;
import com.sicpa.standard.gui.screen.machine.component.devicesStatus.DevicesStatusModel;
import com.sicpa.standard.gui.screen.machine.component.emergency.EmergencyModel;
import com.sicpa.standard.gui.screen.machine.component.error.ScrollingErrorModel;
import com.sicpa.standard.gui.screen.machine.component.warning.Error;
import com.sicpa.standard.gui.screen.machine.component.warning.MessagesModel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.StatisticsModel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo.SystemInfoModel;

public class SplViewController extends MachineViewController {

	protected StatisticsModel statsModel;
	protected DevicesStatusModel devicesModel;
	protected SystemInfoModel systemModel;

	public void addInvalidCodes() {
		this.statsModel.addInvalid();
	}

	public void addInvalidCodes(final long number) {
		this.statsModel.addInvalid(number);
	}

	public void addValidCodes() {
		this.statsModel.addValid();
	}

	public void addValidCodes(final long number) {
		this.statsModel.addValidCodes(number);
	}

	public void setInvalidCodes(final int invalid) {
		this.statsModel.setInvalid(invalid);
	}

	public void setLineSpeed(final int lineSpeed) {
		this.statsModel.setLineSpeed(lineSpeed);
	}

	public void setCodeFreq(final int codeFreq) {
		this.statsModel.setCodeFreq(codeFreq);
	}

	public void startUptime() {
		this.statsModel.startUptime();
	}

	public void stopUptime() {
		this.statsModel.stopUptime();
	}

	public void setValidCodes(final int valid) {
		this.statsModel.setValid(valid);
	}

	public void addDevice(final String key, final String label) {
		this.devicesModel.addDevice(key, label);
	}

	public void removeDevice(final String key) {
		this.devicesModel.removeDevice(key);
	}

	public void setDeviceStatus(final String key, final DeviceStatus status) {
		this.devicesModel.changeStatus(key, status);
	}

	public void setVersion(final String version) {
		this.systemModel.setVersion(version);
	}

	public void resetStatistics() {
		this.statsModel.reset();
	}

	public void removeError(final String key, final String message) {
		this.messagesModel.removeMessage(new Error(key, message));
	}
	
	// ---------listeners

	public void setSystemInfoDateTimeFormat(final String dateFormat) {
		this.systemModel.setDateFormat(dateFormat);
	}

	//

	// ------------ model
	// ------ protected setter so the current package see them but not the user of the controller

	protected void setDevicesModel(final DevicesStatusModel devicesModel) {
		this.devicesModel = devicesModel;
	}

	protected void setStatsModel(final StatisticsModel statsModel) {
		this.statsModel = statsModel;
	}

	protected void setSystemModel(final SystemInfoModel systemModel) {
		this.systemModel = systemModel;
	}

	// --override so it s visible to the current package

	@Override
	protected void setApplicationStatusModel(final ApplicationStatusModel applicationStatusModel) {
		super.setApplicationStatusModel(applicationStatusModel);
	}

	@Override
	protected void setConfirmationModel(final ConfirmationModel confirmationModel) {
		super.setConfirmationModel(confirmationModel);
	}

	@Override
	protected void setEmergencyModel(final EmergencyModel emergencyModel) {
		super.setEmergencyModel(emergencyModel);
	}

	@Override
	protected void setLockableFooterModel(final LockablePanelModel lockableFooterModel) {
		super.setLockableFooterModel(lockableFooterModel);
	}

	@Override
	protected void setLockingErrorModel(final LockingErrorModel lockingErrorModel) {
		super.setLockingErrorModel(lockingErrorModel);
	}

	@Override
	protected void setMessagesModel(final MessagesModel messagesModel) {
		super.setMessagesModel(messagesModel);
	}

	@Override
	protected void setScrollingFatalErrorModel(final ScrollingErrorModel scrollingFatalErrorModel) {
		super.setScrollingFatalErrorModel(scrollingFatalErrorModel);
	}

	@Override
	protected void setScrollingMinorErrorModel(final ScrollingErrorModel scrollingMinorErrorModel) {
		super.setScrollingMinorErrorModel(scrollingMinorErrorModel);
	}

	@Override
	public void setHeaderInfoModel(final BasicMapLikeModel headerInfoModel) {
		super.setHeaderInfoModel(headerInfoModel);
	}
}
