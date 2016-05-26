package com.sicpa.standard.sasscl.view.main.systemInfo;

import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.label.DateTimeLabel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

@SuppressWarnings("serial")
public class SystemInfoView extends AbstractView<ISystemInfoViewListener, SystemInfoModel> {

	private static final Logger logger = LoggerFactory.getLogger(SystemInfoView.class);

	protected JLabel labelAppVersionValue;
	protected JLabel labelAppVersionText;

	protected JLabel labelTime;
	protected DateTimeLabel labelDateValue;

	protected JLabel labelServerStatus;

	protected JLabel labelTitle;

	protected JLabel labelPlcVersionText;
	protected JLabel labelPlcVersionValue;

	public SystemInfoView() {
		initGUI();
		handleLanguageSwitch(null);
	}

	protected void initGUI() {
		setLayout(new MigLayout());

		setLayout(new MigLayout("inset 0 0 0 0 ", "[]80[]", "[]20[]15[]"));

		add(getLabelTitle(), "spanx ,split 2,gaptop 5,gapleft 5");
		add(new JSeparator(), "growx,gapright 5");

		add(getLabelAppVersionText(), "gapleft 5");
		add(getLabelAppVersionValue(), "wrap,pushx");

		add(getLabelPlcVersionText(), "gapleft 5");
		add(getLabelPlcVersionValue(), "wrap,pushx");

		add(getLabelTime(), "gapleft 5");
		add(getLabelDateValue(), "");

		add(getLabelServerStatus(), "newline ,gapleft 5 , spanx");
	}

	@Override
	public void modelChanged() {
		getLabelAppVersionValue().setText(model.getAppVersion());
		getLabelPlcVersionValue().setText(model.getPlcVersion());
		getLabelServerStatus().setVisible(!model.isRemoteServerConnected());
	}

	public JLabel getLabelPlcVersionText() {
		if (labelPlcVersionText == null) {
			labelPlcVersionText = new JLabel();
		}
		return labelPlcVersionText;
	}

	public JLabel getLabelPlcVersionValue() {
		if (labelPlcVersionValue == null) {
			labelPlcVersionValue = new JLabel();
		}
		return labelPlcVersionValue;
	}

	public DateTimeLabel getLabelDateValue() {
		if (labelDateValue == null) {
			labelDateValue = new DateTimeLabel();
		}
		return labelDateValue;
	}

	public JLabel getLabelServerStatus() {
		if (labelServerStatus == null) {
			labelServerStatus = new JLabel();
			labelServerStatus = new JLabel();
			labelServerStatus.setForeground(SicpaColor.RED);
		}
		return labelServerStatus;
	}

	public JLabel getLabelTime() {
		if (labelTime == null) {
			labelTime = new JLabel();
		}
		return labelTime;
	}

	public JLabel getLabelAppVersionText() {
		if (labelAppVersionText == null) {
			labelAppVersionText = new JLabel();
		}
		return labelAppVersionText;
	}

	public JLabel getLabelAppVersionValue() {
		if (labelAppVersionValue == null) {
			labelAppVersionValue = new JLabel();
		}
		return labelAppVersionValue;
	}

	protected void updateDateFormat() {
		String pattern = Messages.get("date.pattern.systemTime");
		try {
			getLabelDateValue().setDateFormat(new SimpleDateFormat(pattern));
		} catch (Exception e) {
			logger.error("invalid or pattern not found {}", pattern);
		}
	}

	public JLabel getLabelTitle() {
		if (labelTitle == null) {
			labelTitle = new JLabel();
		}
		return labelTitle;
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		updateDateFormat();
		getLabelTime().setText(Messages.get("systeminfo.date"));
		getLabelServerStatus().setText(Messages.get("remote.server.disconnected"));
		getLabelAppVersionText().setText(Messages.get("systeminfo.app.version"));
		getLabelPlcVersionText().setText(Messages.get("systeminfo.plc.version"));
		getLabelTitle().setText(Messages.get("systeminfo.title"));
	}

}
