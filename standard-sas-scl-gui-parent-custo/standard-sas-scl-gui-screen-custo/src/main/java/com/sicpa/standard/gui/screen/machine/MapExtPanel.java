package com.sicpa.standard.gui.screen.machine;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

public class MapExtPanel {

	private Map<PanelKey, Class<? extends JComponent>> map;

	public MapExtPanel() {
		this.map = new HashMap<PanelKey, Class<? extends JComponent>>();
	}

	public void put(final PanelKey key, final Class<? extends JComponent> clazz) {
		try {
			clazz.asSubclass(key.getClazz());
		} catch (Exception e) {
			throw new IllegalArgumentException(clazz+" should be a subsclass of "+key.getClazz());
		}
		this.map.put(key, clazz);
	}

	public Class<? extends JComponent> get(final PanelKey key) {
		return this.map.get(key);
	}
}
