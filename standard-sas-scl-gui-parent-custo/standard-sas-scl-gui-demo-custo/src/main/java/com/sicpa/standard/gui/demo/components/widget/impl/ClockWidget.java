package com.sicpa.standard.gui.demo.components.widget.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.demo.components.widget.Widget;
import com.sicpa.standard.gui.utils.PaintUtils;

public class ClockWidget extends Widget {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Widget inst = new ClockWidget();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public static DateFormat dateFormat24 = new SimpleDateFormat("kk:mm");
	public static DateFormat dateFormat12 = new SimpleDateFormat("hh:mm");
	public static DateFormat df = dateFormat12;

	private JRadioButton rb12;
	private JRadioButton rb24;
	private DoneButton doneButton;

	private JPanel front;
	private JPanel back;
	private JPanel backInternal;

	public ClockWidget() {
		this.widgetSize = new Dimension(200, 65);
		initGUI();
	}

	@Override
	public JComponent getFront() {
		if (this.front == null) {
			this.front = new BackGroundPanel();
			this.front.add(new ClockPanel());
		}
		return this.front;
	}

	public JComponent getBackInternal() {
		if (this.backInternal == null) {
			this.backInternal = new JPanel(new MigLayout("fill"));

			ButtonGroup bg = new ButtonGroup();
			bg.add(getRb12());
			bg.add(getRb24());
			this.rb12.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent ae) {
					df = dateFormat12;
				}
			});
			this.rb24.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent ae) {
					df = dateFormat24;
				}
			});
			this.backInternal.add(getRb12());
			this.backInternal.add(getRb24(), "wrap");
			this.backInternal.add(getDoneButton(), "span,right");

			this.backInternal.setOpaque(false);
		}
		return this.backInternal;
	}

	public DoneButton getDoneButton() {
		if (this.doneButton == null) {
			this.doneButton = new DoneButton(getFlip());
		}
		return this.doneButton;
	}

	@Override
	public JPanel getBack() {
		if (this.back == null) {
			this.back = new BackGroundPanel();
			this.back.add(getBackInternal());
		}
		return this.back;
	}

	public JRadioButton getRb12() {
		if (this.rb12 == null) {
			this.rb12 = new JRadioButton("12 hours");
			this.rb12.setOpaque(false);
			this.rb12.setForeground(Color.WHITE);
			this.rb12.setIcon(RadioButtonIcon.INSTANCE);
			this.rb12.setFocusPainted(false);
			this.rb12.setSelected(true);
		}
		return this.rb12;
	}

	public JRadioButton getRb24() {
		if (this.rb24 == null) {
			this.rb24 = new JRadioButton("24 hours");
			this.rb24.setOpaque(false);
			this.rb24.setForeground(Color.WHITE);
			this.rb24.setIcon(RadioButtonIcon.INSTANCE);
			this.rb24.setFocusPainted(false);
		}
		return this.rb24;
	}

	private static class RadioButtonIcon implements Icon {
		private static RadioButtonIcon INSTANCE = new RadioButtonIcon();

		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			Graphics2D g2 = (Graphics2D) g;
			PaintUtils.turnOnAntialias(g2);
			g2.setColor(c.getForeground());
			g2.setStroke(new BasicStroke(1.5f));
			g2.drawOval(x, y, getIconWidth() - 1, getIconHeight() - 1);

			JRadioButton rb = (JRadioButton) c;
			if (rb.isSelected()) {
				g2.fillOval(x, y, getIconWidth() - 1, getIconHeight() - 1);
			}
		}

		public int getIconWidth() {
			return 11;
		}

		public int getIconHeight() {
			return 11;
		}
	}

	@Override
	protected void initGUI() {
		super.initGUI();
		getButtonProperties().setBounds(190, 60, 15, 15);
		getButtonClose().setBounds(5, 5, 25, 25);
	}
}
