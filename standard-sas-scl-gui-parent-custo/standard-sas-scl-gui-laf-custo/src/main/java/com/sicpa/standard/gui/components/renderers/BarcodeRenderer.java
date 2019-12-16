package com.sicpa.standard.gui.components.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.components.border.ComponentBorder;
import com.sicpa.standard.gui.components.border.ComponentBorder.Edge;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class BarcodeRenderer// implements TableCellRenderer
{
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();

				JFrame f = new JFrame();
				f.getContentPane().setLayout(new MigLayout("fill"));
				final BarcodeRenderer b = new BarcodeRenderer();
				b.setBarcodeText("P00000001111");
				f.getContentPane().add(b.getTextBarcode(), "wrap");

				final JTable table = new JTable();
				DefaultTableModel dtm = new DefaultTableModel() {
					@Override
					public boolean isCellEditable(final int row, final int column) {
						return false;
					}
				};
				dtm.addColumn("barcode");
				dtm.addColumn("column 1");
				for (int j = 0; j < 12; j++) {
					dtm.addRow(new Object[] { "P0000000000" + j });
				}

				table.setModel(dtm);

				BarcodeRenderer br = new BarcodeRenderer();
				table.getColumnModel().getColumn(0).setCellRenderer(br.getTableCellRendererBarcode());
				table.setRowHeight(45);
				br.addButtonClickListenerOnTable(table);
				br.setPrintAction(new PrintAction() {
					@Override
					public void print(final String barcode) {
						System.out.println("PRINT:" + barcode);
					}
				});

				table.getColumnModel().getColumn(0).setPreferredWidth(250);
				table.getColumnModel().getColumn(1).setPreferredWidth(70);
				f.getContentPane().add(new JScrollPane(table), "grow");

				f.setSize(400, 450);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}

	private SicpaTableCellRenderer tableCellRendererBarcode;
	private JTextField textBarcode;
	private JButton buttonPrint;
	private PrintAction printAction;
	private boolean usedAsRenderer;
	private String lastBarcodeClicked;

	public BarcodeRenderer() {
		this.usedAsRenderer = false;
	}

	public JButton getButtonPrint() {
		if (this.buttonPrint == null) {
			this.buttonPrint = new JButton();
			this.buttonPrint.setIcon(new ImageIcon(getIconBarcode()));
			this.buttonPrint.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonPrintActionPerformed(e);
				}
			});
		}
		return this.buttonPrint;
	}

	private void buttonPrintActionPerformed(final ActionEvent e) {
		if (this.printAction != null) {
			if (this.usedAsRenderer) {
				this.printAction.print(this.lastBarcodeClicked);
			} else {
				this.printAction.print(getTextBarcode().getText());
			}
		}
	}

	public SicpaTableCellRenderer getTableCellRendererBarcode() {
		if (this.tableCellRendererBarcode == null) {
			this.tableCellRendererBarcode = new BarcodeTableCellRenderer();
			this.tableCellRendererBarcode.add(getButtonPrint());
		}
		return this.tableCellRendererBarcode;
	}

	private class BarcodeTableCellRenderer extends SicpaTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			getButtonPrint().setVisible(BarcodeRenderer.this.indexMouseover == row);

			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			int h = table.getRowHeight();
			int w = table.getColumnModel().getColumn(column).getWidth();
			getButtonPrint().setBounds(w - 55, h / 2 - 15, 50, 30);
			return res;
		}
	}

	public JTextField getTextBarcode() {
		if (this.textBarcode == null) {
			this.textBarcode = new JTextField();
			this.textBarcode.setEditable(false);
			this.textBarcode.setColumns(15);

			ComponentBorder cb = new ComponentBorder(getButtonPrint(), Edge.RIGHT);
			cb.install(this.textBarcode);
		}
		return this.textBarcode;
	}

	private void setBarcodeText(final String text) {
		getTextBarcode().setText(text);
	}

	public void setPrintAction(final PrintAction printAction) {
		this.printAction = printAction;
	}

	private static BufferedImage barcodeImage;

	private static BufferedImage getIconBarcode() {
		if (barcodeImage == null) {
			int cpt = 0;
			int w = 40;
			int h = 20;
			barcodeImage = GraphicsUtilities.createCompatibleImage(w, h);
			Graphics2D g = barcodeImage.createGraphics();
			int test;
			Random r = new Random();
			for (int i = 0; i < w; i++) {
				test = r.nextInt(2);
				if (test == 0) {
					if (cpt == 3) {
						g.setColor(Color.WHITE);
						cpt = 0;
					} else {
						g.setColor(Color.BLACK);
						cpt++;
					}
				} else {
					if (cpt == -3) {
						g.setColor(Color.BLACK);
						cpt = 0;
					} else {
						g.setColor(Color.WHITE);
						cpt--;
					}
				}
				g.drawLine(i, 0, i, h);
			}
			g.dispose();
		}
		return barcodeImage;
	}

	public static interface PrintAction {
		public void print(String barcode);
	}

	public int indexMouseover = -1;

	public void addButtonClickListenerOnTable(final JTable table) {
		this.usedAsRenderer = true;
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());

				if (table.getColumnModel().getColumn(col).getCellRenderer() instanceof BarcodeTableCellRenderer) {
					int colW = table.getColumnModel().getColumn(col).getWidth();
					int w = 0;
					for (int i = 0; i < col; i++) {
						w += table.getColumnModel().getColumn(i).getWidth();
					}
					w += colW - getButtonPrint().getWidth();
					if (e.getX() > w && e.getX() < w + getButtonPrint().getWidth() - 5) {
						int rowH = table.getRowHeight();

						int h = (rowH - getButtonPrint().getHeight()) / 2;

						int posY = e.getY() - row * rowH;

						if (posY > h && posY < h + getButtonPrint().getHeight()) {
							BarcodeRenderer.this.lastBarcodeClicked = table.getValueAt(row, col) + "";
							getButtonPrint().doClick();
						}
					}
					// System.out.println(e.getX() +">"+ w +" "+ e.getX() +"<"+
					// (w + getButtonPrint().getWidth() - 5));
				}
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				if (BarcodeRenderer.this.indexMouseover != -1) {
					BarcodeRenderer.this.indexMouseover = -1;
					table.repaint();
				}
			}
		});
		table.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(final MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				boolean repaint = false;
				if (row != BarcodeRenderer.this.indexMouseover) {
					repaint = true;
				}
				BarcodeRenderer.this.indexMouseover = row;
				if (repaint) {
					table.repaint();
				}
			}
		});
	}
}
