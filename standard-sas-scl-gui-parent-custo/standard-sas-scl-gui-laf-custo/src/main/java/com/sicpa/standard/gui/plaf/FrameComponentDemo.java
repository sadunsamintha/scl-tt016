package com.sicpa.standard.gui.plaf;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXDatePicker;
import org.pushingpixels.lafwidget.LafWidget;

import com.sicpa.standard.gui.components.buttons.PaddedButton;

public class FrameComponentDemo extends javax.swing.JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				SicpaLookAndFeelCusto.install();
				UIManager.put(LafWidget.TEXT_EDIT_CONTEXT_MENU, Boolean.TRUE);
				FrameComponentDemo inst = new FrameComponentDemo();
				inst.pack();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public FrameComponentDemo() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new MigLayout("fill"));

		JPanel panel = new JPanel(new MigLayout("fill"));
		add(new JScrollPane(panel));

		JPanel panel1 = getPanel();
		JPanel panel3 = getPanel();

		SicpaLookAndFeelCusto.flagAsWorkArea(panel1);
		SicpaLookAndFeelCusto.flagAsHeaderOrFooter(panel3);

		panel.add(panel1, "grow");
		// panel.add(panel2, "grow");
		panel.add(panel3, "grow");

		JMenuBar bar = new JMenuBar();
		JMenu menu1 = new JMenu("menu");
		JMenuItem item1 = new JMenuItem("item");
		JMenuItem item2 = new JMenuItem("item");
		JMenuItem item3 = new JMenuItem("item");

		JMenu menu2 = new JMenu("menu");
		JMenuItem item21 = new JMenuItem("item");
		JMenuItem item22 = new JMenuItem("item");
		JMenuItem item23 = new JMenuItem("item");

		bar.add(menu1);
		bar.add(menu2);

		menu1.add(item1);
		menu1.add(item2);
		menu1.addSeparator();
		menu1.add(item3);

		menu2.add(item21);
		menu2.addSeparator();
		menu2.add(item22);
		menu2.add(item23);

		setJMenuBar(bar);

		item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
		item2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.SHIFT_MASK));

		item21.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
		item22.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_MASK));
		item23.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.SHIFT_MASK));
		
		getContentPane().add(new PaddedButton(new JButton("                          line1                      \nline2\nline3")));
	}

	private JPanel getPanel() {
		final JPanel panel = new JPanel(new MigLayout("fill,inset 0 0 0 0", "[220!][200!]", ""));

		panel.add(new JTextField("222222222"), "growx");

		JTextField f0 = new JTextField("333333333");
		f0.setBackground(Color.red);
		f0.setOpaque(true);
		panel.add(f0, "growx,wrap");

		JTextField f1 = new JTextField("not editable");
		JTextField f2 = new JTextField("disable");

		f1.setEditable(false);
		f2.setEnabled(false);

		panel.add(f1, "growx");
		panel.add(f2, "growx,wrap");

		JPasswordField p = new JPasswordField("test");
		panel.add(p, "growx");
		p = new JPasswordField("test");
		p.setEnabled(false);
		panel.add(p, "growx,wrap");

		JXDatePicker picker = new JXDatePicker(new Date());
		panel.add(picker, "growx");

		final JXDatePicker picker2 = new JXDatePicker(new Date());

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				picker2.setEnabled(false);
			}
		});
		panel.add(picker2, "growx,wrap");

		JTextArea jta = new JTextArea();
		jta.setText("normal mmmmmm\nrrrrrr");
		panel.add(jta, "grow");

		jta = new JTextArea();
		jta.setText("not editable mmmmmm\nrrrrrr");
		panel.add(jta, "grow,wrap");
		jta.setEditable(false);

		jta = new JTextArea();
		jta.setEnabled(false);
		jta.setText("disabled mmmmmm\nrrrrrr");
		panel.add(jta, "grow");

		JButton button = new JButton("button");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				JDialog d = new JDialog();
				d.setSize(300, 300);
				//SicpaLookAndFeel.FlagAsWorkArea((JComponent)d.getContentPane()
				// );
				d.setVisible(true);
			}
		});
		panel.add(button, "split 2");
		button = new JButton("button");
		button.setEnabled(false);
		panel.add(button, "wrap");

		JProgressBar bar = new JProgressBar();
		bar.setStringPainted(true);
		bar.setValue(50);
		panel.add(bar, "grow");

		JSpinner spinner = new JSpinner();
		panel.add(spinner, "grow,wrap");

		bar = new JProgressBar();
		bar.setIndeterminate(true);
		panel.add(bar, "grow");

		spinner = new JSpinner();
		spinner.setEnabled(false);
		panel.add(spinner, "grow,wrap");

		JCheckBox check = new JCheckBox("check");
		panel.add(check, "span , split 4");
		check = new JCheckBox("check");
		check.setSelected(true);
		panel.add(check, "");
		check = new JCheckBox("check");
		check.setEnabled(false);
		panel.add(check, "");
		check = new JCheckBox("check");
		check.setSelected(true);
		check.setEnabled(false);
		panel.add(check, "");

		JRadioButton radio = new JRadioButton("radio");
		panel.add(radio, "span , split 4");
		radio = new JRadioButton("radio");
		radio.setSelected(true);
		panel.add(radio);
		radio = new JRadioButton("radio");
		radio.setEnabled(false);
		panel.add(radio);
		radio = new JRadioButton("radio");
		radio.setSelected(true);
		radio.setEnabled(false);
		panel.add(radio);

		DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
		comboModel.addElement("ellemt 1");
		comboModel.addElement("ellemt 2");
		comboModel.addElement("ellemt 3");
		comboModel.addElement("ellemt 4");
		JComboBox combo = new JComboBox(comboModel);
		panel.add(combo, "growx");
		combo = new JComboBox(comboModel);
		combo.setEnabled(false);
		panel.add(combo, "wrap,growx");
		combo = new JComboBox(comboModel);
		combo.setEditable(true);
		panel.add(combo, "growx");
		combo = new JComboBox(comboModel);
		combo.setEditable(true);
		combo.setEnabled(false);
		panel.add(combo, "growx,wrap");

		JSlider slider = new JSlider();
		slider.setPaintTicks(true);
		panel.add(slider, "");

		slider = new JSlider();
		slider.setPaintTicks(true);
		slider.setEnabled(false);
		panel.add(slider, "wrap");

		JSeparator sepa = new JSeparator();
		panel.add(sepa, "grow,span");

		JTree tree = new JTree();
		panel.add(new JScrollPane(tree), "grow, h 100");
		tree = new JTree();
		tree.setEnabled(false);
		panel.add(new JScrollPane(tree), "grow,h 100,wrap");

		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("col1");
		model.addColumn("col1");
		model.addRow(new Object[] { "col 1 row1", " col2 row 1" });
		model.addRow(new Object[] { "col 1 row2", " col2 row 2" });
		model.addRow(new Object[] { "col 1 row3", " col2 row 3" });
		JTable table = new JTable(model);
		panel.add(new JScrollPane(table), "grow, h 100, w 100");
		table = new JTable(model);
		table.setEnabled(false);
		panel.add(new JScrollPane(table), "grow,wrap, h 100 , w 100");

		panel.setBorder(BorderFactory.createTitledBorder("title"));
		return panel;
	}

}
