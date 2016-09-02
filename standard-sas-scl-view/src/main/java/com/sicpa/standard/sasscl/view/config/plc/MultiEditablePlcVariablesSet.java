package com.sicpa.standard.sasscl.view.config.plc;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

@SuppressWarnings("serial")
public class MultiEditablePlcVariablesSet extends JPanel implements ISecuredComponentGetter {

	protected JButton buttonShowCameraImage;
	protected FrameCameraImage frameCamera;
	protected JScrollPane scroll;
	protected JPanel mainPanel;
	protected Map<String, PlcVariablePanel> panelsLines = new HashMap<>();

	public MultiEditablePlcVariablesSet() {
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill, inset 0 0 0 0, gap 0 0 0 0"));
		add(getButtonShowCameraImage(), "wrap,center, h 50");
		add(SmallScrollBar.createLayerSmallScrollBar(getScroll()), "push,grow");
	}
	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		removeAll();
		mainPanel = null;
		scroll = null;
		initGUI();
		revalidate();
	}

	public JButton getButtonShowCameraImage() {
		if (buttonShowCameraImage == null) {
			buttonShowCameraImage = new JButton("show camera image");
			buttonShowCameraImage.addActionListener((evt) -> buttonShowCameraImageActionPerformed());
		}
		return this.buttonShowCameraImage;
	}

	public FrameCameraImage getFrameCamera() {
		if (frameCamera == null) {
			frameCamera = new FrameCameraImage(SwingUtilities.getWindowAncestor(this));
		}
		return this.frameCamera;
	}

	protected void buttonShowCameraImageActionPerformed() {
		getFrameCamera().startShowAnim();
	}

	public JScrollPane getScroll() {
		if (scroll == null) {
			scroll = new JScrollPane(getMainPanel());
		}
		return scroll;
	}

	public JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new MigLayout("ltr,inset 0 0 0 0 , gap 0 0 0 0, hidemode 3"));
			for (Entry<String, PlcVariablePanel> entry : panelsLines.entrySet()) {
				addLinePanel(entry.getValue(), entry.getKey());
			}
		}
		return mainPanel;
	}

	protected void addLinePanel(JPanel p, String lineId) {
		JButton button = createTogglePanelButton(p);
		getMainPanel().add(button, "spanx , split 3, h 50 , w 75");
		getMainPanel().add(new JLabel(Messages.get("line." + lineId + ".name")), "");
		getMainPanel().add(new JSeparator(), "pushx,growx");
		getMainPanel().add(p, "grow,wrap");

	}

	protected JButton createTogglePanelButton(final JPanel p) {
		JButton button = new JButton("+");
		button.addActionListener((evt) -> toggleButtonActionPerformed(button, p));
		p.setVisible(false);
		return button;
	}

	private void toggleButtonActionPerformed(JButton button, JPanel panel) {
		panel.setVisible(!panel.isVisible());
		String text = panel.isVisible() ? "-" : "+";
		button.setText(text);
	}

	public void newPlcGroupEditable(PlcVariableGroupEvent evt) {
		ThreadUtils.invokeLater(() -> {
			PlcVariablePanel p = panelsLines.get(evt.getLineId());
			if (p == null) {
				p = new PlcVariablePanel(evt.getGroups());
				panelsLines.put(evt.getLineId(), p);
				addLinePanel(p, evt.getLineId());
			}
		});
	}



	public Map<String, PlcVariablePanel> getPanelsLines() {
		return panelsLines;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Permission getPermission() {
		return SasSclPermission.EDIT_PLC_VARIABLES;
	}

	@Override
	public String getTitle() {
		return "label.plc.variables";
	}

	@Subscribe
	public void handleNewPlcGroupEditable(PlcVariableGroupEvent evt) {
		ThreadUtils.invokeLater(() -> newPlcGroupEditable(evt));
	}
}
