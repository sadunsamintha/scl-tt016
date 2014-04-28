package com.sicpa.standard.sasscl.productionParameterSelection.node;

import java.util.List;

import javax.swing.ImageIcon;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public interface IProductionParametersNode extends SelectableItem {

	List<? extends IProductionParametersNode> getChildren();

	String getText();

	ImageIcon getImage();

	Object getValue();

	boolean isLeaf();

}
