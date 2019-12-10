package com.sicpa.standard.gui.demo.components.sicpa;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.components.pagination.AbstractTablePagination;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class Pagination extends AbstractTablePagination<Dimension> {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				Pagination pagination = new Pagination();

				f.setSize(800, 600);
				f.getContentPane().add(pagination);
				f.setVisible(true);

				pagination.requestFirst();

			}
		});
	}

	public Pagination() {
		initGUI();
	}

	@Override
	protected String[] getFields() {
		return new String[] { "width", "height" };
	}

	@Override
	protected String[] getHeaders() {
		return new String[] { "width", "height" };
	}

	@Override
	protected List<Dimension> getItems(int pageNumber) {
		ThreadUtils.sleepQuietly(1000);
		List<Dimension> list = new ArrayList<Dimension>();
		for (int i = 0; i < 20; i++) {
			list.add(new Dimension((int) (Math.random() * 1000), (int) (Math.random() * 1000)));
		}
		return list;
	}

	@Override
	protected Pair<List<Dimension>, Integer> doRequestFirst() {
		return new Pair<List<Dimension>, Integer>(getItems(0), 10);
	}
}
