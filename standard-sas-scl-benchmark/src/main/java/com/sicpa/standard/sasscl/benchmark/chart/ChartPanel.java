package com.sicpa.standard.sasscl.benchmark.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

public class ChartPanel extends org.jfree.chart.ChartPanel {

	private static final long serialVersionUID = 1L;

	private JMenu menuCopyToClipboard = new JMenu("Copy data to clipboard");

	private JMenuItem itemCopyToCliboardLineAlpha = new JMenuItem("Line");

	private JMenuItem itemCopyToCliboardVTabAlpha = new JMenuItem("Vertical tab");

	private JMenuItem itemCopyToCliboardHTabAlpha = new JMenuItem("Horizontal tab");

	private JMenuItem itemCopyToCliboardLineValue = new JMenuItem("Line");

	private JMenuItem itemCopyToCliboardVTabValue = new JMenuItem("Vertical tab");

	private JMenuItem itemCopyToCliboardHTabValue = new JMenuItem("Horizontal tab");

	private JMenu menuValue = new JMenu("Value sort");

	public ChartPanel(final JFreeChart chart) {
		super(chart);

		JPopupMenu menu = getPopupMenu();
		menu.add(new JSeparator());

		menu.add(this.menuCopyToClipboard);

		if (getChart().getPlot() instanceof XYPlot) {
			this.menuCopyToClipboard.add(this.itemCopyToCliboardLineAlpha);
			this.menuCopyToClipboard.add(this.itemCopyToCliboardHTabAlpha);
			this.menuCopyToClipboard.add(this.itemCopyToCliboardVTabAlpha);
		} else {
			this.menuCopyToClipboard.add(this.menuValue);
			this.menuValue.add(this.itemCopyToCliboardLineValue);
			this.menuValue.add(this.itemCopyToCliboardHTabValue);
			this.menuValue.add(this.itemCopyToCliboardVTabValue);

			this.itemCopyToCliboardLineValue.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent arg0) {
					copyToClipBoardLine(true);
				}
			});
			this.itemCopyToCliboardHTabValue.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent arg0) {
					copyToClipBoardHTab(true);
				}
			});

			this.itemCopyToCliboardVTabValue.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent arg0) {
					copyToClipBoardVTab(true);
				}
			});
		}

		this.itemCopyToCliboardLineAlpha.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				copyToClipBoardLine(false);
			}
		});
		this.itemCopyToCliboardHTabAlpha.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				copyToClipBoardHTab(false);
			}
		});

		this.itemCopyToCliboardVTabAlpha.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				copyToClipBoardVTab(false);
			}
		});

	}

	private void copyToClipBoardLine(final boolean sort) {
		String res = "";

		for (Entry e : prepareData(sort)) {
			res += e.key + ":" + e.value + ",";
		}
		ClipboardUtils.setContents(res, null);
	}

	private void copyToClipBoardHTab(final boolean sort) {
		String res = "";
		String res2 = "";
		for (Entry e : prepareData(sort)) {
			res += e.key + "\t";
			res2 += e.value + "\t";
		}
		ClipboardUtils.setContents(res + "\n" + res2, null);
	}

	private void copyToClipBoardVTab(final boolean sort) {
		String res = "";

		for (Entry e : prepareData(sort)) {
			res += e.key + "\t";
			res += e.value + "\n";
		}
		ClipboardUtils.setContents(res, null);
	}

	private class Entry {
		String key;
		Integer value;

		private Entry(final String key, final Integer value) {
			this.key = key;
			this.value = value;
		}
	}

	private class EntryComparator implements Comparator<Entry> {
		@Override
		public int compare(final Entry o1, final Entry o2) {

			return o2.value.compareTo(o1.value);
		}

	}

	private ArrayList<Entry> prepareData(final boolean sort) {
		ArrayList<Entry> data = new ArrayList<Entry>();
		Plot plot = getChart().getPlot();
		if (plot instanceof XYPlot) {
			XYPlot xyPlot = (XYPlot) plot;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			for (int i = 0; i < xyPlot.getDataset().getItemCount(0); i++) {
				data.add(new Entry(sdf.format(new Date(xyPlot.getDataset().getX(0, i).longValue())), xyPlot
						.getDataset().getY(0, i).intValue()));
			}
		} else if (plot instanceof CategoryPlot) {
			CategoryPlot cPlot = (CategoryPlot) plot;
			for (int i = 0; i < cPlot.getDataset(0).getRowCount(); i++) {
				data.add(new Entry(cPlot.getDataset(0).getRowKey(i).toString(), cPlot.getDataset(0).getValue(i, 0)
						.intValue()));
			}

		} else if (plot instanceof PiePlot) {
			PiePlot pPlot = (PiePlot) plot;
			for (int i = 0; i < pPlot.getDataset().getItemCount(); i++) {
				data.add(new Entry(pPlot.getDataset().getKey(i).toString(), pPlot.getDataset().getValue(i).intValue()));
			}
		}
		if (sort) {
			Collections.sort(data, new EntryComparator());
		}
		return data;
	}

}
