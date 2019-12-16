package com.sicpa.standard.gui.demo.components.widget.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.Timer;

import com.sicpa.standard.gui.utils.PaintUtils;

public class ClockPanel extends JComponent {
	private Font f = new Font("Arial", Font.BOLD, 65);
	private Font amf = new Font("Arial", Font.BOLD, 12);
	private DateFormat dateFormat12am = new SimpleDateFormat("a");

	public ClockPanel() {
		Timer t = new Timer(1000, new ActionListener() {
			public void actionPerformed(final ActionEvent ae) {
				repaint();
			}
		});
		t.setRepeats(true);
		t.start();
		setOpaque(false);
	}

	@Override
	protected void paintComponent(final Graphics g) {

		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);
		Date currentDate = new Date();

		g2.setFont(this.f);
		g2.setColor(Color.WHITE);
		PaintUtils.drawHighLightText(g2, ClockWidget.df.format(currentDate), 12, 50, Color.WHITE, Color.LIGHT_GRAY);

		if (ClockWidget.df == ClockWidget.dateFormat12) {
			g2.setFont(this.amf);
			g2.drawString(this.dateFormat12am.format(new Date()), getWidth() - 25, 16);
		}
	}

}
