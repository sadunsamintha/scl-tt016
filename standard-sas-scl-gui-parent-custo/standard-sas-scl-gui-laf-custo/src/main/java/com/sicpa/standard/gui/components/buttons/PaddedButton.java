package com.sicpa.standard.gui.components.buttons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelConfig;
import com.sicpa.standard.gui.utils.PaintUtils;

public class PaddedButton extends JPanel {
	protected static final long serialVersionUID = 1L;
	protected AbstractButton button;

	protected int hpadding;
	protected int vpadding;
	protected int cornerArc;
	protected Color enabledColor;
	protected Color disabledColor;

	public PaddedButton(final AbstractButton button) {
		this.hpadding = this.vpadding = SicpaLookAndFeelConfig.getPaddedButtonBorderWidth();
		this.cornerArc = SicpaLookAndFeelConfig.getPaddedButtonCornerArc();
		this.button = button;
		enabledColor = SicpaLookAndFeelConfig.getPaddedButtonEnabledColor();
		disabledColor = SicpaLookAndFeelConfig.getPaddedButtonDisabledColor();
		setOpaque(false);
		button.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				buttonStateChange();
			}
		});

		button.addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(final HierarchyEvent e) {
				setVisible(button.isVisible());
			}
		});
		initGUI();
	}

	@Override
	public void setVisible(final boolean flag) {
		super.setVisible(flag);
		this.button.setVisible(flag);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.button.setEnabled(enabled);
	}

	protected void buttonStateChange() {
		repaint();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill"));

		add(Box.createHorizontalStrut(this.hpadding), "east");
		add(Box.createHorizontalStrut(this.hpadding), "west");

		add(Box.createVerticalStrut(this.vpadding), "north");
		add(Box.createVerticalStrut(this.vpadding), "south");

		add(this.button, "grow");
	}

	@Override
	protected void paintComponent(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);

		Insets inset = getInsets();
		int w = getWidth() - inset.left - inset.right;
		int h = getHeight() - inset.top - inset.bottom;

		if (this.button.isEnabled()) {
			g2.setColor(enabledColor);
		} else {
			g2.setColor(disabledColor);
		}
		g2.fillRoundRect(0, 0, w, h, this.cornerArc, this.cornerArc);
		g2.dispose();
	}

	public AbstractButton getButton() {
		return this.button;
	}

	public void setEnabledColor(Color enabledColor) {
		this.enabledColor = enabledColor;
	}

	public void setDisabledColor(Color disabledColor) {
		this.disabledColor = disabledColor;
	}
}
