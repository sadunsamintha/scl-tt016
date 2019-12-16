package com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen;

import java.util.List;

import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen.CategoryItem.Category;
import com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen.LabelTypeItem.LabelType;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.EmptyMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;

public class TestSelectionFlowCustomModel extends AbstractSelectionFlowModel {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				EmptyMachineFrame frame = new EmptyMachineFrame();
				TestSelectionFlowCustomModel model = new TestSelectionFlowCustomModel();
				frame.setProductionDataSelectionModel(model);
				frame.getSelectProductButton().doClick();
				frame.setVisible(true);
			}
		});
	}

	@Override
	protected void populate(final SelectableItem[] selections, final List<SelectableItem> nextOptions) {
		if (selections.length == 0) {
			// no selection => 1st screen
			nextOptions.add(new CategoryItem(0, "BEER", Category.BEER));
			nextOptions.add(new CategoryItem(1, "SODA", Category.SODA));
			nextOptions.add(new CategoryItem(2, "SPIRIT", Category.SPIRIT));
			nextOptions.add(new CategoryItem(3, "WINE", Category.WINE));
			nextOptions.add(new CategoryItem(4, "TOBACCO", Category.TOBACCO));
		} else if (selections.length == 1) {
			nextOptions.add(new LabelTypeItem(0, LabelType.labelType1));
			nextOptions.add(new LabelTypeItem(1, LabelType.labelType2));
		} else if (selections.length == 2) {
			//show different items depending on the label type
			LabelTypeItem type = (LabelTypeItem) selections[1];
			if (type.getType() == LabelType.labelType1) {
				nextOptions.add(new QtyLabelItem(0, "100", 100));
				nextOptions.add(new QtyLabelItem(1, "1 000", 1000));
				nextOptions.add(new QtyLabelItem(2, "2 000", 2000));
			} else {
				nextOptions.add(new QtyLabelItem(0, "10 000", 10000));
				nextOptions.add(new QtyLabelItem(1, "50 000", 50000));
				nextOptions.add(new QtyLabelItem(2, "100 000", 100000));
				nextOptions.add(new QtyLabelItem(3, "200 000", 500000));
			}
		}
	}

	@Override
	public String getTitle(final SelectableItem[] selection) {
		switch (selection.length) {
		case 0:
			return "SELECT CATEGORY";
		case 1:
			return "SELECT CODE TYPE";
		case 2:
			return "SELECT LABEL QUANTITY";
		}
		return "";
	}

	@Override
	public boolean isLeaf(final SelectableItem[] selections) {
		// return true if the selection is tobocca else leaves are in 3rd screen
		if (selections.length > 0) {
			CategoryItem cat = (CategoryItem) selections[0];

			if (cat.getCategory() == Category.TOBACCO) {
				return true;
			} else {
				return selections.length == 3;
			}
		}
		return false;
	}
}
