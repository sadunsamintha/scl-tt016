package com.sicpa.standard.gui.plaf.popupFactory;

import java.awt.Component;

import javax.swing.Popup;
import javax.swing.PopupFactory;

public class TranslucentPopupFactory extends PopupFactory {
	@Override
	public Popup getPopup(final Component owner, final Component contents, final int x, final int y)
			throws IllegalArgumentException {
		return new TranslucentPopup(contents, x, y);
	}
}
