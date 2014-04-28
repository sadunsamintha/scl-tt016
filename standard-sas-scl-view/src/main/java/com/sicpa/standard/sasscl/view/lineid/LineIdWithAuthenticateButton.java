package com.sicpa.standard.sasscl.view.lineid;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.dialog.login.LoginDialog;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.lineId.DefaultLineIdPanel;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.event.LoginAttemptEvent;
import com.sicpa.standard.sasscl.event.PlcLoginEvent;
import com.sicpa.standard.sasscl.event.PrinterProfileEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.security.UserId;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.MainFrame;

public class LineIdWithAuthenticateButton extends DefaultLineIdPanel {

	private static final Logger logger = LoggerFactory.getLogger(LineIdWithAuthenticateButton.class);

	private static final long serialVersionUID = 1L;

	private static final long SESSION_TIMEOUT_SEC = 300;
	
	private static final int PLC_AUTH_TIMEOUT_MILLIS = 20000;

	private JButton buttonLogin;
	private JButton buttonLogout;
	private JLabel labelUserInfo;
	private JLabel labelLogAs;
	private JPanel panelButton;

	LoginDialog loginDialog;

	private boolean fingerPrintLogin;
	
	ScheduledExecutorService timeoutExecutor;

	protected LoginService loginService;
	protected JXLoginPane xloginPanel;

	private PlcLoginEvent plcAuth;

	private boolean authenticationReceived;

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

	protected void logout() {
		resetTimeout();
		
		showUserInfo(SecurityService.getCurrentUser());
		getView().getConfigPanel().setPanelButtonsVisible(false);
		getButtonLogout().setVisible(false);
		getButtonLogin().setVisible(!fingerPrintLogin);
	}

	protected void login() {
		
		if (plcAuth == null) {
			showUserInfo(SecurityService.getCurrentUser());
		}
		else {
			showUserIdInfo(plcAuth.getUserId());
		}
		getView().getConfigPanel().setPanelButtonsVisible(true);
		getButtonLogin().setVisible(false);
		getButtonLogout().setVisible(true);
		
		scheduleTimeout();
	}

	private void scheduleTimeout() {		
		resetTimeout();
		
		timeoutExecutor = Executors.newSingleThreadScheduledExecutor();
		timeoutExecutor.schedule(new Runnable() {
			
			@Override
			public void run() {
				doLogout();
			}

			
		}, SESSION_TIMEOUT_SEC, TimeUnit.SECONDS);
	}
	
	private void doLogout() {
		SwingUtilities.invokeLater(new Runnable() {					
			@Override
			public void run() {
				logoutAction();
			}
		});
	}

	private void resetTimeout() {
		if (timeoutExecutor != null) {
			timeoutExecutor.shutdown();
		}
	}

	private void initGUI() {

		showUserInfo(SecurityService.getCurrentUser());
		add(getPanelButton(), "north");
	}

