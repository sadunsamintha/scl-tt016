package com.sicpa.standard.gui.demo.components.jide;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.jidesoft.swing.CheckBoxTree;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class CheckBoxTreeDemoFrame extends javax.swing.JFrame {

	private CheckBoxTree checkTree;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CheckBoxTreeDemoFrame inst = new CheckBoxTreeDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public CheckBoxTreeDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		SicpaLookAndFeelCusto.install();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().add(new JScrollPane(getCheckTree()));
		setSize(400, 300);

	}

	public CheckBoxTree getCheckTree() {
		if (this.checkTree == null) {
			this.checkTree = new CheckBoxTree();
			this.checkTree.getCheckBoxTreeSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(final TreeSelectionEvent e) {

					if (CheckBoxTreeDemoFrame.this.checkTree.getCheckBoxTreeSelectionModel().getSelectionPaths() != null) {
						for (TreePath path : CheckBoxTreeDemoFrame.this.checkTree.getCheckBoxTreeSelectionModel().getSelectionPaths()) {
							System.out.println(path);
						}
					} else {
						System.out.println("null");
					}
				}
			});
		}

		return this.checkTree;
	}

}
