package com.sicpa.standard.gui.utils;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class ButtonsUtils {

	public static ButtonGroup createButtonGroup(final AbstractButton ... buttons)
	{
		ButtonGroup bg=new ButtonGroup();
		for(AbstractButton b:buttons)
		{
			bg.add(b);
		}
		return bg;
	}
	
}
