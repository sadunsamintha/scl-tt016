package com.sicpa.standard.gui.screen.machine.impl.SPL.stats;

import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.label.UptimeLabel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultStatisticsPanel extends AbstractStatisticsPanel {

	public static final String I18N_LABEL_VALID_CODE = GUIi18nManager.SUFFIX + "SPL.stats.valid";
	public static final String I18N_LABEL_INVALID_CODE = GUIi18nManager.SUFFIX + "SPL.stats.invalid";
	public static final String I18N_LABEL_TOTAL_CODE = GUIi18nManager.SUFFIX + "SPL.stats.total";
	public static final String I18N_LABEL_LINE_SPEED = GUIi18nManager.SUFFIX + "SPL.stats.lineSpeed";
	public static final String I18N_LABEL_TITLE = GUIi18nManager.SUFFIX + "SPL.stats.labelTitle";
	public static final String I18N_LABEL_CODEFREQ = GUIi18nManager.SUFFIX + "SPL.stats.codeFreq";
	public static final String I18N_LABEL_UPTIME = GUIi18nManager.SUFFIX + "SPL.stats.uptime";

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				final StatisticsModel model = new StatisticsModel();

				DefaultStatisticsPanel p = new DefaultStatisticsPanel(model);

				new Thread(new Runnable() {
					@Override
					public void run() {
						model.startUptime();
						int i = 0;
						while (true) {
							ThreadUtils.sleepQuietly((long) (Math.random() * 10));
							model.addInvalid();
							ThreadUtils.sleepQuietly((long) (Math.random() * 10));
							model.addValid();
							model.setLineSpeed((int) (Math.random() * 100));
							model.setCodeFreq((int) (Math.random() * 10));
							i++;
							if (i == 500) {
								model.stopUptime();
							}
						}
					}
				}).start();

				f.getContentPane().add(p);
				f.setSize(700, 300);
				f.setVisible(true);
			}
		});
	}

	protected JLabel labelNumberValidVal;
	protected JLabel labelNumberInvalidVal;
	protected JLabel labelNumberTotalVal;

	protected JLabel labelPercentValidVal;
	protected JLabel labelPercentInvalidVal;
	protected JLabel labelLineSpeedVal;

	protected JLabel labelValid;
	protected JLabel labelInvalid;
	protected JLabel labelTotal;
	protected JLabel labelLineSpeed;

	protected JLabel labelUptime;
	protected UptimeLabel labelUptimeVal;

	protected JLabel labelCodeFreq;
	protected JLabel labelCodeFreqVal;
	protected JLabel labelTitle;

	public DefaultStatisticsPanel() {
		this(new StatisticsModel());
	}

	public DefaultStatisticsPanel(final StatisticsModel model) {
		super(model);
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("hidemode 3,inset 0 0 0 0 ", "[]100[][]", "[]20[]5[]5[]15[][]"));
		add(getLabelTitle(), ",gapleft 5,split 2, spanx");
		JSeparator sepa = new JSeparator();
		sepa.setBackground(SicpaColor.BLUE_DARK);
		add(sepa, "growx,gapright 5");

		add(getLabelValid(), "gapleft 15");
		add(getLabelNumberValidVal(), "grow, w 25%,pushx");
		add(getLabelPercentValidVal(), "wrap,grow,w 25%");

		add(getLabelInvalid(), "gapleft 15");
		add(getLabelNumberInvalidVal(), "");
		add(getLabelPercentInvalidVal(), ",wrap");

		add(getLabelTotal(), "gapleft 15");
		add(getLabelNumberTotalVal(), "wrap");

		add(getLabelLineSpeed(), "gapleft 15");
		add(getLabelLineSpeedVal(), "wrap");

		add(getLabelCodeFreq(), "gapleft 15");
		add(getLabelCodeFreqVal(), "wrap");

		add(getLabelUptime(), "gapleft 15");
		add(getLabelUptimeVal(), "");
	}

	public JLabel getLabelTitle() {
		if (this.labelTitle == null) {
			this.labelTitle = new JLabel(GUIi18nManager.get(I18N_LABEL_TITLE));
			this.labelTitle.setName(I18N_LABEL_TITLE);
			this.labelTitle.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelTitle;
	}

	@Override
	protected void modelPropertyChanged(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(StatisticsModel.PROPERTY_VALID)) {
			updateCodeValues();
		} else if (evt.getPropertyName().equals(StatisticsModel.PROPERTY_INVALID)) {
			updateCodeValues();
		} else if (evt.getPropertyName().equals(StatisticsModel.PROPERTY_LINESPEED)) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getLabelLineSpeedVal().setText(getModel().getLineSpeed() + " m/min");
				}
			});
		} else if (evt.getPropertyName().equals(StatisticsModel.PROPERTY_CODEFREQ)) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getLabelCodeFreqVal().setText(getModel().getCodeFreq() + "");
				}
			});
		} else if (evt.getPropertyName().equals(StatisticsModel.PROPERTY_UPTIME)) {
			if (evt.getNewValue().equals("start")) {
				getLabelUptimeVal().start();
			} else {
				getLabelUptimeVal().stop();
			}
		}
	}

	public JLabel getLabelNumberValidVal() {
		if (this.labelNumberValidVal == null) {
			this.labelNumberValidVal = new JLabel("0");
			this.labelNumberValidVal.setForeground(SicpaColor.GREEN_DARK);
			this.labelNumberValidVal.setName("labelNumberValid");
		}
		return this.labelNumberValidVal;
	}

	public JLabel getLabelNumberInvalidVal() {
		if (this.labelNumberInvalidVal == null) {
			this.labelNumberInvalidVal = new JLabel("0");
			this.labelNumberInvalidVal.setForeground(SicpaColor.RED);
			this.labelNumberInvalidVal.setName("labelNumberInvalid");
		}
		return this.labelNumberInvalidVal;
	}

	public JLabel getLabelNumberTotalVal() {
		if (this.labelNumberTotalVal == null) {
			this.labelNumberTotalVal = new JLabel("0");
			this.labelNumberTotalVal.setName("labelNumberTotal");
		}
		return this.labelNumberTotalVal;
	}

	public JLabel getLabelPercentValidVal() {
		if (this.labelPercentValidVal == null) {
			this.labelPercentValidVal = new JLabel("-");
			this.labelPercentValidVal.setForeground(SicpaColor.GREEN_DARK);
			this.labelPercentValidVal.setName("labelPercentValid");
		}
		return this.labelPercentValidVal;
	}

	public JLabel getLabelPercentInvalidVal() {
		if (this.labelPercentInvalidVal == null) {
			this.labelPercentInvalidVal = new JLabel("-");
			this.labelPercentInvalidVal.setForeground(SicpaColor.RED);
			this.labelPercentInvalidVal.setName("labelPercentInvalid");
		}
		return this.labelPercentInvalidVal;
	}

	public JLabel getLabelLineSpeedVal() {

		if (this.labelLineSpeedVal == null) {
			this.labelLineSpeedVal = new JLabel("0 m/min");
			// this.labelLineSpeedVal.setForeground(SicpaColor.BLUE_DARK);
			this.labelLineSpeedVal.setName("labelLineSpeed");
		}
		return this.labelLineSpeedVal;
	}

	@Override
	public void setModel(final StatisticsModel model) {
		super.setModel(model);
		updateCodeValues();

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabelLineSpeedVal().setText(getModel().getLineSpeed() + " m/min");
			}
		});
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabelCodeFreqVal().setText(getModel().getCodeFreq() + "");
			}
		});
	}

	protected void updateCodeValues() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabelNumberValidVal().setText(formatNumber(getModel().getValid()));
				getLabelNumberInvalidVal().setText(formatNumber(getModel().getInvalid()));
				getLabelPercentInvalidVal().setText(formatPercent(getModel().getPercentInvalid()));
				getLabelPercentValidVal().setText(formatPercent(getModel().getPercentValid()));
				getLabelNumberTotalVal().setText(formatNumber(getModel().getTotal()));
			}
		});
	}

	protected String formatNumber(final int codes) {
		NumberFormat formatter = NumberFormat.getInstance();
		return formatter.format(codes);
	}

	protected String formatPercent(final float percent) {

		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(2);
		if (Float.isNaN(percent)) {
			return "- %";
		} else {
			return formatter.format(percent) + "%";
		}
	}

	public JLabel getLabelValid() {
		if (this.labelValid == null) {
			this.labelValid = new JLabel(GUIi18nManager.get(I18N_LABEL_VALID_CODE));
			this.labelValid.setName(I18N_LABEL_VALID_CODE);
			this.labelValid.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelValid;
	}

	public JLabel getLabelInvalid() {
		if (this.labelInvalid == null) {
			this.labelInvalid = new JLabel(GUIi18nManager.get(I18N_LABEL_INVALID_CODE));
			this.labelInvalid.setName(I18N_LABEL_INVALID_CODE);
			this.labelInvalid.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelInvalid;
	}

	public JLabel getLabelTotal() {
		if (this.labelTotal == null) {
			this.labelTotal = new JLabel(GUIi18nManager.get(I18N_LABEL_TOTAL_CODE));
			this.labelTotal.setName(I18N_LABEL_TOTAL_CODE);
			this.labelTotal.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelTotal;
	}

	public JLabel getLabelLineSpeed() {
		if (this.labelLineSpeed == null) {
			this.labelLineSpeed = new JLabel(GUIi18nManager.get(I18N_LABEL_LINE_SPEED));
			this.labelLineSpeed.setName(I18N_LABEL_LINE_SPEED);
			this.labelLineSpeed.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelLineSpeed;
	}

	public JLabel getLabelCodeFreq() {
		if (this.labelCodeFreq == null) {
			this.labelCodeFreq = new JLabel(GUIi18nManager.get(I18N_LABEL_CODEFREQ));
			this.labelCodeFreq.setName("I18N_LABEL_CODEFREQ");
			this.labelCodeFreq.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelCodeFreq;
	}

	public JLabel getLabelCodeFreqVal() {
		if (this.labelCodeFreqVal == null) {
			this.labelCodeFreqVal = new JLabel();
			// this.labelCodeFreqVal.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelCodeFreqVal;
	}

	public JLabel getLabelUptime() {
		if (this.labelUptime == null) {
			this.labelUptime = new JLabel(GUIi18nManager.get(I18N_LABEL_UPTIME));
			this.labelUptime.setName(I18N_LABEL_UPTIME);
			this.labelUptime.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelUptime;
	}

	public UptimeLabel getLabelUptimeVal() {
		if (this.labelUptimeVal == null) {
			this.labelUptimeVal = new UptimeLabel();
			// this.labelUptimeVal.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelUptimeVal;
	}
}
