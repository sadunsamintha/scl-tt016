package com.sicpa.standard.sasscl.remoteControl;

import javax.swing.SwingUtilities;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.sasscl.remoteControl.connector.RemoteControlConnector;
import com.sicpa.standard.sasscl.remoteControl.connector.manager.ConnectionManager;
import com.sicpa.standard.sasscl.remoteControl.ioc.RemoteControlBeansName;
import com.sicpa.standard.sasscl.remoteControl.ioc.RemoteControlSpringConfig;

public class RemoteControlMain {

	public static void main(final String[] args) {

		SicpaLookAndFeel.install();
		start("", "9999");
	}

	public static void start(String ip, String port) {
		RemoteControlConnector.nextConnectionIP = ip;
		RemoteControlConnector.nextConnectionPort = port;
		BeanProvider.initSpring(new RemoteControlSpringConfig());
		Messages.load("language/sasscl", "en");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					IGUIComponentGetter f = BeanProvider.getBean(RemoteControlBeansName.MAIN_FRAME_GETTER);
					f.getComponent().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					ConnectionManager.showManager();
				}
				
			}
		});

	}
}
