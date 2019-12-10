package com.sicpa.standard.gui.demo.components.swingx;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class JXTreeTableDemoFrame extends javax.swing.JFrame {

	private JXTreeTable treeTable;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXTreeTableDemoFrame inst = new JXTreeTableDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXTreeTableDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getContentPane().add(new JScrollPane(getTreeTable()));
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JXTreeTable getTreeTable() {
		if (this.treeTable == null) {
			this.treeTable = new JXTreeTable();
			DefaultTreeTableModel dtm = new DefaultTreeTableModel() {

				@Override
				public String getColumnName(final int column) {
					return "column" + column;
				}

				@Override
				public int getColumnCount() {

					return 4;
				}

				@Override
				public Object getValueAt(final Object node, final int column) {
					if (column == 3)
						return true;
					return node + " col" + column;
				}

				@Override
				public boolean isCellEditable(final Object node, final int column) {
					return true;
				}

			};
			DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("test");
			dtm.setRoot(root);

			DefaultMutableTreeTableNode first = new DefaultMutableTreeTableNode("test");
			root.add(first);
			root.add(new DefaultMutableTreeTableNode("test"));
			root.add(new DefaultMutableTreeTableNode("test"));

			first.add(new DefaultMutableTreeTableNode("test"));
			first.add(new DefaultMutableTreeTableNode("test"));
			first.add(new DefaultMutableTreeTableNode("test"));
			first.add(new DefaultMutableTreeTableNode("test"));

			this.treeTable.setTreeTableModel(dtm);

			System.out.println(this.treeTable.getCellRenderer(0, 0));

		}
		return this.treeTable;
	}
}
