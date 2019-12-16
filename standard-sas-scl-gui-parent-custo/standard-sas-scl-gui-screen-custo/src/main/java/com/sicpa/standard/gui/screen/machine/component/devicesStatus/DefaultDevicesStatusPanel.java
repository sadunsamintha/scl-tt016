package com.sicpa.standard.gui.screen.machine.component.devicesStatus;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.component.light.LightPanel;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultDevicesStatusPanel extends AbstractDevicesStatusPanel {

	public static final String I18N_LABEL_TITLE = GUIi18nManager.SUFFIX+"devicesStatus.title";

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				DevicesStatusModel model = new DevicesStatusModel();

				DefaultDevicesStatusPanel p = new DefaultDevicesStatusPanel(model);
				model.addDevice("cam", "CAMERA");
				model.addDevice("dms", "DMS");
				model.addDevice("print", "PRINTER");
				model.changeStatus("dms", DeviceStatus.ERROR);
				f.getContentPane().add(p);
				f.setSize(300, 300);
				f.setVisible(true);
			}
		});
	}

	private Map<String, LightPanel> panels;

	private JLabel title;

	public DefaultDevicesStatusPanel(final DevicesStatusModel model) {
		super(model);
		this.panels = new HashMap<String, LightPanel>();
		initGUI();
	}

	public DefaultDevicesStatusPanel() {
		this(new DevicesStatusModel());
	}

	public JLabel getTitle() {
		if (this.title == null) {
			this.title = new JLabel(GUIi18nManager.get(I18N_LABEL_TITLE));
			this.title.setName(I18N_LABEL_TITLE);
			this.title.setForeground(SicpaColor.BLUE_DARK);
			this.title.setFont(SicpaFont.getFont(20));
		}
		return this.title;
	}

	private void initGUI() {
		setLayout(new MigLayout("inset 0 0 0 0"));
		add(getTitle(), "gapleft 5,gaptop 5,span,split 2,pushx");
		JSeparator sepa = new JSeparator();
		sepa.setBackground(SicpaColor.BLUE_DARK);
		add(sepa, "growx, h 2!,gap right 5");
	}

	@Override
	public void setModel(final DevicesStatusModel model) {
		super.setModel(model);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {

				if (DefaultDevicesStatusPanel.this.panels != null) {
					DefaultDevicesStatusPanel.this.panels.clear();
				}
				removeAll();
				add(getTitle());
				for (String key : model.getKeys()) {
					addDeviceInternal(key, model.getLabel(key));
					changeStatusInternal(key, model.getStatus(key));
				}
			}
		});
	}

	private void addDeviceInternal(final String key, final String label) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				LightPanel lp = new LightPanel(SicpaColor.GRAY, 30, label);
				lp.setForeground(SicpaColor.BLUE_DARK);
				DefaultDevicesStatusPanel.this.panels.put(key, lp);
				add(lp, "newline,gapleft 10");
				lp.setName("lightPanel-" + key);
			}
		});
	}

	@Override
	protected void modelStatusChanged(final DeviceStatusChangeEvent evt) {
		changeStatusInternal(evt.getKey(), evt.getNewStatus());
	}

	@Override
	protected void modelDeviceAdded(final DeviceStatusChangeEvent evt) {
		addDeviceInternal(evt.getKey(), evt.getLabel());
	}

	@Override
	protected void modelDeviceRemoved(final DeviceStatusChangeEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				remove(DefaultDevicesStatusPanel.this.panels.get(evt.getKey()));
				DefaultDevicesStatusPanel.this.panels.remove(evt.getKey());
				repaint();
			}
		});
	}

	private void changeStatusInternal(final String key, final DeviceStatus newStatus) {
		// even if not needed EDT wise
		// keep this in the EDT to be sure it's executed after the modelDeviceAdded
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				LightPanel lp = DefaultDevicesStatusPanel.this.panels.get(key);
				lp.showColor(newStatus.getColor());
			}
		});
	}
}
