package com.sicpa.standard.gui.components.virtualKeyboard;

import javax.swing.text.JTextComponent;

public abstract class VirtualKeyboardType {
	public static final VirtualKeyboardType KEYBOARD_ALPHA = new VirtualKeyboardType() {

		@Override
		public VirtualKeyboardPanel getKeyboard(final JTextComponent textComp) {
			return VirtualKeyboardPanel.getAlphaSortedKeyboard(textComp);
		}
	};
	public static final VirtualKeyboardType KEYBOARD_QWERTZ = new VirtualKeyboardType() {

		@Override
		public VirtualKeyboardPanel getKeyboard(final JTextComponent textComp) {
			return VirtualKeyboardPanel.getQWERTZKeyboard(textComp);
		}
	};
	public static final VirtualKeyboardType KEYBOARD_QWERTY = new VirtualKeyboardType() {

		@Override
		public VirtualKeyboardPanel getKeyboard(final JTextComponent textComp) {
			return VirtualKeyboardPanel.getQWERTYKeyboard(textComp);
		}
	};
	public static final VirtualKeyboardType KEYBOARD_AZERTY = new VirtualKeyboardType() {

		@Override
		public VirtualKeyboardPanel getKeyboard(final JTextComponent textComp) {
			return VirtualKeyboardPanel.getAZERTYKeyboard(textComp);
		}
	};

	public abstract VirtualKeyboardPanel getKeyboard(JTextComponent textComp);
}
