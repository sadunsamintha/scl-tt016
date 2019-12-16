package com.sicpa.standard.gui.components.layeredComponents.validationOverlay;

import javax.swing.JComponent;

public interface Validator<T extends JComponent> {
	String validate(T comp);
}
