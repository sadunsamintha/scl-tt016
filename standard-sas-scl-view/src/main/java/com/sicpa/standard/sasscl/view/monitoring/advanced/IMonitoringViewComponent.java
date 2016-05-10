package com.sicpa.standard.sasscl.view.monitoring.advanced;

import javax.swing.JComponent;

public interface IMonitoringViewComponent {

	void update(PlcMonitoringModel model);

	JComponent getComponent();

	String getConstraints();
}
