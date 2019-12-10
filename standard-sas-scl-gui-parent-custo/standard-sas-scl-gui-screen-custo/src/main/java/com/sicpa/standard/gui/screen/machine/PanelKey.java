package com.sicpa.standard.gui.screen.machine;

import javax.swing.JComponent;

public class PanelKey {

	private Class<? extends JComponent> clazz;

	public PanelKey(final Class<? extends JComponent> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends JComponent> getClazz() {
		return this.clazz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PanelKey other = (PanelKey) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "panelKey:" + this.clazz;
	}
}
