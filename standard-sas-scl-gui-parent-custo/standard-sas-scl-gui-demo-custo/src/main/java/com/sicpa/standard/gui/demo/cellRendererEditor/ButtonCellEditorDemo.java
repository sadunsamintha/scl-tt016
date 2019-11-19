package com.sicpa.standard.gui.demo.cellRendererEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.components.cellEditor.ButtonCellEditorWithDisabledState;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

public class ButtonCellEditorDemo extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				ButtonCellEditorDemo inst = new ButtonCellEditorDemo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private DefaultTableModel dtm;
	private JTable table;

	public ButtonCellEditorDemo() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(getTable()), BorderLayout.CENTER);
		setSize(950, 300);
	}

	public JTable getTable() {
		if (this.table == null) {
			this.dtm = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(int row, int column) {
					return row % 2 == 0;
				}
			};
			this.dtm.addColumn("col 0");
			this.dtm.addColumn("col 1");
			for (int i = 0; i < 10; i++) {
				this.dtm.addRow(new Object[] { "", "some value" });
			}

			this.table = new JTable(this.dtm);

			ButtonCellEditorWithDisabledState editor = new ButtonCellEditorWithDisabledState() {

				@Override
				protected void buttonActionPerformed(final int row, final JTable table) {
					table.setValueAt("", row, 1);
				}

				@Override
				protected String getText(final JTable table, final int row) {
					return "ROW:" + row;
				}

				@Override
				protected void prepareButton(final JButton button, final JTable table, final Object value,
						final int row, final int column) {
					super.prepareButton(button, table, value, row, column);
					button.setIcon(new ImageIcon(getImageDelete()));
				}
			};

			table.getColumnModel().getColumn(0).setCellEditor(editor);
			table.getColumnModel().getColumn(0).setCellRenderer(editor);
			table.setRowHeight(50);
		}
		return table;
	}

	private BufferedImage imageDelete;

	private BufferedImage getImageDelete() {
		if (this.imageDelete == null) {
			this.imageDelete = GraphicsUtilities.createCompatibleTranslucentImage(20, 20);
			Graphics2D g = (Graphics2D) this.imageDelete.createGraphics();
			g.setFont(new Font("arial", Font.BOLD, 20));

			g.setColor(Color.red);
			PaintUtils.turnOnAntialias(g);
			g.drawString("X", 5, 15);

			g.dispose();
		}
		return this.imageDelete;
	}
}
