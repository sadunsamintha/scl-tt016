package com.sicpa.standard.gui.demo.components.swingx;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import com.sicpa.standard.gui.painter.SicpaJXMultiSplitPaneDividerPainter;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class JXMultiSplitPaneDemoFrame extends javax.swing.JFrame {

	private JXMultiSplitPane splitpane;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXMultiSplitPaneDemoFrame inst = new JXMultiSplitPaneDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXMultiSplitPaneDemoFrame() {
		super();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getSplitpane());
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getContentPane().add(getSplitpane());
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JXMultiSplitPane getSplitpane() {
		if (this.splitpane == null) {
			this.splitpane = new JXMultiSplitPane();

			String layoutDef = "(COLUMN top (ROW left right) bottom)";
			MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
			this.splitpane.setModel(modelRoot);

			JButton top = new JButton("top");
			top.setPreferredSize(new Dimension(0, 100));

			JButton bottom = new JButton("bottom");
			bottom.setPreferredSize(new Dimension(0, 100));

			JScrollPane jta = new JScrollPane(new JTextArea());
			jta.setPreferredSize(new Dimension(100, 100));

			JScrollPane tree = new JScrollPane(new JTree());
			tree.setPreferredSize(new Dimension(50, 200));

			this.splitpane.add(jta, "left");
			this.splitpane.add(tree, "right");
			this.splitpane.add(top, "top");
			this.splitpane.add(bottom, "bottom");

			this.splitpane.setDividerSize(5);
			this.splitpane.setDividerPainter(new SicpaJXMultiSplitPaneDividerPainter(this.splitpane));

		}
		return this.splitpane;
	}

}
