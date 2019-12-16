package com.sicpa.standard.gui.demo.components.jide;

import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jidesoft.swing.CheckBoxList;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class CheckBoxListDemoFrame extends javax.swing.JFrame {

	private CheckBoxList checkList;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				CheckBoxListDemoFrame inst = new CheckBoxListDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public CheckBoxListDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getContentPane().add(new JScrollPane(getCheckList()));
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CheckBoxList getCheckList() {
		if (this.checkList == null) {
			DefaultListModel model = new DefaultListModel();
			for (int i = 0; i < 20; i++) {
				model.addElement("item " + i);
			}

			this.checkList = new CheckBoxList(model);

			this.checkList.getCheckBoxListSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					for (int i : CheckBoxListDemoFrame.this.checkList.getCheckBoxListSelectedIndices()) {
						System.out.println(i);
					}
				}
			});

		}
		return this.checkList;
	}

}
