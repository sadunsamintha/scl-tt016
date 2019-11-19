package com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo;

import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.label.DateTimeLabel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultSystemInfoPanel extends AbstractSystemInfoPanel {

	public static final String I18N_LABEL_TITLE = GUIi18nManager.SUFFIX + "systemInfo.title";
	public static final String I18N_LABEL_VERSION = GUIi18nManager.SUFFIX + "systemInfo.version";
	public static final String I18N_LABEL_DATE = GUIi18nManager.SUFFIX + "systemInfo.dateTime";

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				SystemInfoModel model = new SystemInfoModel();
				model.setVersion("132");

				DefaultSystemInfoPanel p = new DefaultSystemInfoPanel(model);

				f.getContentPane().add(p);
				f.pack();
				f.setVisible(true);
			}
		});
	}

	private JLabel labelVersion;
	private JLabel labelVersionVal;
	private JLabel labelDateTime;
	private DateTimeLabel labelDateVal;
	private JLabel labelTitle;

	public DefaultSystemInfoPanel() {
		this(new SystemInfoModel());
	}

	public DefaultSystemInfoPanel(final SystemInfoModel model) {
		super(model);
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout(", inset 0 0 0 0 ", "[]80[]", "[]20[]15[]"));

		add(getLabelTitle(), "spanx ,split 2,gaptop 5,gapleft 5");
		JSeparator sepa = new JSeparator();
//		sepa.setBackground(SicpaColor.BLUE_DARK);
		add(sepa, "growx,gapright 5");

		add(getLabelVersion(), "gapleft 15");
		add(getLabelVersionVal(), "wrap,pushx");

		add(getLabelDateTime(), "gapleft 15");
		add(getLabelDateVal(), "");

	}

	public JLabel getLabelTitle() {
		if (this.labelTitle == null) {
			this.labelTitle = new JLabel(GUIi18nManager.get(I18N_LABEL_TITLE));
			this.labelTitle.setName(I18N_LABEL_TITLE);
			this.labelTitle.setForeground(SicpaColor.BLUE_DARK);
		}

		return this.labelTitle;
	}

	public JLabel getLabelVersion() {
		if (this.labelVersion == null) {
			this.labelVersion = new JLabel(GUIi18nManager.get(I18N_LABEL_VERSION));
			this.labelVersion.setName(I18N_LABEL_VERSION);
			this.labelVersion.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelVersion;
	}

	public JLabel getLabelVersionVal() {
		if (this.labelVersionVal == null) {
			this.labelVersionVal = new JLabel();
			// this.labelVersionVal.setForeground(SicpaColor.BLUE_DARK);
			this.labelVersionVal.setName("labelVersion");
		}
		return this.labelVersionVal;
	}

	public JLabel getLabelDateTime() {
		if (this.labelDateTime == null) {
			this.labelDateTime = new JLabel(GUIi18nManager.get(I18N_LABEL_DATE));
			this.labelDateTime.setName(I18N_LABEL_DATE);
			this.labelDateTime.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelDateTime;
	}

	public DateTimeLabel getLabelDateVal() {
		if (this.labelDateVal == null) {
			this.labelDateVal = new DateTimeLabel();
			// this.labelDateVal.setForeground(SicpaColor.BLUE_DARK);
			this.labelDateVal.setName("labelDate");
		}
		return this.labelDateVal;
	}

	@Override
	protected void modelPropertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(SystemInfoModel.PROPERTY_VERSION)) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getLabelVersionVal().setText(evt.getNewValue() + "");
				}
			});
		} else if (evt.getPropertyName().equals(SystemInfoModel.PROPERTY_DATE_FORMAT)) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getLabelDateVal().setDateFormat(new SimpleDateFormat(evt.getNewValue() + ""));
				}
			});
		}
	}

	@Override
	public void setModel(final SystemInfoModel model) {
		super.setModel(model);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabelVersionVal().setText(model.getVersion());
			}
		});
	}
}