	public JButton getButtonLogin() {
		if (this.buttonLogin == null) {
			this.buttonLogin = new JButton(Messages.get("security.login"));
			this.buttonLogin.setName("security.login");
			this.buttonLogin.setFont(this.buttonLogin.getFont().deriveFont(10f));
			this.buttonLogin.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonLoginActionPerformed();
				}
			});
		}
		return this.buttonLogin;
	}

	public JButton getButtonLogout() {
		if (this.buttonLogout == null) {
			this.buttonLogout = new JButton(Messages.get("security.logout"));
			this.buttonLogout.setName("security.logout");
			this.buttonLogout.setVisible(false);
			this.buttonLogout.setFont(this.buttonLogout.getFont().deriveFont(10f));
			this.buttonLogout.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					logoutAction();
				}
			});
		}
		return this.buttonLogout;
	}

	protected void logoutAction() {
		OperatorLogger.log("User Logout");
		((MainFrame) getView()).logoutActionPerformed();
		EventBusService.post(new PrinterProfileEvent(1));
	}

	protected void buttonLoginActionPerformed() {
		getXloginPanel().setUserName("");
		getXloginPanel().setPassword(new char[] {});

		getLoginDialog().setVisible(true);
	}

	public LoginService getLoginService() {
		if (loginService == null) {
			loginService = new LoginService() {
				@Override
				public boolean authenticate(final String login, final char[] password, final String server)
						throws Exception {					
					OperatorLogger.log("User Login: {}", login);
					
					try {
						SecurityService.login(login, new String(password));
						return true;
					} catch (LoginException e) {
						logger.error("Login error", e);
						return false;
					}
				}

				@Override
				public void cancelAuthentication() {
					super.cancelAuthentication();
					getLoginDialog().setVisible(false);
				}
				
				@Override
				public void startAuthentication(String user, final char[] password, final String server) throws Exception {
					final String login;
					if (!fingerPrintLogin && !authenticateOnPlc(user, new String(password))){
						getXloginPanel().setErrorMessage("PLC authentication failed");
						login = null;
					}
					else {
						login = plcAuth.getAppUserProfile().getLogin();
					}
					
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							doAuthenticate(login, password, server);							
						}
					});
				}
				
				void doAuthenticate(String login, char[] password, String server) {
					try {
						super.startAuthentication(login, password, server);
					} catch (Exception e) {
						logger.error("Login error", e);
					}
				}
			};
		}
		return loginService;
	}
	

	/**
	 * Blocking call for PLC authentication
	 * @param login
	 * @param password 
	 * @param password
	 * @return
	 */
	private boolean authenticateOnPlc(final String login, final String password) {
		
		new Thread(new Runnable() {			
			@Override
			public void run() {
				EventBusService.post(new LoginAttemptEvent(login, password));
			}
		}).start();
		
		return (waitAuthentication() != null && plcAuth.getUserId()!= null 
				&& plcAuth.getUserId().getLogin().equals(login));
	}
	
	/**
	 * Waits for PLC authentication
	 * @return
	 */
	private PlcLoginEvent waitAuthentication() {
		plcAuth = null;
		authenticationReceived = false;
		for (int i=0; i*100 < PLC_AUTH_TIMEOUT_MILLIS; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				return null;
			}
			if (authenticationReceived) {
				return plcAuth;
			}
		}
		return null;
	}
	
	/**
	 * Receives the PLC authentication
	 * @param event
	 */
	@Subscribe
	public void onPlcAuthenticated(PlcLoginEvent event) {

		plcAuth = event;
		authenticationReceived = true;
		String login = null;
		try {
			if (fingerPrintLogin) {
				login = plcAuth.getAppUserProfile().getLogin();
				getLoginService().startAuthentication(login, plcAuth.getAppUserProfile().getPassword().toCharArray(), null);
			}
		} catch (Exception e) {
			EventBusService.post(new MessageEvent(MessageEventKey.PLC.UNABLE_AUTHENTICATE_USER));
			logger.error("Login failed for {}", login);
		}
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
		this.loginDialog = null;
		this.xloginPanel = null;
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

	protected AbstractMachineFrame getView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof AbstractMachineFrame) {
				return (AbstractMachineFrame) f;
			}
		}
		return null;
	}

	public JLabel getLabelUserInfo() {
		if (this.labelUserInfo == null) {
			this.labelUserInfo = new JLabel("  ");
			this.labelUserInfo.setFont(this.labelUserInfo.getFont().deriveFont(10f));
		}
		return this.labelUserInfo;
	}

	protected void showUserInfo(final User user) {
		if (user != null) {
			getLabelUserInfo().setText(user.getLogin());
		} else {
			getLabelUserInfo().setText("");
		}
	}
	


	private void showUserIdInfo(UserId userId) {
		if (userId != null) {
			getLabelUserInfo().setText(userId.getSurname() + ", " + userId.getFirstname());
		} else {
			getLabelUserInfo().setText("");
		}
	}

	public JLabel getLabelLogAs() {
		if (this.labelLogAs == null) {
			this.labelLogAs = new JLabel(Messages.get("security.logas"));
			this.labelLogAs.setName("security.logas");
			this.labelLogAs.setFont(this.labelLogAs.getFont().deriveFont(10f));
		}
		return this.labelLogAs;
	}

	public JPanel getPanelButton() {
		if (this.panelButton == null) {
			this.panelButton = new JPanel(new MigLayout("hidemode 3, inset 0 0 0 0, gap 0 0 0 0"));
			this.panelButton.setOpaque(false);
			this.panelButton.add(getButtonLogin(), "west");
			this.panelButton.add(getButtonLogout(), "west");
			this.panelButton.add(getLabelLogAs(), "gap left 15 ,west");
			this.panelButton.add(getLabelUserInfo(), "gap left 5 ,gap right 15,west");
		}
		return this.panelButton;
	}

	public void setFingerPrintLogin(boolean fingerPrintLogin) {
		this.fingerPrintLogin = fingerPrintLogin;
		getButtonLogin().setVisible(!fingerPrintLogin);
	}
}
