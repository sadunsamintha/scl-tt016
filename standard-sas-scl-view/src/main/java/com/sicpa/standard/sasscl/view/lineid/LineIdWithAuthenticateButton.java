package com.sicpa.standard.sasscl.view.lineid;

import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginService;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.dialog.login.LoginDialog;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.lineId.DefaultLineIdPanel;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.MainFrame;

public class LineIdWithAuthenticateButton extends DefaultLineIdPanel {

	private static final long serialVersionUID = 1L;

	private JButton buttonLogin;
	private JButton buttonLogout;
	private JLabel labelUserInfo;
	private JLabel labelLogAs;
	private JPanel panelButton;
	private LoginService loginService;
	private JXLoginPane xloginPanel;
	private LoginDialog loginDialog;

	public LineIdWithAuthenticateButton() {
		EventBusService.register(this);
		initGUI();
		SecurityService.addLoginListener(new ILoginListener() {

			@Override
			public void logoutCompleted() {
				logout();
			}

			@Override
			public void loginSucceeded() {
				login();
			}
		});
	}

	private void logout() {
		showUserInfo(SecurityService.getCurrentUser());
		getButtonLogout().setVisible(false);
		getButtonLogin().setVisible(true);
	}

	private void login() {
		showUserInfo(SecurityService.getCurrentUser());
		getButtonLogin().setVisible(false);
		getButtonLogout().setVisible(true);
	}

	private void initGUI() {
		showUserInfo(SecurityService.getCurrentUser());
		add(getPanelButton(), "north");
	}

	public JButton getButtonLogin() {
		if (buttonLogin == null) {
			buttonLogin = new JButton(Messages.get("security.login"));
			buttonLogin.setName("security.login");
			buttonLogin.setFont(buttonLogin.getFont().deriveFont(10f));
			buttonLogin.addActionListener(e -> buttonLoginActionPerformed());
		}
		return buttonLogin;
	}

	public JButton getButtonLogout() {
		if (buttonLogout == null) {
			buttonLogout = new JButton(Messages.get("security.logout"));
			buttonLogout.setName("security.logout");
			buttonLogout.setVisible(false);
			buttonLogout.setFont(this.buttonLogout.getFont().deriveFont(10f));
			buttonLogout.addActionListener(e -> buttonLogoutActionPerformed());
		}
		return buttonLogout;
	}

	private void buttonLogoutActionPerformed() {
		OperatorLogger.log("User Logout");
		((MainFrame) getView()).logoutActionPerformed();
	}

	private void buttonLoginActionPerformed() {
		getXloginPanel().setUserName("");
		getXloginPanel().setPassword(new char[] {});
		getLoginDialog().setErrorMessageVisible(false);

		getLoginDialog().setVisible(true);
	}

	public LoginService getLoginService() {
		if (loginService == null) {
			loginService = new LoginService() {
				@Override
				public boolean authenticate(final String login, final char[] password, final String server)
						throws Exception {
					OperatorLogger.log("User Login: {}", login);
					SecurityService.login(login, new String(password));
					return true;
				}

				@Override
				public void cancelAuthentication() {
					super.cancelAuthentication();
					getLoginDialog().setVisible(false);
				}
			};
		}
		return loginService;
	}

	public LoginDialog getLoginDialog() {
		if (loginDialog == null) {
			getLoginService().addLoginListener(new LoginAdapter() {
				@Override
				public void loginSucceeded(final LoginEvent arg0) {
					getLoginDialog().setVisible(false);
				}
			});
			getLoginService().setSynchronous(true);
			loginDialog = new LoginDialog(getXloginPanel(), null);
			loginDialog.setLocationRelativeTo(getView().getConfigPanel());
		}
		return loginDialog;
	}

	@Subscribe
	public void handleLanguageSwitchEvent(LanguageSwitchEvent evt) {
		loginDialog = null;
		xloginPanel = null;
	}

	public JXLoginPane getXloginPanel() {
		if (xloginPanel == null) {
			xloginPanel = new JXLoginPane(getLoginService());
			xloginPanel.setBannerText(Messages.get("security.login.ask"));
			xloginPanel.setMessage(Messages.get("security.login.message.enter"));
			xloginPanel.setErrorMessage(Messages.get("security.login.failed"));
		}
		return xloginPanel;
	}

	private AbstractMachineFrame getView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof AbstractMachineFrame) {
				return (AbstractMachineFrame) f;
			}
		}
		return null;
	}

	public JLabel getLabelUserInfo() {
		if (labelUserInfo == null) {
			labelUserInfo = new JLabel("  ");
			labelUserInfo.setFont(labelUserInfo.getFont().deriveFont(10f));
		}
		return labelUserInfo;
	}

	private void showUserInfo(final User user) {
		if (user != null) {
			getLabelUserInfo().setText(user.getLogin());
		} else {
			getLabelUserInfo().setText("");
		}
	}

	public JLabel getLabelLogAs() {
		if (labelLogAs == null) {
			labelLogAs = new JLabel(Messages.get("security.logas"));
			labelLogAs.setName("security.logas");
			labelLogAs.setFont(labelLogAs.getFont().deriveFont(10f));
		}
		return labelLogAs;
	}

	public JPanel getPanelButton() {
		if (panelButton == null) {
			panelButton = new JPanel(new MigLayout("hidemode 3, inset 0 0 0 0, gap 0 0 0 0"));
			panelButton.setOpaque(false);
			panelButton.add(getButtonLogin(), "west");
			panelButton.add(getButtonLogout(), "west");
			panelButton.add(getLabelLogAs(), "gap left 15 ,west");
			panelButton.add(getLabelUserInfo(), "gap left 5 ,gap right 15,west");
		}
		return panelButton;
	}
}
