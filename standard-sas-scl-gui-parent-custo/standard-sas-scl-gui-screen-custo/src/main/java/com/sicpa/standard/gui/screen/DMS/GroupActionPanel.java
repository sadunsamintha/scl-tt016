package com.sicpa.standard.gui.screen.DMS;

import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.buttons.taskButton.TaskButton;
import com.sicpa.standard.gui.plaf.SicpaFont;

public class GroupActionPanel extends javax.swing.JPanel {
	private JLabel labelTitle;

	public GroupActionPanel(final String name) {
		super();
		initGUI();
		setGroupName(name);
	}

	private void initGUI() {
		setLayout(new MigLayout(""));
		add(getLabelTitle(), "top,wrap");
	}

	private JLabel getLabelTitle() {
		if (this.labelTitle == null) {
			this.labelTitle = new JLabel("group name here");
			this.labelTitle.setFont(SicpaFont.getFont(35));
		}
		return this.labelTitle;
	}

	public void setGroupName(final String name) {
		getLabelTitle().setText(name);
	}

	public GroupActionPanel addTask(final String name, String info, final AbstractAction action,
			final BufferedImage image) {
		if (info == null || info.length() == 0) {
			info = "Some info here...";
		}
		TaskButton b = new TaskButton(name, info, image);
		b.setWithShadow(false);
		b.setAction(action);

		add(b, "h 100,w 400,growx , pushx, wrap, center");

		return this;
	}
}
