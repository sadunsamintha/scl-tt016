package com.sicpa.standard.sasscl.benchmark;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.JBusyComponent;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.benchmark.chart.TimeChart.TypeChart;

public class BenchmarkFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		BenchmarkConfig.load();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeel.install();
				new BenchmarkFrame().setVisible(true);
			}
		});
	}

	private static class PanelKey extends JPanel {
		private static final long serialVersionUID = 1L;
		JList listKey;
		JRadioButton radioAfter;
		JRadioButton radioBefore;

		public PanelKey() {
			initGUI();
		}

		private void initGUI() {
			setLayout(new MigLayout("fill"));
			add(new JScrollPane(getListKey()), "push,grow,spany 2");
			add(getRadioBefore(), "wrap");
			add(getRadioAfter(), "wrap");
			ButtonGroup bg = new ButtonGroup();
			bg.add(this.radioBefore);
			bg.add(this.radioAfter);
		}

		public JList getListKey() {
			if (this.listKey == null) {
				this.listKey = new JList();
			}
			return this.listKey;
		}

		public JRadioButton getRadioAfter() {
			if (this.radioAfter == null) {
				this.radioAfter = new JRadioButton("After");
			}
			return this.radioAfter;
		}

		public JRadioButton getRadioBefore() {
			if (this.radioBefore == null) {
				this.radioBefore = new JRadioButton("Before");
			}
			return this.radioBefore;
		}

		private LogEventType getSelectedEventType() {
			if (this.radioAfter.isSelected()) {
				return LogEventType.after;
			} else {
				return LogEventType.before;
			}
		}
	}

	private JTextField textFile;
	private JButton buttonBrowse;
	private JButton buttonParse;
	private JButton buttonShowChart;
	private JRadioButton radioMarkerMin;
	private JRadioButton radioMarkerSec;
	private JRadioButton radioMarkerHour;
	private JScrollPane scrollChart;
	private PanelKey panelKey1;
	private PanelKey panelKey2;
	private JFileChooser chooser;
	private JRadioButton radioLineChart;
	private JRadioButton radioBarChart;

	private BenchmarkLogParser parser;

	private JBusyComponent<JComponent> busyPanel;
	private JPanel mainPanel;

	public BenchmarkFrame() {
		initGUI();
	}

	private void initGUI() {

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getBusyPanel());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1024, 768);

	}

	public JButton getButtonBrowse() {
		if (this.buttonBrowse == null) {
			this.buttonBrowse = new JButton("Browse");
			this.buttonBrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonBrowseActionPerformed();
				}
			});
		}
		return this.buttonBrowse;
	}

	private void buttonBrowseActionPerformed() {
		if (this.chooser == null) {
			this.chooser = new JFileChooser(new File(".").getAbsolutePath());
		}

		int res = this.chooser.showOpenDialog(this);
		if (res == JFileChooser.APPROVE_OPTION) {
			getTextFile().setText(this.chooser.getSelectedFile().getAbsolutePath());
		}
	}

	public JButton getButtonShowChart() {
		if (this.buttonShowChart == null) {
			this.buttonShowChart = new JButton("show chart");
			this.buttonShowChart.setEnabled(false);
			this.buttonShowChart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonShowChartActionPerformed();
				}
			});
		}
		return this.buttonShowChart;
	}

	private void buttonShowChartActionPerformed() {
		LogEventType type1 = getPanelKey1().getSelectedEventType();
		LogEventType type2 = getPanelKey2().getSelectedEventType();
		String markerLabel;
		int markerDelta;
		if (getRadioMarkerMin().isSelected()) {
			markerDelta = 1000 * 60;
			markerLabel = "min";
		} else if (getRadioMarkerSec().isSelected()) {
			markerDelta = 1000;
			markerLabel = "sec";
		} else {
			markerDelta = 1000 * 3600;
			markerLabel = "hour";
		}

		String key1 = getPanelKey1().listKey.getSelectedValue().toString();
		String key2 = getPanelKey2().listKey.getSelectedValue().toString();

		TypeChart typeChart;
		if (getRadioBarChart().isSelected()) {
			typeChart = TypeChart.bar;
		} else {
			typeChart = TypeChart.line;
		}

		JComponent chart = ChartFactory.getChart(this.parser.getData(), key1, key2, type1, type2, markerLabel,
				markerDelta, typeChart);
		getScrollChart().setViewportView(chart);
	}

	public JScrollPane getScrollChart() {
		if (this.scrollChart == null) {
			this.scrollChart = new JScrollPane();
		}
		return this.scrollChart;
	}

	public JRadioButton getRadioMarkerMin() {
		if (this.radioMarkerMin == null) {
			this.radioMarkerMin = new JRadioButton("min");
			this.radioMarkerMin.setSelected(true);
		}
		return this.radioMarkerMin;
	}

	public JRadioButton getRadioMarkerSec() {
		if (this.radioMarkerSec == null) {
			this.radioMarkerSec = new JRadioButton("sec");
		}
		return this.radioMarkerSec;
	}

	public JRadioButton getRadioMarkerHour() {
		if (radioMarkerHour == null) {
			radioMarkerHour = new JRadioButton("hour");
		}
		return radioMarkerHour;
	}

	public JTextField getTextFile() {
		if (this.textFile == null) {
			this.textFile = new JTextField();
			this.textFile.setText(BenchmarkConfig.getProperty(BenchmarkConfig.LAST_PARSED_FILE));
		}
		return this.textFile;
	}

	public JButton getButtonParse() {
		if (this.buttonParse == null) {
			this.buttonParse = new JButton("Parse file");
			this.buttonParse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonParseActionPerformed();
				}
			});
		}
		return this.buttonParse;
	}

	long cptByteRead = 0;

	public void setProgress(long byteRead) {
		cptByteRead += byteRead;
		File f = new File(getTextFile().getText());
		long size = f.length();
		final int progress = (int) (((double) cptByteRead / size) * 100);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getBusyPanel().getBusyModel().setValue(progress);
			}
		});

	}

	private void buttonParseActionPerformed() {
		getScrollChart().setViewportView(null);
		getBusyPanel().setBusy(true);
		cptByteRead = 0;
		setProgress(0);

		new Thread(new Runnable() {
			@Override
			public void run() {
				saveCurrentSelection();
				parser = new BenchmarkLogParser(BenchmarkFrame.this);
				parser.parse(getTextFile().getText());
				populateListKey();
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						getBusyPanel().setBusy(false);
					}
				});
			}
		}).start();

	}

	private void saveCurrentSelection() {
		BenchmarkConfig.putProperties(BenchmarkConfig.LAST_PARSED_FILE, getTextFile().getText());
		BenchmarkConfig.save();
	}

	private void populateListKey() {
		if (this.parser != null) {
			Set<String> keys = new TreeSet<String>();
			for (LogEvent evt : this.parser.getData().getEvents()) {
				keys.add(evt.getKey());
			}

			DefaultListModel model1 = new DefaultListModel();
			DefaultListModel model2 = new DefaultListModel();
			for (String key : keys) {
				model1.addElement(key);
				model2.addElement(key);
			}

			getPanelKey1().listKey.setModel(model1);
			getPanelKey2().listKey.setModel(model2);
		}
	}

	public PanelKey getPanelKey1() {
		if (this.panelKey1 == null) {
			this.panelKey1 = new PanelKey();
			this.panelKey1.radioBefore.setSelected(true);
			this.panelKey1.listKey.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(final ListSelectionEvent e) {
					panelKeyListSelectionChanged();
				}
			});
		}
		return this.panelKey1;
	}

	private void panelKeyListSelectionChanged() {
		this.buttonShowChart.setEnabled(getPanelKey1().listKey.getSelectedIndex() != -1
				&& getPanelKey2().listKey.getSelectedIndex() != -1);
	}

	public PanelKey getPanelKey2() {
		if (this.panelKey2 == null) {
			this.panelKey2 = new PanelKey();
			this.panelKey2.radioAfter.setSelected(true);
			this.panelKey2.listKey.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(final ListSelectionEvent e) {
					panelKeyListSelectionChanged();
				}
			});
		}
		return this.panelKey2;
	}

	public JRadioButton getRadioBarChart() {
		if (this.radioBarChart == null) {
			this.radioBarChart = new JRadioButton("Bar chart");
			this.radioBarChart.setSelected(true);
		}
		return this.radioBarChart;
	}

	public JRadioButton getRadioLineChart() {
		if (this.radioLineChart == null) {
			this.radioLineChart = new JRadioButton("Line chart");
		}
		return this.radioLineChart;
	}

	public JBusyComponent<JComponent> getBusyPanel() {
		if (busyPanel == null) {
			busyPanel = new JBusyComponent<JComponent>(getMainPanel());
			getBusyPanel().getBusyModel().setDeterminate(true);
		}
		return busyPanel;
	}

	public JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();

			mainPanel.setLayout(new MigLayout("fill"));
			mainPanel.add(new JLabel("File:"), "split 2");
			mainPanel.add(getTextFile(), "grow");
			mainPanel.add(getButtonBrowse());
			mainPanel.add(getButtonParse(), "wrap");
			mainPanel.add(getPanelKey1(), "grow,spanx , split2");
			mainPanel.add(getPanelKey2(), "grow");
			mainPanel.add(new JLabel("Marker:"), "split 4");
			mainPanel.add(getRadioMarkerMin());
			mainPanel.add(getRadioMarkerSec());
			mainPanel.add(getRadioMarkerHour());
			mainPanel.add(getRadioBarChart(), "split 2");
			mainPanel.add(getRadioLineChart());
			mainPanel.add(getButtonShowChart(), "wrap");
			mainPanel.add(getScrollChart(), "push,grow,spanx");

			ButtonGroup bg = new ButtonGroup();
			bg.add(getRadioMarkerMin());
			bg.add(getRadioMarkerSec());
			bg.add(getRadioMarkerHour());

			bg = new ButtonGroup();
			bg.add(getRadioLineChart());
			bg.add(getRadioBarChart());
		}
		return mainPanel;
	}
}
