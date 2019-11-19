package com.sicpa.standard.gui.components.virtualKeyboard;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel.DefaultVirtualKeyboardListener;
import com.sicpa.standard.gui.listener.CoalescentListener;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class SMVirtualKeyboardDialog extends JDialog {

	public static final String I18N_OK = GUIi18nManager.SUFFIX + "virtualKeyboard.dialog.ok";
	public static final String I18N_CANCEL = GUIi18nManager.SUFFIX + "virtualKeyboard.dialog.cancel";

	private static final long serialVersionUID = 1L;

	protected VirtualKeyboardPanel keyboard;
	protected JButton buttonOk;
	protected JButton buttonCancel;
	protected JTextComponent textField;
	protected JTextComponent passwordField;
	protected JTextComponent textTarget;
	protected JLabel labelTitle;
	protected boolean validateOnHide = false;

	public static void hideCursor(RootPaneContainer window) {
		WindowsUtils.hideCursor(window);
	}

	public static void main(final String[] args) {
		VirtualKeyboardPanel.USING_SMALL_BUTTON = true;
		VirtualKeyboardPanel.GAP_BETWEEN_BUTTON = false;
		SicpaLookAndFeelCusto.install();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				JFrame f = new JFrame();
				// hideCursor(f);

				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout());
				JTextField f1 = new JTextField(15);
				JTextField f2 = new JTextField(15);

				f.getContentPane().add(f1, "wrap");
				f.getContentPane().add(f2);

				f1.getDocument().addDocumentListener(new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent e) {
						// System.out.println("remove");
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						// System.out.println("insert");
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						// System.out.println("update");
					}
				});

				final SMVirtualKeyboardDialog d = new SMVirtualKeyboardDialog(VirtualKeyboardPanel
						.getDefaultKeyboard(null));
				d.setValidateOnHide(true);

				System.out.println(f1.getBackground());

				d.attach(f1);
				d.attach(f2);

				f.pack();
				f.setVisible(true);
			}
		});
	}

	static {
		VirtualKeyboardPanel.USING_SMALL_BUTTON = true;
	}

	public SMVirtualKeyboardDialog(final VirtualKeyboardPanel keyboard) {
		super();
		WindowsUtils.hideDecoration(this);
		this.keyboard = keyboard;
		initGUI();

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				if (validateOnHide) {
					if (validateOnNextFocusLost) {
						buttonOkActionPerformed();
					}
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setVisible(false);
					}
				});
			}
		});
	}

	public VirtualKeyboardPanel getKeyboard() {
		return this.keyboard;
	}

	private void initGUI() {
		getContentPane().setLayout(new MigLayout("fill, inset 1 1 1 1 ,hidemode 3"));
		((JPanel) getContentPane()).setBorder(new LineBorder(SicpaColor.BLUE_DARK));
		add(getLabelTitle(), "wrap");
		add(getTextField(), "span,growx");
		add(getPasswordField(), "span,growx");
		add(this.keyboard, "spanx,growx");
		add(getButtonCancel(), "spanx , split 2 , right , sg 1");
		add(getButtonOk(), "sg 1");
		setSize(220, 180);

		disableFocusOnAllComp(getContentPane());
	}

	public JLabel getLabelTitle() {
		if (this.labelTitle == null) {
			this.labelTitle = new JLabel();
		}
		return this.labelTitle;
	}

	public JButton getButtonOk() {
		if (this.buttonOk == null) {
			this.buttonOk = new JButton(GUIi18nManager.get(I18N_OK));
			this.buttonOk.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonOkActionPerformed();
				}
			});
		}
		return this.buttonOk;
	}

	protected void buttonOkActionPerformed() {
		if (this.textTarget != null) {
			this.textTarget.setText(getTextField().getText());
		}
		validateOnNextFocusLost = false;
		setText("");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(false);
			}
		});
	}

	protected boolean validateOnNextFocusLost = true;

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			validateOnNextFocusLost = true;
		}
	}

	public JButton getButtonCancel() {
		if (this.buttonCancel == null) {
			this.buttonCancel = new JButton(GUIi18nManager.get(I18N_CANCEL));
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
		validateOnNextFocusLost = false;
		setText("");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(false);
			}
		});
	}

	public JTextComponent getTextField() {
		if (this.textField == null) {
			this.textField = new JTextField();
			this.keyboard.addVirtualKeyboardListener(new DefaultVirtualKeyboardListener(this.textField));
		}
		return this.textField;
	}

	public JTextComponent getPasswordField() {
		if (this.passwordField == null) {
			this.passwordField = new JPasswordField();
			this.passwordField.setVisible(false);
			this.keyboard.addVirtualKeyboardListener(new DefaultVirtualKeyboardListener(this.passwordField));
		}
		return this.passwordField;
	}

	public void setText(final String text) {
		getTextField().setText(text);
		getPasswordField().setText(text);
	}

	public void attach(final JTextComponent textComp) {
		attach(textComp, "");
	}

	public static int MOUSE_EVENT_GROUPING_DELAY = 250;

	private class MouseCoalescentListener extends CoalescentListener implements MouseListener {

		JTextComponent textComp;
		String title;

		public MouseCoalescentListener(JTextComponent textComp, String title) {
			super(MOUSE_EVENT_GROUPING_DELAY);
			this.textComp = textComp;
			this.title = title;
		}

		@Override
		public void doAction() {
			textFieldMouseClick(textComp, title);

		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			eventReceived();
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}

	protected void textFieldMouseClick(JTextComponent textComp, String title) {

		if (textComp.isEnabled() && textComp.isEditable()) {

			if (textComp instanceof JPasswordField) {
				getPasswordField().setVisible(true);
				getTextField().setVisible(false);
			} else {
				getPasswordField().setVisible(false);
				getTextField().setVisible(true);
			}

			setText(textComp.getText());

			SMVirtualKeyboardDialog.this.textTarget = textComp;
			getLabelTitle().setText(title);

			setVisible(true);
			requestFocus();
			requestFocusInWindow();
		}

	}

	public void attach(final JTextComponent textComp, final String title) {
		textComp.addMouseListener(new MouseCoalescentListener(textComp, title));
	}

	private void disableFocusOnAllComp(final Container comp) {
		for (Component c : ((JComponent) comp).getComponents()) {
			c.setFocusable(false);
			if (c instanceof Container) {
				disableFocusOnAllComp(((Container) c));
			}
		}
	}

	public void setValidateOnHide(boolean validateOnHide) {
		this.validateOnHide = validateOnHide;
	}

	public boolean isValidateOnHide() {
		return validateOnHide;
	}
}
