package com.sicpa.standard.gui.screen.machine.component.warning;

import java.awt.Graphics2D;

import javax.swing.JList;

public interface WarningCellRendererPainter {
	public void paintCell(Graphics2D g2, Message warning, float animProgress, int index, JList list, int width,
			int height);

	public int getCellHeight(Message warning, float animProgress, int index, JList list);
}
