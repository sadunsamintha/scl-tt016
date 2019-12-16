package com.sicpa.standard.gui.components.virtualKeyboard;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.LocaleUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.Dialog.ModalExclusionType;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class SpinnerNumericVirtualKeyboard {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				System.setProperty("local.language","fr");

				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JPanel dp = new JPanel();
				dp.setSize(200,200);
				JSpinner dspinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 99999.0, 1.0));
				JPanel bp = new JPanel();
				bp.setSize(200,200);
				JSpinner bspinner = new JSpinner(new SpinnerNumberModel(new Byte((byte) 0), new Byte((byte) 0), new Byte(Byte.MAX_VALUE), new Byte((byte) 1)));
				JPanel ip = new JPanel();
				ip.setSize(200,200);
				JSpinner ispinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1));
				dp.add(new Label("Double Spinner"));
				dp.add(dspinner);
				f.getContentPane().add(dp);
				dp.add(new Label("Byte Spinner"));
				dp.add(bspinner);
				f.getContentPane().add(bp);
				ip.add(new Label("Integer Spinner"));
				ip.add(ispinner);
				f.getContentPane().add(ip);
				f.setSize(200, 800);
				f.setVisible(true);
			}
		});
	}

	public static final String I18N_CANCEL = GUIi18nManager.SUFFIX + "spinner.virtualKeyboard.cancel";
	public static final String I18N_OK = GUIi18nManager.SUFFIX + "spinner.virtualKeyboard.ok";
	public static final String I18N_CLEAR = GUIi18nManager.SUFFIX + "spinner.virtualKeyboard.clear";
	private static final String DEFAULT_LANG = "en";
	private static final int MAX_KEYPAD_SIZE = 10;

	private JButton numericButtons[];
	private JButton clearButton;
	private JButton decimalPointButton;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel numberPanel;
	private JDialog dialog;
	private static SpinnerNumericVirtualKeyboard INSTANCE;
	private JTextComponent component;
	private String oldValue;
	private String localDecimalPoint = ".";
	private int maxValue = Integer.MAX_VALUE;
	private int maxNumericButton = 10;
	private boolean firstKeyOnKeyboardVisible = false;

	private static ISpinnerDialogCallback callback;

	private SpinnerNumericVirtualKeyboard() {
		setDecimalPointFromLocale();
	}


	public static void setCallback(ISpinnerDialogCallback _callback) {
		getKeyboard();
		callback = _callback;
	}

	/**
	 * Destroy static instance so that application can create new instance. This is neccessary
	 * for changes in locale settings from utililising applications.
     */
	public static void destroyInstance(){
		INSTANCE = null;
	}

	public static SpinnerNumericVirtualKeyboard getKeyboard() {
		if (INSTANCE == null) {
		   INSTANCE = new SpinnerNumericVirtualKeyboard();
		}
		return INSTANCE;
	}

	public void setVisible(final JTextField component,SpinnerNumberModel model) {
		setVisible(component,model.getMaximum(), true,isFloatingPointDataType(model));
	}

	private boolean isFloatingPointDataType(SpinnerModel model) {
		if (model.getValue() instanceof Float || model .getValue() instanceof Double){
			return true;
		}
		return false;
	}

	private int getMaxValue(Comparable value) {
		int tmp = MAX_KEYPAD_SIZE;
		if (value instanceof Double){
			tmp = (int)Double.parseDouble(value.toString());
		}
		if (value instanceof Float){
			tmp = (int)Float.parseFloat(value.toString());
		}
		if (value instanceof Long){
			tmp = (int)Long.parseLong(value.toString());
		}
		if (value instanceof Integer){
			tmp = (int)Integer.parseInt(value.toString());
		}
		if (value instanceof Short){
			tmp = (int)Short.parseShort(value.toString());
		}
		if (value instanceof Byte){
			tmp = Byte.MAX_VALUE;
		}
		return tmp;

	}


	public void setVisible(final JTextComponent texComp, final Comparable maxValue, final boolean visible,boolean decimalInputAccepted) {
		if (texComp == null) {
			throw new IllegalArgumentException("JTextComponent_is_null");
		}

		if (visible) {
			this.firstKeyOnKeyboardVisible = true;
			this.component = texComp;
			this.oldValue = this.component.getText();
			this.maxValue = getMaxValue(maxValue);
			this.maxNumericButton = this.maxValue < MAX_KEYPAD_SIZE ? this.maxValue : MAX_KEYPAD_SIZE;
			Point p = new Point(this.component.getX(), this.component.getY());
			SwingUtilities.convertPointToScreen(p, this.component);
			p.x += this.component.getWidth();
			p.y -= (getDialog().getHeight() - this.component.getHeight()) / 2;
			getDialog().setLocation(p);
			getDialog().requestFocus();
			getDialog().requestFocusInWindow();
			this.getDecimalPointButton().setEnabled(decimalInputAccepted);

		}
		getDialog().setVisible(visible);
	}

	private JButton getOkButton() {
		if (this.okButton == null) {
			this.okButton = new JButton(GUIi18nManager.get(I18N_OK));
			this.okButton.setName(I18N_OK);
			this.okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					okButtonActionPerformed();
				}
			});
		}
		return this.okButton;
	}

	private void fireActionPerformed() {
		if (!callback.accept()) {
			cancelButtonActionPerformed();
		}
	}

	private void okButtonActionPerformed() {
		fireActionPerformed();
		getDialog().setVisible(false);
	}

	private JButton getCancelButton() {
		if (this.cancelButton == null) {
			this.cancelButton = new JButton(GUIi18nManager.get(I18N_CANCEL));
			this.cancelButton.setName(I18N_CANCEL);
			this.cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					cancelButtonActionPerformed();
				}
			});
		}
		return this.cancelButton;
	}

	private void cancelButtonActionPerformed() {
		this.component.setText(this.oldValue);
		getDialog().setVisible(false);
	}

	private JButton getClearButton() {
		if (this.clearButton == null) {
			this.clearButton = new JButton(GUIi18nManager.get(I18N_CLEAR));
			this.clearButton.setName(I18N_CLEAR);
			this.clearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					clearButtonActionPerformed();
				}
			});
		}
		return this.clearButton;
	}

	private void clearButtonActionPerformed() {
		this.component.setText("");
	}

	private JButton getDecimalPointButton() {

		if (this.decimalPointButton == null) {
			this.decimalPointButton = new JButton(localDecimalPoint);
			this.decimalPointButton.setName("decimalButton");
			this.decimalPointButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					decimalPointButtonActionPerformed();
				}
			});
		}
		return this.decimalPointButton;
	}

	private Locale getLocale() {
		String jvmLang = Optional.ofNullable(System.getProperty("local.language")).orElse(DEFAULT_LANG);
		return LocaleUtils.availableLocaleList().stream()
				.filter(l -> l.getLanguage().equals(jvmLang) && l.getCountry().equals(""))
				.findFirst().orElse(Locale.getDefault());

	}

	private void setDecimalPointFromLocale() {
		localDecimalPoint = Character.toString(((DecimalFormat)NumberFormat.getInstance(getLocale())).getDecimalFormatSymbols().getDecimalSeparator());
	}

	private void decimalPointButtonActionPerformed() {
		String currentText = this.component.getText();
		if (!currentText.contains(localDecimalPoint)) {
			currentText = currentText + localDecimalPoint;
			this.component.setText(currentText);
		}
	}

	private JButton[] getNumericButtons() {
		if (this.numericButtons == null) {
			this.numericButtons = new JButton[MAX_KEYPAD_SIZE];

			for (int i = 0; i < MAX_KEYPAD_SIZE; i++) {
				this.numericButtons[i] = new JButton();
				this.numericButtons[i].setName("" + i);
				this.numericButtons[i].setText(String.valueOf(i));
				this.numericButtons[i].setEnabled(i > maxNumericButton ? false:true);
				this.numericButtons[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						numericButtonsActionPerformed(e);
					}
				});
			}
		}

		return this.numericButtons;
	}

	private void numericButtonsActionPerformed(final ActionEvent event) {
		String currentText = this.firstKeyOnKeyboardVisible ? "" : this.component.getText();
		this.firstKeyOnKeyboardVisible = false;
		currentText = currentText.equals(localDecimalPoint) ? "" : currentText;
		String textFromEvent = ((JButton) event.getSource()).getText();
		String textCandidate = currentText + textFromEvent;
		if (textCandidate.length() > 1 && textCandidate.charAt(0) == '0'){
			if (!(textCandidate.charAt(1) == localDecimalPoint.charAt(0))){
				textCandidate = textCandidate.substring(1,textCandidate.length());
			}
		}

		this.component.setText(textCandidate);

	}

	private JPanel getNumberPanel() {
		if (this.numberPanel == null) {
			this.numberPanel = new JPanel(new MigLayout("fill,wrap 3", "5[]", "5[]"));

			for (int i = 1; i < 10; i++) {
				this.numberPanel.add(getNumericButtons()[i], "grow,h 50 , w 50, sizegroup 1");
			}
			this.numberPanel.add(getDecimalPointButton(), "grow, sizegroup 1");
			this.numberPanel.add(getNumericButtons()[0], "grow, sizegroup 1");
			this.numberPanel.add(getClearButton(), "grow, sizegroup 1");
			this.numberPanel.add(getOkButton(), "grow, sizegroup 1");
			this.numberPanel.add(getMinusButton(), "grow, sizegroup 1");
			this.numberPanel.add(getCancelButton(), "grow,sizegroup 1");

			SicpaLookAndFeelCusto.flagAsDefaultArea(this.numberPanel);
		}
		return this.numberPanel;
	}

	private JButton minusButton;

	private static boolean minusVisible = false;

	public static void setMinusVisible(final boolean visible) {
		minusVisible = visible;
		if (INSTANCE != null) {
			INSTANCE.getMinusButton().setVisible(visible);
		}
	}

	public JButton getMinusButton() {
		if (this.minusButton == null) {
			this.minusButton = new JButton("-/+");
			this.minusButton.setVisible(minusVisible);
			this.minusButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					minusButtonActionPerformed();
				}
			});
		}
		return this.minusButton;
	}

	private void minusButtonActionPerformed() {
		String currentText = this.component.getText();
		if (currentText.startsWith("-")) {
			currentText = currentText.substring(1);
		} else {
			currentText = "-" + currentText;
		}
		this.component.setText(currentText);
	}

	private JDialog getDialog() {
		if (this.dialog == null) {
			this.dialog = new JDialog();
			this.dialog.setAlwaysOnTop(true);
			this.dialog.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);

			Draggable.makeDraggable(this.dialog);
			this.dialog.add(getNumberPanel());
			this.dialog.pack();
			this.dialog.setSize(this.dialog.getWidth(), 400);
			this.dialog.addWindowFocusListener(new WindowAdapter() {
				@Override
				public void windowLostFocus(final WindowEvent e) {
					dialogWindowLostFocus();
				}
			});
		}
		return this.dialog;
	}

	private void dialogWindowLostFocus() {
		fireActionPerformed();
		getDialog().setVisible(false);
	}

}