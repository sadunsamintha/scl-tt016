package com.sicpa.standard.gui.components.layeredComponents.oldValue;

public class DefaultImplOldValueFormater implements OldValueFormater {
	@Override
	public String getFormatedValue(final Object oldValue) {
		if (oldValue == null) {
			return "";
		} else {
			return oldValue.toString();
		}

	}
}
