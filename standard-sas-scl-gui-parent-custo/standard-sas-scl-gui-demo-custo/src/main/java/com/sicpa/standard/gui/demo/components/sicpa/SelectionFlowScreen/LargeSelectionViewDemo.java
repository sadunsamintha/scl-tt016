package com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.DefaultSelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.DefaultLargeSelectionView;

public class LargeSelectionViewDemo {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();

				DefaultLargeSelectionView view = new DefaultLargeSelectionView();

				view.setColumn(2);

				view.setSelectableItems(new SelectableItem[] {
						new DefaultSelectableItem(1, "111"),
						new DefaultSelectableItem(2, "222"),
						new DefaultSelectableItem(3, "333"),
						new DefaultSelectableItem(4, "444") });

				view.setColumn(3);
				view.setSelectableItems(new SelectableItem[] {
						new DefaultSelectableItem(1, "111"),
						new DefaultSelectableItem(2, "222"),
						new DefaultSelectableItem(3, "333"),
						new DefaultSelectableItem(4, "444"),
						new DefaultSelectableItem(5, "555"),
						new DefaultSelectableItem(6, "666") });

				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(800, 600);
				f.getContentPane().add(view);
				f.setVisible(true);

			}
		});
	}
}
