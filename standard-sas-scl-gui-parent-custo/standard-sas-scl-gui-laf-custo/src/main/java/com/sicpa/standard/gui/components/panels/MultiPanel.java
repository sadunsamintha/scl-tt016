package com.sicpa.standard.gui.components.panels;

import java.util.ArrayList;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class MultiPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<JPanel> panels;
	private int current;

	public MultiPanel() {
		setLayout(new MigLayout("fill,hidemode 3"));
		this.panels = new ArrayList<JPanel>();
		this.current = -1;
	}

	public void setPanels(final JPanel[] panels) {
		this.panels = new ArrayList<JPanel>();
		if (panels.length > 0) {
			for (JPanel p : panels) {
				this.panels.add(p);
			}
			showPanel(0);
		}
	}

	public void showPanel(final int i) {
		removeAll();

		if (i >= 0 && i < this.panels.size()) {
			setVisible(true);
			add(this.panels.get(i), "grow");
			this.panels.get(i).setVisible(true);
			this.current = i;
		} else {
			setVisible(false);
			this.current = -1;
		}
		revalidate();
		repaint();
	}

	public void addPanel(final JPanel panel) {
		this.panels.add(panel);
	}

	public boolean isLast() {
		return this.current == this.panels.size() - 1;
	}

	public void showNext() {
		if (isLast()) {
			return;
		}
		this.current++;
		showPanel(this.current);
	}

	public boolean isfirst() {
		return this.current == 0;
	}

	public void showPrevious() {
		if (isfirst()) {
			return;
		}
		this.current--;
		showPanel(this.current);
	}
}
