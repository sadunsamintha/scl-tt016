package com.sicpa.standard.gui.screen.machine.component.configPassword;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.JBusyComponent;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel.DefaultVirtualKeyboardListener;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel.MouseClickKeyboardDialogListener;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

@Deprecated
public class DefaultConfigPasswordPanel extends AbstractConfigPasswordPanel {

	private static final long serialVersionUID = 1L;
	public static final String I18N_CONFIG_PASSWORD_BUTTON_LOGIN = GUIi18nManager.SUFFIX+"configPassword.loginButton";
	public static final String I18N_CONFIG_PASSWORD_BUTTON_CANCEL = GUIi18nManager.SUFFIX+"configPassword.cancelButton";

	private JButton buttonOk;
	private JButton buttonCancel;
	private JPasswordField password;
	private JPanel mainPanel;
	private JBusyComponent<JComponent> busyPanel;

	public DefaultConfigPasswordPanel(final ConfigPasswordModel model) {
		super(model);
		initGui();
	}

	public DefaultConfigPasswordPanel() {
		this(null);
	}

	private void initGui() {
		setLayout(new BorderLayout());
		add(getBusyPanel());

	}

	public JButton getButtonCancel() {
		if (this.buttonCancel == null) {
			this.buttonCancel = new JButton(GUIi18nManager.get(I18N_CONFIG_PASSWORD_BUTTON_CANCEL));
			this.buttonCancel.setName(I18N_CONFIG_PASSWORD_BUTTON_CANCEL);
			this.buttonCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonCancelActionPerformed();
				}
			});
		}
		return this.buttonCancel;
	}

	private void buttonCancelActionPerformed() {
		getModel().cancel();
	}

	public JButton getButtonOk() {
		if (this.buttonOk == null) {
			this.buttonOk = new JButton(GUIi18nManager.get(I18N_CONFIG_PASSWORD_BUTTON_LOGIN));
			this.buttonOk.setName(I18N_CONFIG_PASSWORD_BUTTON_LOGIN);
			this.buttonOk.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonOkActionPerformed();
				}
			});
		}
		return this.buttonOk;
	}

	private void buttonOkActionPerformed() {
		setBusy(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ThreadUtils.sleepQuietly(200);
				getModel().checkPassword(new String(getPassword().getPassword()));
			}
		}).start();

	}

	private JPasswordField getPassword() {
		if (this.password == null) {
			this.password = new JPasswordField(15);
			VirtualKeyboardPanel.attachKeyboardDialog(this.password, VirtualKeyboardPanel
					.getDefaultKeyboard(this.password));
		}
		return this.password;
	}

	public void setKeyboard(final VirtualKeyboardPanel kb) {
		MouseListener[] tab = getPassword().getMouseListeners();
		for (MouseListener listener : tab) {
			if (listener instanceof MouseClickKeyboardDialogListener) {
				getPassword().removeMouseListener(listener);
			}

		}
		VirtualKeyboardPanel.attachKeyboardDialog(this.password, kb);
		kb.addVirtualKeyboardListener(new DefaultVirtualKeyboardListener(getPassword()));
	}

	private JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel(new MigLayout("fill,inset 0 0 0 0"));
			this.mainPanel.add(getPassword(), "grow,w 50%");
			this.mainPanel.add(getButtonOk(), "growx , sg 1");
			this.mainPanel.add(getButtonCancel(), "growx , sg 1");
			SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.mainPanel);
		}
		return this.mainPanel;
	}

	private JBusyComponent<JComponent> getBusyPanel() {
		if (this.busyPanel == null) {

			this.busyPanel = new JBusyComponent<JComponent>(getMainPanel());
			this.busyPanel.setName("configPasswordBusy");
		}
		return this.busyPanel;
	}

	private void setBusy(final boolean flag) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getBusyPanel().setBusy(flag);
			}
		});
	}

	@Override
	protected void modelPasswordChecked() {
		setBusy(false);
	}

	@Override
	public void setVisible(final boolean flag) {
		super.setVisible(flag);
		getPassword().setText("");
	}

	@Override
	public void grabFocus() {
		getPassword().grabFocus();
	}
}
