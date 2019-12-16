package com.sicpa.standard.gui.screen.DMS.log;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.jidesoft.swing.SearchableUtils;
import com.jidesoft.swing.TableSearchable;
import com.sicpa.standard.gui.screen.DMS.MasterPCCClientFrame;
import com.sicpa.standard.gui.utils.ThreadUtils;

public abstract class TableLog extends JScrollPane {
	protected JTable table;
	protected DefaultTableModel model;
	private TableSearchable searchable;
	private AbstractAction getMoreRowAction;
	private boolean cellTooltipVisible;

	public TableLog() {
		setViewportView(getTable());

		this.searchable = SearchableUtils.installSearchable(this.table);
		this.searchable.setBackground(Color.white);

		this.searchable.setMainIndex(4);

		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				tableMouseClicked();
			}
		});

		getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(final AdjustmentEvent e) {
				if (!e.getValueIsAdjusting()) {
					verticalScrollBarValueChanged(e);
				}
			}
		});

		this.table.getTableHeader().setReorderingAllowed(false);
	}

	private void verticalScrollBarValueChanged(final AdjustmentEvent e) {
		if (getVerticalScrollBar().getMaximum() == getVerticalScrollBar().getVisibleAmount()) {
			return;
		}

		if (getVerticalScrollBar().getMaximum() <= (getVerticalScrollBar().getVisibleAmount() + e.getValue())) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					fetchNextRows();
				}
			}).start();
		}
	}

	public void fetchNextRows() {
		if (this.getMoreRowAction != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Window w = SwingUtilities.getWindowAncestor(TableLog.this);
					if (w instanceof MasterPCCClientFrame) {
						((MasterPCCClientFrame) w).setBusy(true);
					}

					ThreadUtils.sleepQuietly(100);
					TableLog.this.getMoreRowAction.actionPerformed(null);
					ThreadUtils.sleepQuietly(100);
					if (w instanceof MasterPCCClientFrame) {
						((MasterPCCClientFrame) w).setBusy(false);
					}
				}
			}).start();
		}
	}

	private void tableMouseClicked() {
		this.searchable.setMainIndex(this.table.getSelectedColumn());
	}

	public JTable getTable() {
		if (this.table == null) {
			this.model = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(final int row, final int column) {
					return false;
				}
			};
			this.table = new JTable(this.model) {
				@Override
				public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
					Component c = super.prepareRenderer(renderer, row, column);
					if (c instanceof JComponent) {
						JComponent jc = (JComponent) c;
						if (isCellTooltipVisible()) {
							Object value = getValueAt(row, column);
							if (value == null) {
								jc.setToolTipText("");
							} else {
								String t = value.toString();
								String[] lines = t.split("\\n");
								StringBuilder sb = new StringBuilder();
								if (lines.length > 0) {
									sb.append("<html>");
									for (String s : lines) {
										sb.append(s).append("<br>");
									}
									t = sb.toString();
								}
								jc.setToolTipText(t);
							}
						} else {
							jc.setToolTipText("");
						}
					}
					return c;
				}
			};
		}
		return this.table;
	}

	public void setGetMoreRowAction(final AbstractAction getMoreRowAction) {
		this.getMoreRowAction = getMoreRowAction;
	}

	public void setCellTooltipVisible(final boolean cellTooltipVisible) {
		this.cellTooltipVisible = cellTooltipVisible;
	}

	public boolean isCellTooltipVisible() {
		return this.cellTooltipVisible;
	}
}
