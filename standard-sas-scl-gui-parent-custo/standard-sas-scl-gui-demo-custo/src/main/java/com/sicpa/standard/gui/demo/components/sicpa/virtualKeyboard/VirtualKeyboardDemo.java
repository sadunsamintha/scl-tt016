package com.sicpa.standard.gui.demo.components.sicpa.virtualKeyboard;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel.IVirtualKeyboardButtonTask;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel.SpecialVirtualKeyboardButton;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel.VirtualKeyboardButton;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel.VirtualKeyboardEvent;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel.VirtualKeyboardListener;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class VirtualKeyboardDemo {
	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();

				final VirtualKeyboardPanel kb = createKeyboard();

				JFrame f = new JFrame();
				f.setLayout(new MigLayout());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JTextField text = new JTextField(15);
				f.getContentPane().add(text);

				addModifyKeyInput(kb, text);

				f.setSize(300, 300);
				f.setVisible(true);

				VirtualKeyboardPanel.attachKeyboardDialog(text, kb);
			}
		});
	}
	private static void addModifyKeyInput(final VirtualKeyboardPanel kb, final JTextComponent comp) {

		VirtualKeyboardListener[] list = kb.getListeners(VirtualKeyboardListener.class);
		for (VirtualKeyboardListener l : list) {
			kb.removeVirtualKeyboard(l);
		}

		kb.addVirtualKeyboardListener(new VirtualKeyboardListener() {
			@Override
			public void keyPressed(final VirtualKeyboardEvent evt) {
				if (evt.isSpecial()) {
					return;
				}
				String text = comp.getText();
				if (!text.isEmpty()) {
					String lastChar = text.charAt(text.length() - 1) + "";
					if (evt.getKey().equals(lastChar)) {
						text = comp.getText().substring(0, text.length() - 1);
						comp.setText(text + "2");
					} else {
						comp.setText(text + evt.getKey());
					}
				} else {
					comp.setText(text + evt.getKey());
				}
			}
		});
	}

	private static VirtualKeyboardPanel createKeyboard() {

		final VirtualKeyboardPanel kb = new VirtualKeyboardPanel(null);

		// custom button
		SpecialVirtualKeyboardButton kbButton = new SpecialVirtualKeyboardButton("num", "{num}", false);
		kbButton.setBackground(Color.BLUE);
		kbButton.setForeground(Color.WHITE);
		kb.addAvailableSpecialButton(kbButton);

		kb.addLayoutConstraint(" ", "span 7 ,grow");
		kb.addLayoutConstraint("{del}", "spany 2 ,grow");
		kb.addButtonsRow("1234567890");
		kb.addButtonsRow("QWERTYUIOP");
		kb.addButtonsRow("ASDFGHJKL{del}");
		kb.addButtonsRow("{capsLock}ZXCVBNM");
		kb.addButtonsRow("{ctrl}{alt} {clr}{num}");

		// alternative layout when pressing the alt key
		kb.addAlternativeLayout("alt", "+\"*�%&/()=?", "[]\\{\\},.-", "\\\\;:_/*-<>{alt}");
		
		//alternative layout when pressing the alt num key
		kb.addAlternativeLayout("num", "789", "465", "123", "0.","{num}");
		kb.addAlternativeLayoutConstraint("num", "{num}", "spanx 3 ,growx");

		// when pressing alt change the layout
		kb.addAction("{alt}", new IVirtualKeyboardButtonTask() {
			@Override
			public void buttonClicked(final VirtualKeyboardButton button, final VirtualKeyboardPanel panel) {
				if (kb.getCurrentLayout().equals("alt")) {
					kb.resetLayout();
				} else {
					kb.changeLayout("alt");
				}
			}
		});

		// when pressing num change the layout
		kb.addAction("{num}", new IVirtualKeyboardButtonTask() {
			@Override
			public void buttonClicked(final VirtualKeyboardButton button, final VirtualKeyboardPanel panel) {
				if (kb.getCurrentLayout().equals("num")) {
					kb.resetLayout();
				} else {
					kb.changeLayout("num");
				}
			}
		});

//		kb.setPreferredSize(new Dimension(100, 100));
		return kb;
	}
}
