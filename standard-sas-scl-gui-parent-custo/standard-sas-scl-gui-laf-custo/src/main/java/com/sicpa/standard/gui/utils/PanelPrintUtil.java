package com.sicpa.standard.gui.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class PanelPrintUtil extends javax.swing.JPanel implements Printable {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				final JPanel p = new JPanel(new FlowLayout());
				p.setOpaque(true);
				p.setBackground(Color.RED);
				frame.getContentPane().add(p);
				JButton printButton = new JButton("print");
				printButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						PrinterJob printJob = PrinterJob.getPrinterJob();
						printJob.setPrintable(new PanelPrintUtil(p));
						if (printJob.printDialog())
							try {
								printJob.print();
							} catch (PrinterException pe) {
								pe.printStackTrace();
							}
					}
				});
				p.add(printButton);

				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.setSize(500, 500);
				frame.setVisible(true);
			}
		});
	}

	private JPanel panel;

	public PanelPrintUtil(final JPanel panel) {
		this.panel = panel;
	}

	public int print(final Graphics g, final PageFormat pf, final int pageIndex) throws PrinterException {
		// for faster printing, turn off double buffering
		RepaintManager.currentManager(this.panel).setDoubleBufferingEnabled(false);
		Graphics2D g2 = (Graphics2D) g;
		Dimension d = this.panel.getSize(); // get size of document
		double panelWidth = d.width; // width in pixels
		double panelHeight = d.height; // height in pixels

		double pageHeight = pf.getImageableHeight(); // height of printer page
		double pageWidth = pf.getImageableWidth(); // width of printer page

		double scale = pageWidth / panelWidth;
		int totalNumPages = (int) Math.ceil(scale * panelHeight / pageHeight);

		// make sure not print empty pages
		if (pageIndex >= totalNumPages) {
			return Printable.NO_SUCH_PAGE;
		}

		// shift Graphic to line up with beginning of print-imageable region
		g2.translate(pf.getImageableX(), pf.getImageableY());

		// shift Graphic to line up with beginning of next page to print
		g2.translate(0f, -pageIndex * pageHeight);

		// scale the page so the width fits...
		g2.scale(scale, scale);

		this.panel.paint(g2); // repaint the page for printing

		RepaintManager.currentManager(this.panel).setDoubleBufferingEnabled(true);
		return Printable.PAGE_EXISTS;

	}

}
