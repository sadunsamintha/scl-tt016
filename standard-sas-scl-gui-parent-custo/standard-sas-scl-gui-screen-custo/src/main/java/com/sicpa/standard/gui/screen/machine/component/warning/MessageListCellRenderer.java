package com.sicpa.standard.gui.screen.machine.component.warning;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelConfig;

@SuppressWarnings("serial")
public class MessageListCellRenderer extends JPanel implements ListCellRenderer {
	// store the animprogress for all the warning
	private Map<Integer, Float> mapAnimWarning = new HashMap<Integer, Float>();

	private WarningCellRendererPainter warningRenderer;

	private JList currentList;
	private Message currentWarning;

	public MessageListCellRenderer() {
		SicpaLookAndFeelCusto.flagAsDefaultArea(this);
		this.setOpaque(false);
		this.setPreferredSize(new Dimension(100, 100));
		this.warningRenderer = (WarningCellRendererPainter) SicpaLookAndFeelConfig.getPCCFrameWarningPainter();
		if (this.warningRenderer == null) {
			this.warningRenderer = new DefaultWarningCellRenderer();
		}
	}

	private int index;

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		this.index = index;

		float animProgress = 1;
		if (this.mapAnimWarning.get(index + 1) != null) {
			animProgress = this.mapAnimWarning.get(index + 1);
		}

		setPreferredSize(new Dimension(0, this.warningRenderer
				.getCellHeight((Message) value, animProgress, index, list)));
		this.currentWarning = (Message) value;
		this.currentList = list;
		return this;

	}

	@Override
	protected void paintComponent(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		float animProgress = this.mapAnimWarning.get(this.index);

		this.warningRenderer.paintCell(g2, this.currentWarning, animProgress, this.index, this.currentList, getWidth(),
				getHeight());
	}

	public void setAnimProgress(final int index, final float progress) {
		this.mapAnimWarning.put(index, progress);
	}

	public Float getAnimProgress(final int index) {
		return this.mapAnimWarning.get(index);
	}
}
