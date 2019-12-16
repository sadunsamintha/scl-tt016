package com.sicpa.standard.gui.screen.DMS.log;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.screen.DMS.MasterPCCClientFrame;
import com.sicpa.standard.gui.screen.DMS.log.UserLog.EStatusLog;
import com.sicpa.standard.gui.screen.DMS.mvc.AbstractView;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class LogScreen extends JPanel implements TransitionTarget, AbstractView {

	public static final String I18N_ADMIN = GUIi18nManager.SUFFIX+"DMS.LogScreen.admin";
	public static final String I18N_USER = GUIi18nManager.SUFFIX+"DMS.LogScreen.user";
	public static final String I18N_CANCEL = GUIi18nManager.SUFFIX+"DMS.LogScreen.cancel";
	public static final String I18N_REFRESH = GUIi18nManager.SUFFIX+"DMS.LogScreen.refresh";

	private TableUserLog userLog;
	private TableAdminLog adminLog;
	private JCheckBox checkUser;
	private JCheckBox checkAdmin;
	private JButton closeButton;
	private JButton refreshButton;
	private AbstractAction refreshButtonAction;
	private MasterPCCClientFrame frame;

	public LogScreen(final MasterPCCClientFrame frame) {
		this.frame = frame;
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,hidemode 3"));
		add(getAdminLog(), "grow,span,wrap");
		add(getUserLog(), "gap top 20 ,grow,span,wrap");
		add(getCheckUser(), "span,split 5");
		add(getCheckAdmin(), "");
		add(Box.createGlue(), "pushx,growx");
		add(getRefreshButton());
		add(getCloseButton(), "");

		getUserLog().getTable().getColumnModel().getColumn(3).setCellRenderer(
				new UserLogi18nCellRenderer(this.frame.getLanguageProperties()));
		getUserLog().getTable().getColumnModel().getColumn(4).setCellRenderer(
				new UserLogi18nCellRenderer(this.frame.getLanguageProperties()));

	}

	public TableUserLog getUserLog() {
		if (this.userLog == null) {
			this.userLog = new TableUserLog();
		}
		return this.userLog;
	}

	public TableAdminLog getAdminLog() {
		if (this.adminLog == null) {
			this.adminLog = TableAdminLog.getINSTANCE();
			this.adminLog.setVisible(false);
		}
		return this.adminLog;
	}

	public JCheckBox getCheckAdmin() {
		if (this.checkAdmin == null) {
			this.checkAdmin = new JCheckBox(GUIi18nManager.get(I18N_ADMIN));
			// this.checkAdmin.setSelected(true);
			this.checkAdmin.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					checkAdminActionPerformed();
				}
			});
		}
		return this.checkAdmin;
	}

	private void checkAdminActionPerformed() {
		new ScreenTransition(this, this, 500).start();
	}

	public JCheckBox getCheckUser() {
		if (this.checkUser == null) {
			this.checkUser = new JCheckBox(GUIi18nManager.get(I18N_USER));
			this.checkUser.setSelected(true);
			this.checkUser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					checkUserActionPerformed();
				}
			});
		}
		return this.checkUser;
	}

	private void checkUserActionPerformed() {
		new ScreenTransition(this, this, 500).start();
	}

	@Override
	public void setupNextScreen() {
		this.userLog.setVisible(this.checkUser.isSelected());
		this.adminLog.setVisible(this.checkAdmin.isSelected());
	}

	public JButton getCloseButton() {
		if (this.closeButton == null) {
			this.closeButton = new JButton(GUIi18nManager.get(I18N_CANCEL));
			this.closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonCloseActionPerformed();
				}
			});
		}
		return this.closeButton;
	}

	private void buttonCloseActionPerformed() {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof MasterPCCClientFrame) {
			((MasterPCCClientFrame) w).replaceMainPanel(null);
		}
	}

	public JButton getRefreshButton() {
		if (this.refreshButton == null) {
			this.refreshButton = new JButton(GUIi18nManager.get(I18N_REFRESH));
			this.refreshButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					refreshButtonActionPerformed();
				}
			});
		}
		return this.refreshButton;
	}

	private void refreshButtonActionPerformed() {
		getUserLog().model.getDataVector().clear();
		getUserLog().model.fireTableDataChanged();
		if (this.refreshButtonAction != null) {
			final Window w = SwingUtilities.getWindowAncestor(this);
			if (w instanceof MasterPCCClientFrame) {
				((MasterPCCClientFrame) w).setBusy(true);

				new Thread(new Runnable() {
					@Override
					public void run() {
						LogScreen.this.refreshButtonAction.actionPerformed(null);
						((MasterPCCClientFrame) w).setBusy(false);
					}
				}).start();
			}
		}
	}

	public void setRefreshButtonAction(final AbstractAction refreshButtonAction) {
		this.refreshButtonAction = refreshButtonAction;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		Object o = evt.getNewValue();
		if (evt.getPropertyName().equals(EStatusLog.failure.toString())
				|| evt.getPropertyName().equals(EStatusLog.success.toString())) {
			if (o instanceof UserLog) {
				UserLog log = (UserLog) o;
				this.userLog.addLineFirst(log.getStatus(), log.getDate(), log.getItemCode(), log.getOperation(), log
						.getResult());
			}
		}
		// loading logs from db
		else if (evt.getPropertyName().equals("OperationsLogs")) {
			if (o instanceof List) {
				final List<UserLog> logs = (List<UserLog>) o;
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						for (UserLog log : logs) {
							LogScreen.this.userLog.addLineLast(log.getStatus(), log.getDate(), log.getItemCode(), log
									.getOperation(), log.getResult());
						}
					}
				});
			}
		}
	}
}
