package com.sicpa.standard.gui.plaf.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.plaf.basic.BasicLoginPaneUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;

public class SicpaLoginPaneUI extends BasicLoginPaneUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaLoginPaneUI((JXLoginPane) comp);
	}

	private JXLoginPane loginPane;

	public SicpaLoginPaneUI(final JXLoginPane dlg) {
		super(dlg);
		this.loginPane = dlg;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		init(this.loginPane);
	}

	boolean first = true;

	private void init(final JComponent comp) {
		comp.addHierarchyBoundsListener(new HierarchyBoundsListener() {

			@Override
			public void ancestorResized(final HierarchyEvent e) {
				if (SicpaLoginPaneUI.this.first) {
					Window w = SwingUtilities.getWindowAncestor(comp);
					if (w instanceof JDialog) {
						initHack((JComponent) ((JDialog) w).getContentPane());
						((JDialog) w).getRootPane().setDefaultButton(null);
					} else {
						initHack(comp);
					}
					SicpaLoginPaneUI.this.first = false;
				}
			}

			@Override
			public void ancestorMoved(final HierarchyEvent e) {

			}
		});

		comp.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				// thisComponentResize();
			}
		});
	}

	// private void thisComponentResize() {
	//
	// try {
	// Field f = ReflectionUtils.getField(this.loginPane.getClass(), "banner");
	// f.setAccessible(true);
	// JXImagePanel panel = (JXImagePanel) f.get(this.loginPane);
	// panel.setImage(getBanner());
	// this.loginPane.repaint();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private void initHack(final JComponent comp) {
		for (Component c : comp.getComponents()) {
			if (c instanceof JPasswordField) {
				initPasswordField((JPasswordField) c);
			} else if (c instanceof JTextField) {
				initLoginField((JTextField) c);
			} else if (c instanceof JLabel) {
				initLabel((JLabel) c);
			} else if (c instanceof JButton) {
				initButton((JButton) c);
			} else if (c instanceof JComponent) {
				initHack((JComponent) c);
			}
		}
	}

	private void initLoginField(final JTextField login) {
		VirtualKeyboardPanel.attachKeyboardDialog(login, VirtualKeyboardPanel.getDefaultKeyboard(login));
	}

	private void initPasswordField(final JPasswordField password) {
		VirtualKeyboardPanel.attachKeyboardDialog(password, VirtualKeyboardPanel.getDefaultKeyboard(password));
	}

	private void initLabel(final JLabel label) {
		if (label.getText() != null) {
			label.setText(label.getText().toUpperCase());
		}
	}

	private void initButton(final JButton button) {
		button.setFont(button.getFont().deriveFont(30f));
		button.setText(button.getText().toUpperCase());
	}

	@Override
	public Image getBanner() {
		BufferedImage res;
		res = GraphicsUtilities.createCompatibleTranslucentImage(1, 1);
		return res;
	}
}
