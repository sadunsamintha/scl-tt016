package com.sicpa.standard.sasscl.view.deviceIncidentContext;

import java.awt.Component;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.color.ColorUtil;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.repository.errors.AppMessage;
import com.sicpa.standard.sasscl.repository.errors.IErrorsRepository;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class DeviceIncidentContextPanel extends JPanel implements ISecuredComponentGetter {

	private static final long serialVersionUID = 1L;

	private IErrorsRepository errorRepository;

	private JTextArea textArea;

	public DeviceIncidentContextPanel() {
		initGUI();
		start();
	}

	public void setErrorRepository(IErrorsRepository errorRepository) {
		this.errorRepository = errorRepository;
	}

	private void initGUI() {

		textArea = new JTextArea();

		setLayout(new MigLayout("fill"));

		add(new JLabel("Device Context Console"), "span,split 2");
		add(new JSeparator(), "growx");

		JComponent comp = SmallScrollBar.createLayerSmallScrollBar(new JScrollPane(textArea));
		comp.setBorder(new LineBorder(ColorUtil.setAlpha(SicpaColor.BLUE_DARK, 55)));
		add(comp, "span,grow,wrap,push");

	}

	/**
	 * 
	 * @param deviceType
	 *            --> type of device : PLC, Printer, Camera , etc
	 * @param log
	 */
	protected void addDeviceStatusLog(StringBuffer log) {

		Map<String, DeviceStatus> deviceStatusMap = errorRepository.getDevicesStatus();

		log.append("DEVICE STATUS:\n");
		for (Entry<String, DeviceStatus> entry : deviceStatusMap.entrySet()) {
			String identifier = entry.getKey();
			log.append(String.format("%s : %s\n", identifier, entry.getValue().toString()));
		}
		log.append("\n");
	}

	protected void addAllLog(StringBuffer log) {

		log.append("ERROR:\n");
		for (AppMessage error : errorRepository.getApplicationErrors()) {
			log.append(error.getTime());
			log.append(error.getCode());
			log.append(" - ");
			log.append(error.getMessage());
			log.append("\n");
		}
		log.append("\nWARNING:\n");
		for (AppMessage error : errorRepository.getApplicationWarnings()) {
			log.append(error.getTime());
			log.append(error.getCode());
			log.append(" - ");
			log.append(error.getMessage());
			log.append("\n");
		}
	}

	protected ApplicationFlowState currentState;

	@Subscribe
	public void applicationStateChanged(ApplicationFlowStateChangedEvent evt) {
		currentState = evt.getCurrentState();
	}

	private void start() {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {

						Thread.sleep(2000);

						StringBuffer log = new StringBuffer();

						if (errorRepository != null) {
							log.append(String.format("application state: %s\n", currentState == null ? ""
									: currentState.getName()));
							addDeviceStatusLog(log);
							addAllLog(log);
						}

						textArea.setText(log.toString());

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});
		t.setDaemon(true);
		t.start();

	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Permission getPermission() {
		return SasSclPermission.DEVICE_CONTEXT_CONSOLE;
	}

	@Override
	public String getTitle() {
		return "device.context.console";
	}

}
