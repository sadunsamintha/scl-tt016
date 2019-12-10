package com.sicpa.standard.gui.components.joptionPane;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sicpa.standard.gui.components.windows.WindowFadeInManager;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class AutoCloseOptionPane {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				showMessage(null, "Operation successful", "Success", 5, JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	public static void showMessage(final Component comp, final Object message, final String title, final int seconde) {
		showMessage(comp, message, title, seconde, "OK", "sec", JOptionPane.INFORMATION_MESSAGE, false);
	}

	public static void showMessage(final Component comp, final Object message, final String title, final int seconde,
			final int messageType) {
		showMessage(comp, message, title, seconde, "OK", "sec", messageType, false);
	}

	public static void showMessage(final Component comp, final Object message, final String title, final int seconde,
			final String okButtonText, final String secondeText, final int messageType, final boolean fadein) {
		final int[] remainingSeconde = new int[] { seconde };
		final JButton[] b = new JButton[1];
		b[0] = new JButton(okButtonText + " (" + (remainingSeconde[0]) + secondeText + ")");
		JOptionPane p = new JOptionPane(message, messageType, JOptionPane.YES_OPTION, null, b);
		final JDialog d = p.createDialog(title);

		final Timer[] t = new Timer[1];
		t[0] = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				remainingSeconde[0]--;
				if (remainingSeconde[0] < 0) {
					t[0].stop();
					if (fadein) {
						WindowFadeInManager.fadeOut(d);
					} else {
						d.setVisible(false);
						d.dispose();
					}

				}
				b[0].setText(okButtonText + " (" + (remainingSeconde[0]) + secondeText + ")");
			}
		});

		b[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				t[0].stop();
				if (fadein) {
					WindowFadeInManager.fadeOut(d);
				} else {
					d.setVisible(false);
					d.dispose();
				}
			}
		});

		t[0].start();
		if (fadein) {
			WindowFadeInManager.fadeIn(d);
		} else {
			d.setVisible(true);
		}
	}
}
