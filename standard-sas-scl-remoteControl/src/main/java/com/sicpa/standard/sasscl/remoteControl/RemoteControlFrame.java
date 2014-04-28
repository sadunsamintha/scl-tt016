package com.sicpa.standard.sasscl.remoteControl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.divxdede.swing.busy.JBusyComponent;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;
import com.sicpa.standard.sasscl.remoteControl.beanEditor.RemoteBeanEditor;
import com.sicpa.standard.sasscl.remoteControl.connector.manager.ConnectionManager;
import com.sicpa.standard.sasscl.remoteControl.ioc.RemoteControlBeansName;
import com.thoughtworks.xstream.XStream;

public class RemoteControlFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	protected JPanel panelButton;
	protected JScrollPane scroll;
	protected JBusyComponent<JComponent> busyComponent;
	protected JPanel mainPanel;

	public RemoteControlFrame() {
		initGUI();
	}

	private void initGUI() {
		getContentPane().setLayout(new MigLayout("fill"));
		getContentPane().add(getBusyComponent(), "push,grow");
		setSize(1024, 768);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				ConnectionManager.showManager();
			}
		});
	}

	public JPanel getPanelButton() {
		if (this.panelButton == null) {
			this.panelButton = new JPanel();
			this.panelButton.setLayout(new MigLayout(""));
			addOption("BeanViewer", RemoteControlBeansName.BEAN_VIEWER_GETTER);
			addOption("System events", RemoteControlBeansName.SYSTEM_EVENT_GETTER);
			addOption("Production statistics", RemoteControlBeansName.PRODUCTION_STATISTICS_GETTER);
			addOption("Production report", RemoteControlBeansName.REPORT_GETTER);
			addOption("Loggers", RemoteControlBeansName.LOG_LEVEL_PANEL_GETTER);

			addOption("global config", new RemoteBeanEditor(getRemoteBean(BeansName.GLOBAL_CONFIG),
					BeansName.GLOBAL_CONFIG));

			addOption("Storage explorer", RemoteControlBeansName.STORAGE_EXPLORER_GETTER);
			addOption("Log explorer", RemoteControlBeansName.LOG_EXPLORER_GETTER);

			addOption("Threads dump", RemoteControlBeansName.THREAD_DUMP_GETTER);

		}
		return this.panelButton;
	}

	public static Object getRemoteBean(final String beanName) {
		RemoteControlSasMBean bean = BeanProvider.getBean(RemoteControlBeansName.CONTROL_BEAN);
		String b = bean.getXMLBean(beanName);
		if (b == null) {
			return null;
		}
		XStream x = new XStream();
		return x.fromXML(b);
	}

	protected ButtonGroup grp = new ButtonGroup();

	protected void addOption(final String text, final Component panel) {
		ButtonDisplayPanel button = new ButtonDisplayPanel(text, panel);
		this.grp.add(button);
		getPanelButton().add(button, "wrap , sg 1");
	}

	protected void addOption(final String text, final String beanName) {
		IGUIComponentGetter getter = BeanProvider.getBean(beanName);
		addOption(text, getter.getComponent());
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane();
		}
		return this.scroll;
	}

	public void setMainPanel(final JComponent panel) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getScroll().setViewportView(panel);
			}
		});
	}

	private class ButtonDisplayPanel extends ToggleImageAndTextButton {
		private static final long serialVersionUID = 1L;

		public ButtonDisplayPanel(final String text, final Component panelToDisplay) {
			super(text);
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					setMainPanel((JComponent) panelToDisplay);
				}
			});
		}
	}

	public JBusyComponent<JComponent> getBusyComponent() {
		if (this.busyComponent == null) {
			this.busyComponent = new JBusyComponent<JComponent>(getMainPanel());
		}
		return this.busyComponent;
	}

	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel(new MigLayout("fill"));
			this.mainPanel.add(new JScrollPane(getPanelButton()), "grow");
			this.mainPanel.add(getScroll(), "grow,push");
		}
		return this.mainPanel;
	}

	public void setBusy(final boolean busy) {
		getBusyComponent().setBusy(busy);
	}
}
