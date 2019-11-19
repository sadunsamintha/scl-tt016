package com.sicpa.standard.gui.screen.machine.impl.SPL.CustomisationSample;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.DefaultStatisticsPanel;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.StatisticsModel;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class SampleCustomStatsPanel extends DefaultStatisticsPanel {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				SampleCustomStatsPanel p = new SampleCustomStatsPanel();
				final SampleCustomStatisticsModel model = (SampleCustomStatisticsModel) p.getModel();
				model.setPrinterWarning(2);

				new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							ThreadUtils.sleepQuietly((long) (Math.random() * 5));
							model.addValid();
							model.addValid();
						}
					}
				}).start();

				new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							model.addInvalid();
							ThreadUtils.sleepQuietly((long) (Math.random() * 10));
						}
					}
				}).start();

				f.getContentPane().add(p);
				f.setSize(700, 300);
				f.setVisible(true);
			}
		});
	}

	private JLabel labelPrinterWarning;
	private JLabel labelPrinterWarningVal;
	private PercentBar qualityBar;

	public SampleCustomStatsPanel() {
		super(new SampleCustomStatisticsModel());
		initGUI();
	}

	private void initGUI() {
		removeAll();
		setLayout(new MigLayout("inset 0 0 0 0 ", "[]100[][]", "[]20[]5[]5[]15[][]"));
		add(getLabelTitle(), ",gapleft 5,split 2, spanx");
		JSeparator sepa = new JSeparator();
		sepa.setBackground(SicpaColor.BLUE_DARK);
		add(sepa, "growx,gapright 5");

		add(getLabelValid(), "gapleft 15");
		add(getLabelNumberValidVal(), "grow, w 25%,pushx");
		add(getQualityBar(), "wrap,grow,w 25%,spany 2,gap right 10");

		add(getLabelInvalid(), "gapleft 15");
		add(getLabelNumberInvalidVal(), "wrap");

		add(getLabelTotal(), "gapleft 15");
		add(getLabelNumberTotalVal(), "wrap");

		add(getLabelPrinterWarning(), "gapleft 15");
		add(getLabelPrinterWarningVal(), "wrap");

		add(getLabelLineSpeed(), "gapleft 15");
		add(getLabelLineSpeedVal(), "wrap");

		add(getLabelUptime(), "gapleft 15");
		add(getLabelUptimeVal(), "");
	}

	public JLabel getLabelPrinterWarning() {
		if (this.labelPrinterWarning == null) {
			this.labelPrinterWarning = new JLabel("PRINTER WARNING");
			this.labelPrinterWarning.setForeground(SicpaColor.BLUE_DARK);
		}

		return this.labelPrinterWarning;
	}

	public JLabel getLabelPrinterWarningVal() {
		if (this.labelPrinterWarningVal == null) {
			this.labelPrinterWarningVal = new JLabel("0");
		}
		return this.labelPrinterWarningVal;
	}

	public PercentBar getQualityBar() {
		if (this.qualityBar == null) {
			this.qualityBar = new PercentBar();
		}
		return this.qualityBar;
	}

	@Override
	protected void modelPropertyChanged(final PropertyChangeEvent evt) {
		super.modelPropertyChanged(evt);
		if (evt.getPropertyName().equals(SampleCustomStatisticsModel.PROPERTY_PRINTER_WARNING)) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getLabelPrinterWarningVal().setText(evt.getNewValue() + "");
				}
			});
		} else if (evt.getPropertyName().equals(StatisticsModel.PROPERTY_VALID)) {
			updateQualityBar();
		} else if (evt.getPropertyName().equals(StatisticsModel.PROPERTY_INVALID)) {
			updateQualityBar();
		}
	}

	private void updateQualityBar() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getQualityBar().setCodes(getModel().getValid(), getModel().getInvalid());
			}
		});
	}

	private static class PercentBar extends JPanel {

		long valid;
		long invalid;

		public void setCodes(final long valid, final long invalid) {
			this.valid = valid;
			this.invalid = invalid;
			repaint();
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			g.setColor(SicpaColor.GREEN_DARK);
			g.fillRect(0, 0, getWidth(), getHeight());

			float percentInvalid = (1f * this.invalid) / (this.valid + this.invalid);
			if (percentInvalid > 0) {
				g.setColor(SicpaColor.RED);
				g.fillRect((int) (getWidth() - (getWidth() * percentInvalid)), 0, getWidth(), getHeight());
				g.setColor(SicpaColor.BLUE_DARK);
				g.drawString(NumberFormat.getPercentInstance().format(1-percentInvalid), 10, 20);
			}
		}
	}
}
