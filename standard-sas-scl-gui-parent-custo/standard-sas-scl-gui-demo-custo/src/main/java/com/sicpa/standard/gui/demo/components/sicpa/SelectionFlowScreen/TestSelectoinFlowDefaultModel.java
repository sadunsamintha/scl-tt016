package com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen;

import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen.CategoryItem.Category;
import com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen.LabelTypeItem.LabelType;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.EmptyMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.DefaultSelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowModel;

public class TestSelectoinFlowDefaultModel {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				EmptyMachineFrame frame = new EmptyMachineFrame();
				frame.setProductionDataSelectionModel(createModel());
				frame.getSelectProductButton().doClick();
//				((DefaultSummaryPanel)frame.getSummaryPanel()).setTooLargePolicy(TooLargePolicy.DECREASE_FONT_SIZE);
				frame.setVisible(true);
			}
		});
	}

	public static DefaultSelectionFlowModel createModel() {
		SelectableItem[] categories = new SelectableItem[] { new CategoryItem(0, getName(), Category.BEER),
				new CategoryItem(1, getName(), Category.SODA), new CategoryItem(1, getName(), Category.SODA),
				new CategoryItem(2, getName(), Category.SPIRIT), new CategoryItem(3, getName(), Category.WINE),
				new CategoryItem(4, getName(), Category.TOBACCO), new CategoryItem(1, getName(), Category.SODA),
				new CategoryItem(2, getName(), Category.SPIRIT), new CategoryItem(3, getName(), Category.WINE),
				new CategoryItem(4, getName(), Category.TOBACCO), new CategoryItem(1, getName(), Category.SODA),
				new CategoryItem(2, getName(), Category.SPIRIT), new CategoryItem(3, getName(), Category.WINE),
				new CategoryItem(4, getName(), Category.TOBACCO), new CategoryItem(1, getName(), Category.SODA),
				new CategoryItem(2, getName(), Category.SPIRIT), new CategoryItem(3, getName(), Category.WINE),
				new CategoryItem(4, getName(), Category.TOBACCO), new CategoryItem(1, getName(), Category.SODA),
				new CategoryItem(2, getName(), Category.SPIRIT), new CategoryItem(3, getName(), Category.WINE),
				new CategoryItem(4, getName(), Category.TOBACCO), new CategoryItem(1, getName(), Category.SODA),
				new CategoryItem(2, getName(), Category.SPIRIT), new CategoryItem(3, getName(), Category.WINE),
				new CategoryItem(4, getName(), Category.TOBACCO), new CategoryItem(1, getName(), Category.SODA),
				new CategoryItem(2, getName(), Category.SPIRIT), new CategoryItem(3, getName(), Category.WINE),
				new CategoryItem(4, getName(), Category.TOBACCO)
		};

		SelectableItem[] labels = new SelectableItem[] { new LabelTypeItem(0, LabelType.labelType1),
				new LabelTypeItem(1, LabelType.labelType2) };

		SelectableItem[] numbers = new SelectableItem[] { new QtyLabelItem(0, "200", 200),
				new QtyLabelItem(1, "1000", 1000), new QtyLabelItem(2, "10 000", 10000),
				new QtyLabelItem(3, "30 000", 30000) };

		numbers = new DefaultSelectableItem[5];
		for (int i = 0; i < 5; i++) {
			numbers[i] = new DefaultSelectableItem(i, (i + 1) * 10000 + "");

		}

		SelectableItem[][] items = new SelectableItem[][] { categories, labels, numbers };

		String[] title = new String[] { "SELECT CATEGORY", "SELECT LABEL TYPE", "NUMBER OF LABELS" };

		DefaultSelectionFlowModel model = new DefaultSelectionFlowModel(items, title);

		return model;
	}

	static int i = 0;

	private static String getName() {
		i++;
		if (i % 3 == 0) {
			return " Produit non-defini � Generique Verre vin � Bouteille en verre � Domestique";
		} else {
			return "Produit non-defini";
		}
		
	}
}
