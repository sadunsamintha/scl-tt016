package com.sicpa.standard.gui.components.virtualKeyboard;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;

public class VirtualKeyboardDialog extends DialogWithDropShadow {

	private static final long serialVersionUID = 1L;
	protected JComponent sourceComponent;
	protected VirtualKeyboardPanel keyboard;
	protected JScrollPane scroll;

	public VirtualKeyboardDialog() {
		super(null);
		setWithCloseButton(true);
		setAlwaysOnTop(true);
		setResizable(false);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		initGUI();
		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(final WindowEvent e) {
				setVisible(false);
				VirtualKeyboardDialog.this.keyboard.resetLayout();
			}
		});
	}

	private void initGUI() {
		setLayout(new MigLayout("inset 0 0 0 0, fill"));
		this.scroll = new JScrollPane();
		this.scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(this.scroll, "grow,gapbottom 10");
		setSize(570, 200);
	}

	protected void setOwner(final VirtualKeyboardPanel kb, final JComponent comp) {
		this.scroll.setViewportView(kb);
		this.sourceComponent = comp;
		this.keyboard = kb;
		pack();
	}

	@Override
	public void startHideAnim() {
		setVisible(false);
	}

	protected Point computePosition() {
		int h = getHeight();

		int xComp = this.sourceComponent.getLocationOnScreen().x;
		int yComp = this.sourceComponent.getLocationOnScreen().y;

		int xmax = 0;
		try {
			for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				GraphicsConfiguration[] conf = gd.getConfigurations();
				if (conf.length > 0) {
					xmax += conf[0].getBounds().width;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			xmax = 0;
		}
		if (xmax <= 0) {
			xmax = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
		}

		int x, y;

		if (yComp - h >= 0) {
			y = yComp - h;
		} else {
			y = yComp + this.sourceComponent.getHeight();
		}

		int deltax = xmax - (xComp + this.sourceComponent.getWidth());
		if (deltax > 0) {
			x = xComp;
		} else {
			x = xComp + deltax;
		}
		return new Point(x, y);
	}
}