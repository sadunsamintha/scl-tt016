package com.sicpa.standard.gui.demo.components.sicpa.propertySheet;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class PanelDemoPropertySheet extends JPanel {

	private JLabel labelName;
	private JLabel labelAdress;
	private JLabel labelPhoneNumber;
	private JLabel labelColor;

	private JTextField textName;
	private JTextField textAdress;
	private JTextField textPhoneNumber;
	private JCheckBox checkActive;
	private JPanel panelColor;

	public PanelDemoPropertySheet() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getLabelName());
		add(getTextName(), "grow,wrap");

		add(getLabelAdress());
		add(getTextAdress(), "grow,wrap");

		add(getLabelPhoneNumber());
		add(getTextPhoneNumber(), "grow,wrap");

		add(getLabelColor());
		add(getPanelColor(), "h 25!, w 25! , wrap");

		add(getCheckActive());
	}

	public JLabel getLabelAdress() {
		if (this.labelAdress == null) {
			this.labelAdress = new JLabel("Adress");
		}
		return this.labelAdress;
	}

	public JLabel getLabelName() {
		if (this.labelName == null) {
			this.labelName = new JLabel("name");
		}
		return this.labelName;
	}

	public JLabel getLabelPhoneNumber() {
		if (this.labelPhoneNumber == null) {
			this.labelPhoneNumber = new JLabel("phone number");
		}
		return this.labelPhoneNumber;
	}

	public JTextField getTextAdress() {
		if (this.textAdress == null) {
			this.textAdress = new JTextField();
			this.textAdress.setEditable(false);
		}
		return this.textAdress;
	}

	public JTextField getTextName() {
		if (this.textName == null) {
			this.textName = new JTextField();
			this.textName.setEditable(false);
		}
		return this.textName;
	}

	public JTextField getTextPhoneNumber() {
		if (this.textPhoneNumber == null) {
			this.textPhoneNumber = new JTextField();
			this.textPhoneNumber.setEditable(false);
		}
		return this.textPhoneNumber;
	}

	public JCheckBox getCheckActive() {
		if (this.checkActive == null) {
			this.checkActive = new JCheckBox("valid");
			this.checkActive.setEnabled(false);
		}
		return this.checkActive;
	}

	public void setValid(final boolean flag) {
		getCheckActive().setSelected(flag);
	}

	public void setAdress(final String text) {
		getTextAdress().setText(text);
	}

	@Override
	public void setName(final String text) {
		getTextName().setText(text);
	}

	public void setPhoneNumber(final String text) {
		getTextPhoneNumber().setText(text);
	}

	@Override
	public String getName() {
		return getTextName().getText();
	}

	public String getAdress() {
		return getTextAdress().getText();
	}

	public String getPhoneNumber() {
		return getTextPhoneNumber().getText();
	}

	@Override
	public boolean isValid() {
		return getCheckActive().isSelected();
	}

	public JPanel getPanelColor() {
		if (this.panelColor == null) {
			this.panelColor = new JPanel();
			this.panelColor.setBackground(Color.RED);
		}
		return this.panelColor;
	}

	public Color getColor() {
		return getPanelColor().getBackground();
	}

	public void setColor(final Color color) {
		getPanelColor().setBackground(color);
	}

	public JLabel getLabelColor() {
		if (this.labelColor == null) {
			this.labelColor = new JLabel("Color");
		}
		return this.labelColor;
	}
}
