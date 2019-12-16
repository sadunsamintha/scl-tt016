package com.sicpa.standard.gui.demo.components.sicpa.virtualKeyboard;

import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.virtualKeyboard.SMVirtualKeyboardDialog;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SicpaMobileVirtualKeyboardDialogDemo {

	public static void main(String[] args) {

		VirtualKeyboardPanel.USING_SMALL_BUTTON = true;
		VirtualKeyboardPanel.GAP_BETWEEN_BUTTON = false;

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				SicpaLookAndFeelCusto.install();

				SicpaLookAndFeelCusto.setFontSize(10);

				JFrame f = new JFrame();
				f.setLayout(new MigLayout());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JTextComponent text = new JPasswordField(15);
				f.getContentPane().add(text);

				final VirtualKeyboardPanel kb = VirtualKeyboardPanel.getDefaultKeyboard(null);

				SMVirtualKeyboardDialog d = new SMVirtualKeyboardDialog(kb);

				d.attach(text, "Password");

				Draggable.makeDraggable(d);

				f.setSize(300, 300);
				f.setVisible(true);
			}
		});
	}
}
