package com.sicpa.standard.sasscl.remoteControl.log;

import java.util.Map;

import ch.qos.logback.classic.Level;

import com.sicpa.standard.gui.components.loggers.LogLevelPanel;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;

public class RemoteLogLevelPanel extends LogLevelPanel {

	private static final long serialVersionUID = 1L;

	protected RemoteControlSasMBean controlBean;

	public RemoteLogLevelPanel(final RemoteControlSasMBean controlBean) {
		super(createModel(controlBean));
		this.controlBean = controlBean;
		setLoggersListName(controlBean.getLoggersList());
	}

	@Override
	public RemoteLogLevelPanelModel getModel() {
		return (RemoteLogLevelPanelModel) super.getModel();
	}

	public void setLoggersListName(final Map<String, Level> mapLogger) {
		getModel().setLoggersListName(mapLogger);
	}

	protected static RemoteLogLevelPanelModel createModel(final RemoteControlSasMBean controlBean) {
		RemoteLogLevelPanelModel res = new RemoteLogLevelPanelModel(controlBean);
		res.setLoggersListName(controlBean.getLoggersList());
		return res;
	}
}
