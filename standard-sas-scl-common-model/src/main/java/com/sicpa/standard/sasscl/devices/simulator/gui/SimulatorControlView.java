package com.sicpa.standard.sasscl.devices.simulator.gui;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class SimulatorControlView {

	protected boolean visible = true;
	protected JFrame frame;
	protected JTabbedPane tab;

	public void addSimulator(final String name, final JPanel panel) {
		if (!visible) {
			return;
		}
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				addTab(name, panel);
			}

		});
	}

	protected void addTab(final String name, final JPanel panel) {
		for (int i = 0; i < getTab().getTabCount(); i++) {
			if (getTab().getTitleAt(i).equals(name)) {
				// if already added do nothing
				return;
			}
		}
		if (getTab().getTabCount() == 0) {
			try {
				if (!AppUtils.isHeadless()) {
					getFrame().setVisible(true);
				}
			} catch (HeadlessException e) {
			}
		}
		getTab().addTab(name, panel);
	}

	public void removeSimulator(final String name) {
		if (!visible) {
			return;
		}
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				remove(name);
			}
		});
	}

	protected synchronized void remove(final String name) {
		for (int i = 0; i < getTab().getTabCount(); i++) {
			if (getTab().getTitleAt(i).equals(name)) {
				getTab().removeTabAt(i);
				if (getTab().getTabCount() == 0) {
					try {
						getFrame().setVisible(false);
					} catch (HeadlessException e) {
					}
				}
				return;
			}
		}
	}

	public JTabbedPane getTab() {
		if (tab == null) {
			tab = new JTabbedPane();
		}
		return this.tab;
	}

	public JFrame getFrame() {
		if (frame == null) {
			frame = new JFrame();
			frame.getContentPane().add(getTab());
			frame.setSize(650, 450);
		}
		return frame;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}
}
