package com.sicpa.standard.gui.components.dialog.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginService;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.text.filter.SizeDocumentFilter;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class LoginDialog extends JDialog {

	protected static final long serialVersionUID = 1L;
	public static final String I18N_LOGIN = GUIi18nManager.SUFFIX + "login.loginButton";
	public static final String I18N_CANCEL = GUIi18nManager.SUFFIX + "login.cancelButton";

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				final JXLoginPane panel = new JXLoginPane();
				panel.setBannerText("PLEASE LOGIN");
				panel.setMessage("ENTER YOU USER NAME/PASSWORD");

				LoginService login = new LoginService() {
					@Override
					public boolean authenticate(final String arg0, final char[] arg1, final String arg2)
							throws Exception {
						throw new Exception("authentification error");
					}
				};

				// show a message when the login fail
				login.addLoginListener(new LoginAdapter() {
					@Override
					public void loginFailed(final LoginEvent source) {
						panel.setErrorMessage(source.getCause().getMessage());
					}
				});

				final LoginDialog d = LoginDialog.showLoginDialog(panel, login, null);
				// add a label somewhere
				d.getMainPanel().add(new JLabel("some label"), "pos 20 visual.y+60");

				// set the error message visible
				d.setErrorMessageVisible(true);

				// change the error message over time
				// new Thread(new Runnable() {
				// @Override
				// public void run() {
				// while (d.isVisible()) {
				// SwingUtilities.invokeLater(new Runnable() {
				// @Override
				// public void run() {
				// panel.setErrorMessage("error:" + System.currentTimeMillis());
				// }
				// });
				// ThreadUtils.sleepQuietly(100);
				// }
				// }
				// }).start();
			}
		});
	}

	public static LoginDialog showLoginDialog(final JXLoginPane pane, final LoginService login, final JFrame parent) {
		final LoginDialog d = new LoginDialog(pane, parent);
		d.setLocationRelativeTo(null);
		pane.setLoginService(login);
		login.addLoginListener(new LoginAdapter() {
			@Override
			public void loginSucceeded(final LoginEvent source) {
				d.setVisible(false);
			}
		});
		d.setVisible(true);
		return d;
	}

	public static void showLoginDialog(final LoginService login) {
		JXLoginPane pane = new JXLoginPane();
		showLoginDialog(pane, login, null);
	}

	protected JXLoginPane loginPane;
	protected JButton buttonLogin;
	protected JPanel banner;
	protected JPanel mainPanel;
	protected JButton buttonCancel;

	public LoginDialog(final JXLoginPane loginPane, final JFrame parent) {
		super(parent);
		setModal(parent != null);
		this.loginPane = loginPane;
		initGUI();
	}

	protected void initGUI() {
		WindowsUtils.setOpaque(this, false);
		Draggable.makeDraggable(this);
		WindowsUtils.hideDecoration(this);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getMainPanel());
		getMainPanel().setLayout(new MigLayout("fill, inset 0 0 0 0"));
		getMainPanel().add(getBanner(), "span,h 100!,grow");
		getMainPanel().add(this.loginPane, "grow,push,span");
		getMainPanel().add(getButtonCancel(), "right,gap right 10 , gap bottom 5,split 2, sg 1 ");
		getMainPanel().add(getButtonLogin(), "sg 1,gap right 10");

		setSize(550, 420);

		initActionOnTextField(this.loginPane);

	}

	protected void initActionOnTextField(final JComponent root) {
		for (final Component comp : root.getComponents()) {
			if (comp instanceof JTextField) {
				initATextField((JTextField) comp);
			} else if (comp instanceof JComponent) {
				initActionOnTextField((JComponent) comp);
			}
		}
	}

	protected void initATextField(final JTextField textField) {
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				textFieldActionPerformed(textField);
			}
		});
		new SizeDocumentFilter(50).installFilter(textField);
	}

	protected void textFieldActionPerformed(final JTextField text) {
		getButtonLogin().doClick();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				text.grabFocus();
			}
		});
	}

	public JButton getButtonLogin() {
		if (this.buttonLogin == null) {
			this.buttonLogin = new JButton();
			this.buttonLogin.setAction(this.loginPane.getActionMap().get(JXLoginPane.LOGIN_ACTION_COMMAND));
			this.buttonLogin.setText(GUIi18nManager.get(I18N_LOGIN));
			this.buttonLogin.setName(I18N_LOGIN);
		}
		return this.buttonLogin;
	}

	public JButton getButtonCancel() {
		if (this.buttonCancel == null) {
			this.buttonCancel = new JButton(GUIi18nManager.get(I18N_CANCEL));
			this.buttonCancel.setName(I18N_CANCEL);
			this.buttonCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonCancelActionPerformed();
				}
			});
		}
		return this.buttonCancel;
	}

	protected void buttonCancelActionPerformed() {
		this.loginPane.getLoginService().cancelAuthentication();
		setVisible(false);
		dispose();
	}

	@SuppressWarnings("serial")
	public JPanel getBanner() {
		if (this.banner == null) {
			this.banner = new JPanel() {
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);

					Graphics2D g2 = (Graphics2D) g;

					g.setFont(g.getFont().deriveFont(35f));
					String bannerText = LoginDialog.this.loginPane.getBannerText();
					if (bannerText == null) {
						bannerText = "LOGIN";
					}
					bannerText = bannerText.toUpperCase();
//					PaintUtils.drawHighLightText(g2, bannerText, 10, 50, SicpaColor.BLUE_DARK,
//							SicpaColor.BLUE_ULTRA_LIGHT);
					PaintUtils.drawHighLightText(g2, bannerText, 10, 50, SicpaColor.BLUE_DARK,
							SicpaColor.CLOUD_GREY);
				}
			};
			this.banner.setOpaque(false);
		}
		return this.banner;
	}

	@SuppressWarnings("serial")
	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel() {
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);
					Graphics2D g2 = (Graphics2D) g;
					PaintUtils.turnOnAntialias(g2);

					Point start = new Point(0, 0);
					Point end = new Point(0, getBanner().getHeight());
//					Color c1 = SicpaColor.BLUE_LIGHT;
//					Color c2 = SicpaColor.BLUE_ULTRA_LIGHT;
					Color c1 = SicpaColor.CLOUD_GREY;
					Color c2 = SicpaColor.CLOUD_GREY;
					GradientPaint paint = new GradientPaint(start, c1, end, c2);
					g2.setPaint(paint);
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
					g.setColor(SicpaColor.BLUE_DARK);
					g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
				}
			};
			this.mainPanel.setOpaque(false);
		}
		return this.mainPanel;
	}

	public void setErrorMessageVisible(final boolean visible) {
		try {
			Field f = this.loginPane.getClass().getDeclaredField("errorMessageLabel");
			f.setAccessible(true);
			JXLabel label = (JXLabel) f.get(this.loginPane);
			label.setVisible(visible);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
