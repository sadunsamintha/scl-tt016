package com.sicpa.standard.gui.screen.machine.component.SelectionFlow;

import java.io.Serializable;

import javax.swing.ImageIcon;

public interface SelectableItem extends Serializable{
	String getText();
	//use imageicon because bufferedImage are not serializable
	ImageIcon getImage();
	boolean isShownOnSummary();
	String getFormatedTextForSummary();
	int getId();
}
