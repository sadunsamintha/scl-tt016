package com.sicpa.standard.sasscl.remoteControl.connector;

import java.io.IOException;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class RemoteControlConnector {
	protected MBeanServerConnection mbsc;
	protected JMXConnector jmxc;

	public static String nextConnectionIP;
	public static String nextConnectionPort;

	public RemoteControlConnector() {

	}

	public Object createControlBean() throws MalformedObjectNameException, NullPointerException, IOException {
		try {

			ObjectName mbeanName = new ObjectName(
					"com.sicpa.standard.sasscl.monitoring.mbean.sasscl:type=SassclRemoteControl");

			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + nextConnectionIP + ":"
					+ nextConnectionPort + "/jmxrmi");
			this.jmxc = JMXConnectorFactory.connect(url, null);
			this.mbsc = this.jmxc.getMBeanServerConnection();
			
			Class<?> clazz=Class.forName(mbsc.getMBeanInfo(mbeanName).getClassName());
			Class<?> interfaze =clazz.getInterfaces()[0];
			

			Object mbeanProxy = JMX.newMBeanProxy(this.mbsc, mbeanName, interfaze, false);
			return mbeanProxy;
		} catch (final Exception e) {
			e.printStackTrace();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, e.getMessage(), "connection error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		return null;
	}

	public void close() throws IOException {
		this.jmxc.close();
	}
}
