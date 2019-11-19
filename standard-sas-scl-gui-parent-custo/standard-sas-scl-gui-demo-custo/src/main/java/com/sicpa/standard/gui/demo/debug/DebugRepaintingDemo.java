package com.sicpa.standard.gui.demo.debug;

import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.DebugRepaintingUI;

public class DebugRepaintingDemo extends JFrame {

	public DebugRepaintingDemo() {
		super("DebugLayerDemo");
		initGUI();
	}

	private JTable table;
	private JTabbedPane tabbedPane;
	private JPanel buttonPanel;
	private JXLayer<JComponent> debugComp;
	private DebugRepaintingUI debugUI;
	private JMenuBar myMenuBar;

	private void initGUI() {
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(getDebugComp());

		setJMenuBar(getMyMenuBar());

		setSize(400, 350);
		setLocationRelativeTo(null);
	}

	// --------DEBUG COMPONENT
	public JXLayer<JComponent> getDebugComp() {
		if (this.debugComp == null) {
			this.debugComp = new JXLayer<JComponent>(getTabbedpane());
			this.debugComp.setUI(getDebugUI());
		}
		return this.debugComp;
	}

	public DebugRepaintingUI getDebugUI() {
		if (this.debugUI == null) {
			this.debugUI = new DebugRepaintingUI();
		}
		return this.debugUI;
	}

	// ----------------------
	private JMenuBar getMyMenuBar() {
		if (this.myMenuBar == null) {
			this.myMenuBar = new JMenuBar();
			JMenu optionsMenu = new JMenu("Options");
			this.myMenuBar.add(optionsMenu);
			final JCheckBoxMenuItem startItem = new JCheckBoxMenuItem("Start debugging", true);
			startItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK));
			optionsMenu.add(startItem);
			startItem.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
//					DebugRepaintingDemo.this.debugUI.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				}
			});
			this.myMenuBar.add(optionsMenu);
		}
		return this.myMenuBar;
	}

	private JTabbedPane getTabbedpane() {
		if (this.tabbedPane == null) {
			this.tabbedPane = new JTabbedPane();
			this.tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
			this.tabbedPane.addTab("Components", getButtonPanel());
			this.tabbedPane.addTab("Table", new JScrollPane(getTable()));
			this.tabbedPane.addTab("Tree", new JScrollPane(new JTree()));
		}
		return this.tabbedPane;
	}

	private JTable getTable() {
		if (this.table == null) {
			this.table = new JTable(new AbstractTableModel() {
				public int getColumnCount() {
					return 10;
				}

				public int getRowCount() {
					return 20;
				}

				public Object getValueAt(final int rowIndex, final int columnIndex) {
					return "" + rowIndex;
				}
			});
		}
		return this.table;
	}

	private JPanel getButtonPanel() {
		if (this.buttonPanel == null) {
			this.buttonPanel = new JPanel(new MigLayout("fill,wrap 1"));
			this.buttonPanel.add(new JButton("JButton"));
			this.buttonPanel.add(new JRadioButton("JRadioButton"));
			this.buttonPanel.add(new JCheckBox("JCheckBox"));
			this.buttonPanel.add(new JTextField(10));
			String[] str = { "One", "Two", "Three" };
			this.buttonPanel.add(new JComboBox(str));
			this.buttonPanel.add(new JSlider(0, 100));
		}

		return this.buttonPanel;
	}

	public static void main(final String[] args) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DebugRepaintingDemo().setVisible(true);
			}
		});
	}
}
