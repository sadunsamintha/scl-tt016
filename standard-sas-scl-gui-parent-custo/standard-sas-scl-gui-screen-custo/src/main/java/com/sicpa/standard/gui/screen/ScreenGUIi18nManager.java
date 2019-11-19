package com.sicpa.standard.gui.screen;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.dialog.login.LoginDialog;
import com.sicpa.standard.gui.screen.DMS.MasterPCCClientFrame;
import com.sicpa.standard.gui.screen.DMS.log.LogScreen;
import com.sicpa.standard.gui.screen.DMS.log.TableAdminLog;
import com.sicpa.standard.gui.screen.DMS.log.TableUserLog;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.PreviousSelectionPanel;
import com.sicpa.standard.gui.screen.machine.component.config.ConfigPanel;
import com.sicpa.standard.gui.screen.machine.component.configPassword.DefaultConfigPasswordPanel;
import com.sicpa.standard.gui.screen.machine.component.devicesStatus.DefaultDevicesStatusPanel;
import com.sicpa.standard.gui.screen.machine.component.selectionSummary.DefaultSummaryPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.DefaultStatisticsPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo.DefaultSystemInfoPanel;

public class ScreenGUIi18nManager {

	public static void populateDefault() {

		GUIi18nManager.populateI18n(ConfigPanel.I18N_YES_SAVE_BUTTON, "YES\nSAVE");
		GUIi18nManager.populateI18n(ConfigPanel.I18N_NO_DONT_SAVE_BUTTON, "NO\nDO NOT SAVE");
		GUIi18nManager.populateI18n(ConfigPanel.I18N_CLOSE_MESSAGE_PARAM_MODIFIED,
				"SOME PARAMETERS WERE MODIFIED.\nDO YOU WANT TO SAVE THEM ?");
		GUIi18nManager.populateI18n(ConfigPanel.I18N_EXIT_BUTTON, "EXIT SET-UP");
		GUIi18nManager.populateI18n(LogScreen.I18N_ADMIN, "ADMIN");
		GUIi18nManager.populateI18n(LogScreen.I18N_CANCEL, "CANCEL");
		GUIi18nManager.populateI18n(LogScreen.I18N_REFRESH, "REFRESH");
		GUIi18nManager.populateI18n(LogScreen.I18N_USER, "USER");

		GUIi18nManager.populateI18n(TableAdminLog.I18N_DATE, "DATE");
		GUIi18nManager.populateI18n(TableAdminLog.I18N_FILE, "FILE");
		GUIi18nManager.populateI18n(TableAdminLog.I18N_LOG, "LOG");
		GUIi18nManager.populateI18n(TableAdminLog.I18N_THREAD, "THREAD");

		GUIi18nManager.populateI18n(TableUserLog.I18N_DATE, "DATE");
		GUIi18nManager.populateI18n(TableUserLog.I18N_ITEM_CODE, "ITEM CODE");
		GUIi18nManager.populateI18n(TableUserLog.I18N_OPERATION, "OPERATION");
		GUIi18nManager.populateI18n(TableUserLog.I18N_RESULT, "RESULT");
		GUIi18nManager.populateI18n(TableUserLog.I18N_TYPE, "TYPE");

		GUIi18nManager.populateI18n(MasterPCCClientFrame.I18N_EXIT_CONFIRM_MESSAGE, "EXIT THE APPLICATION?");
		GUIi18nManager.populateI18n(MasterPCCClientFrame.I18N_EXIT_CONFIRM_TITLE, "CONFIRMATION");

		GUIi18nManager.populateI18n(AbstractMachineFrame.I18N_CHANGE_TYPE, "CHANGE\nTYPE");
		GUIi18nManager.populateI18n(AbstractMachineFrame.I18N_EXIT, "EXIT");
		GUIi18nManager
				.populateI18n(AbstractMachineFrame.I18N_EXIT_LABEL, "DO YOU REALLY WANT TO EXIT THE APPLICATION?");
		GUIi18nManager.populateI18n(AbstractMachineFrame.I18N_EXIT_YES, "YES\nEXIT");
		GUIi18nManager.populateI18n(AbstractMachineFrame.I18N_EXIT_NO, "NO\nDO NOT EXIT");
		GUIi18nManager.populateI18n(AbstractMachineFrame.I18N_START, "START");
		GUIi18nManager.populateI18n(AbstractMachineFrame.I18N_STOP, "STOP");

		GUIi18nManager.populateI18n(DefaultSystemInfoPanel.I18N_LABEL_DATE, "SYSTEM TIME");
		GUIi18nManager.populateI18n(DefaultSystemInfoPanel.I18N_LABEL_TITLE, "SYSTEM INFORMATIONS");
		GUIi18nManager.populateI18n(DefaultSystemInfoPanel.I18N_LABEL_VERSION, "VERSION");

		GUIi18nManager.populateI18n(DefaultStatisticsPanel.I18N_LABEL_INVALID_CODE, "INVALID");
		GUIi18nManager.populateI18n(DefaultStatisticsPanel.I18N_LABEL_LINE_SPEED, "LINE SPEED");
		GUIi18nManager.populateI18n(DefaultStatisticsPanel.I18N_LABEL_TOTAL_CODE, "TOTAL");
		GUIi18nManager.populateI18n(DefaultStatisticsPanel.I18N_LABEL_VALID_CODE, "VALID");
		GUIi18nManager.populateI18n(DefaultStatisticsPanel.I18N_LABEL_TITLE, "STATISTICS");
		GUIi18nManager.populateI18n(DefaultStatisticsPanel.I18N_LABEL_CODEFREQ, "CODE FREQ");
		GUIi18nManager.populateI18n(DefaultStatisticsPanel.I18N_LABEL_UPTIME, "UPTIME");

		GUIi18nManager.populateI18n(DefaultDevicesStatusPanel.I18N_LABEL_TITLE, "DEVICES CONNECTION");
		// GUIi18nManager.populateI18n(,"");

		GUIi18nManager.populateI18n(DefaultConfigPasswordPanel.I18N_CONFIG_PASSWORD_BUTTON_CANCEL, "CANCEL");
		GUIi18nManager.populateI18n(DefaultConfigPasswordPanel.I18N_CONFIG_PASSWORD_BUTTON_LOGIN, "OK");

		GUIi18nManager.populateI18n(DefaultSummaryPanel.I18N_TITLE, "SELECTIONS");

		GUIi18nManager.populateI18n(PreviousSelectionPanel.I18N_SELECT_PREVIOUS_LABEL, "PREVIOUS SELECTION:");
		GUIi18nManager.populateI18n(PreviousSelectionPanel.I18N_NO_PREVIOUS_VALUES, "NO PREVIOUS SELECTION");
	}

	public static void main(final String[] args) {
		GUIi18nManager.populateI18n(LoginDialog.I18N_LOGIN, "LOGIN");
		System.out.println(GUIi18nManager.get(LoginDialog.I18N_LOGIN));
	}
}
