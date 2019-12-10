package com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.EmptyMachineFrame;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectionFlowViewFactory;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionButtonView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionView;

public class CustomSelectionViewFactory implements SelectionFlowViewFactory {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				TestSelectionFlowCustomModel model = new TestSelectionFlowCustomModel();
				EmptyMachineFrame frame = new EmptyMachineFrame();
				frame.setProductionDataSelectionModel(model);
				frame.getSelectionFlowView().setViewFactory(new CustomSelectionViewFactory());
				frame.getSelectProductButton().doClick();
				frame.setVisible(true);
			}
		});
	}

	@Override
	public AbstractSelectionView createSelectionView(final SelectableItem[] items) {
		if (items.length > 0) {
			if (items[0] instanceof LabelTypeItem) {
				return new CustomImageSelectionView(items);
			}
		}
		return new CustomTextSelectionView(items);
	}

	private static class CustomTextSelectionView extends AbstractSelectionView {
		JComboBox combo;
		JButton button;

		public CustomTextSelectionView(final SelectableItem[] items) {
			initGUI();
			for (SelectableItem item : items) {
				getCombo().addItem(item);
			}
		}

		private void initGUI() {
			setLayout(new MigLayout("fill"));
			add(getCombo(), "center, h 100 , w 300");
			add(getButton(), "h 75");
		}

		public JComboBox getCombo() {
			if (this.combo == null) {
				this.combo = new JComboBox();
			}
			return this.combo;
		}

		public JButton getButton() {
			if (this.button == null) {
				this.button = new JButton("SELECT THIS");
				this.button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						buttonActionPerformed();
					}
				});
			}
			return this.button;
		}

		private void buttonActionPerformed() {
			fireSelectionChanged((SelectableItem) getCombo().getSelectedItem());
		}
	}

	private static class CustomImageSelectionView extends AbstractSelectionButtonView {
		public CustomImageSelectionView(final SelectableItem[] items) {
			super(2, items);
		}

		@Override
		protected int getMaxButtonWidth() {
			return 400;
		}
	}
}
