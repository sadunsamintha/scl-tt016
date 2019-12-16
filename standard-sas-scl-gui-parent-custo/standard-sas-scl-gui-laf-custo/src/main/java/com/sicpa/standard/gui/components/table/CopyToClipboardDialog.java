package com.sicpa.standard.gui.components.table;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.JTableUtils;

public class CopyToClipboardDialog extends DialogWithDropShadow {
	public final static String I18N_LABEL_TITLE = GUIi18nManager.SUFFIX+"table.CopyToClipboardDialog.labelTitle";
	public final static String I18N_RADIO_CELL = GUIi18nManager.SUFFIX+"table.CopyToClipboardDialog.radioCell";
	public final static String I18N_RADIO_ROW = GUIi18nManager.SUFFIX+"table.CopyToClipboardDialog.radioRow";
	public final static String I18N_RADIO_COLUMN = GUIi18nManager.SUFFIX+"table.CopyToClipboardDialog.radioColumn";
	public final static String I18N_CHECK_HEADER = GUIi18nManager.SUFFIX+"table.CopyToClipboardDialog.checkHeader";
	public final static String I18N_BUTTON_OK = GUIi18nManager.SUFFIX+"table.CopyToClipboardDialog.buttonOk";
	public final static String I18N_BUTTON_CANCEL = GUIi18nManager.SUFFIX+"table.CopyToClipboardDialog.buttonCancel";

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(500, 500);
				JTable t = new JTable();
				JTableUtils.turnOnDialogCopyToClipboard(t);
				DefaultTableModel dtm = new DefaultTableModel();
				dtm.addColumn("col 1");
				dtm.addColumn("col 2");
				for (int i = 0; i < 55; i++) {
					dtm.addRow(new Object[] { "col1:" + i, "col2:" + i });
				}
				t.setModel(dtm);
				JScrollPane scroll = new JScrollPane(t);
				f.getContentPane().add(scroll);
				f.setVisible(true);
			}
		});
	}

	private JRadioButton radioCell;
	private JRadioButton radioRow;
	private JRadioButton radioColumn;
	private JCheckBox checkHeader;
	private JButton buttonOk;
	private JButton buttonCancel;
	private JTable table;

	private boolean ok;

	public CopyToClipboardDialog(final CopyToClipBoardConfig config, final JTable table) {
		super(SwingUtilities.getWindowAncestor(table));

		// setAlwaysOnTop(true);
		setTitle("Copy to clipboard");
		this.table = table;
		initGUI();
		ButtonGroup bg = new ButtonGroup();
		bg.add(getRadioCell());
		bg.add(getRadioColumn());
		bg.add(getRadioRow());
		setModal(true);
		setConfig(config);

		setResizable(false);
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getRadioCell());
		add(getRadioRow());
		add(getRadioColumn(), "wrap");
		add(getCheckHeader(), "skip 1,span, wrap");
		add(getButtonOk(), "right,span,split 2,sizegroup 1");
		add(getButtonCancel(), "sizegroup 1");
		setSize(300, 180);
	}

	public JRadioButton getRadioCell() {
		if (this.radioCell == null) {
			this.radioCell = new JRadioButton(GUIi18nManager.get(I18N_RADIO_CELL));
		}
		return this.radioCell;
	}

	public JRadioButton getRadioColumn() {
		if (this.radioColumn == null) {
			this.radioColumn = new JRadioButton(GUIi18nManager.get(I18N_RADIO_COLUMN));
		}
		return this.radioColumn;
	}

	public JRadioButton getRadioRow() {
		if (this.radioRow == null) {
			this.radioRow = new JRadioButton(GUIi18nManager.get(I18N_RADIO_ROW));
			this.radioRow.setSelected(true);
		}
		return this.radioRow;
	}

	public JCheckBox getCheckHeader() {
		if (this.checkHeader == null) {
			this.checkHeader = new JCheckBox(GUIi18nManager.get(I18N_CHECK_HEADER));
		}
		return this.checkHeader;
	}

	private void copyTableCell() {
		if (this.table.getSelectedRow() == -1)
			return;

		StringSelection stsel;
		StringBuffer sbf = new StringBuffer();

		sbf.append(this.table.getValueAt(this.table.getSelectedRow(), this.table.getSelectedColumn()));

		stsel = new StringSelection(sbf.toString());
		java.awt.datatransfer.Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
		system.setContents(stsel, stsel);
	}

	public JButton getButtonCancel() {
		if (this.buttonCancel == null) {
			this.buttonCancel = new JButton(GUIi18nManager.get(I18N_BUTTON_CANCEL));
			this.buttonCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonCancelActionPerformed();
				}
			});
		}
		return this.buttonCancel;
	}

	private void buttonCancelActionPerformed() {
		this.ok = false;
		setVisible(false);
	}

	public JButton getButtonOk() {
		if (this.buttonOk == null) {
			this.buttonOk = new JButton(GUIi18nManager.get(I18N_BUTTON_OK));
			this.buttonOk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonOkActionPerformed();
				}
			});
		}
		return this.buttonOk;
	}

	private void buttonOkActionPerformed() {
		this.ok = true;
		setVisible(false);
		if (this.radioCell.isSelected()) {
			copyTableCell();
		} else if (this.radioColumn.isSelected()) {
			copyColumn();
		} else if (this.radioRow.isSelected()) {
			copyRow();
		}
	}

	public boolean isOk() {
		return this.ok;
	}

	public static class CopyToClipBoardConfig {
		int radio;
		boolean header;

		public CopyToClipBoardConfig(final int radio, final boolean header) {
			this.header = header;
			this.radio = radio;
		}
	}

	public CopyToClipBoardConfig getConfig() {
		int radio;
		if (this.radioCell.isSelected()) {
			radio = 1;
		} else if (this.radioRow.isSelected()) {
			radio = 2;
		} else {
			radio = 3;
		}

		return new CopyToClipBoardConfig(radio, this.checkHeader.isSelected());
	}

	private void setConfig(final CopyToClipBoardConfig config) {
		if (config == null) {
			return;
		}
		switch (config.radio) {
		case 1:
			this.radioCell.setSelected(true);
			break;
		case 2:
			this.radioRow.setSelected(true);
			break;
		case 3:
			this.radioColumn.setSelected(true);
			break;
		}
		this.checkHeader.setSelected(config.header);
	}

	private void copyColumn() {
		StringSelection stsel;
		StringBuffer sbf = new StringBuffer();
		int col = this.table.getSelectedColumn();

		if (this.checkHeader.isSelected()) {
			sbf.append(this.table.getColumnName(col));
			sbf.append("\n");
		}

		Object value = null;

		for (int row : this.table.getSelectedRows()) {
			value = this.table.getValueAt(row, col);
			sbf.append(value);
			sbf.append("\n");
		}

		stsel = new StringSelection(sbf.toString());
		Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
		system.setContents(stsel, stsel);
	}

	private void copyRow() {
		StringSelection stsel;
		StringBuffer sbf = new StringBuffer();

		if (this.checkHeader.isSelected()) {
			JTableHeader tblHeader = this.table.getTableHeader();
			TableColumnModel model = tblHeader.getColumnModel();
			int headCount = model.getColumnCount();
			for (int col = 0; col < headCount; col++) {
				sbf.append(model.getColumn(col).getHeaderValue());
				if (col < headCount - 1) {
					sbf.append("\t");
				}
			}
			sbf.append("\n");
		}

		Object value = null;

		for (int row : this.table.getSelectedRows()) {
			for (int col = 0; col < this.table.getColumnCount(); col++) {
				value = this.table.getValueAt(row, col);

				sbf.append(value);

				sbf.append("\t");
			}
			sbf.append("\n");
		}
		stsel = new StringSelection(sbf.toString());
		Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
		system.setContents(stsel, stsel);
	}
}
