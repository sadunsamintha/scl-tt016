package com.sicpa.standard.gui.demo.components.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.components.renderers.SicpaTreeCellRenderer;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class JtreeNonOpaque extends JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JtreeNonOpaque f = new JtreeNonOpaque();
				f.setVisible(true);
			}
		});
	}

	private JScrollPane scroll;
	private JTree tree;
	private JPanel backgroundPanel;

	public JtreeNonOpaque() {
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(getBackgroundPanel());
		getContentPane().add(getScroll());
		setSize(400, 300);
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getTree());
			this.scroll.setOpaque(false);
			this.scroll.getViewport().setOpaque(false);
		}
		return this.scroll;
	}

	public JTree getTree() {
		if (this.tree == null) {
			this.tree = new JTree();
			this.tree.setOpaque(false);
			this.tree.setCellRenderer(new SicpaTreeCellRenderer() {
				@Override
				public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel,
						final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
					JComponent comp = (JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
							row, hasFocus);
					comp.setBackground(new Color(0, 0, 0, 0));
					comp.setForeground(Color.WHITE);
					return comp;
				}
			});
		}
		return this.tree;
	}

	public JPanel getBackgroundPanel() {
		if (this.backgroundPanel == null) {
			this.backgroundPanel = new JPanel() {
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.RED);
					g.fillRect(0, 0, 200, 100);
				}
			};
			this.backgroundPanel.setLayout(new BorderLayout());
		}
		return this.backgroundPanel;
	}

}
