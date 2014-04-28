package com.sicpa.standard.sasscl;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.launcher.LoaderConfig;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class Main {

	private static Logger logger;

	static {
		File log = new File("./log");

		if (!log.exists()) {
			log.mkdir();
		}

		logger = LoggerFactory.getLogger(Main.class);
	}

	public static void main(final String[] args) {
		logger.info("Starting Application.");

		SicpaLookAndFeel.install();

		SpringConfig config = new SpringConfig();
		// config.put(SpringConfig.OFFLINE_COUNTING, "spring" + File.separator + "offlineCounting.xml");

		new MainApp() {

			protected void checkApplicationEnvironnement() {
			};

			protected String getApplicationVersion() {
				InputStream versionFile = null;
				try {
					versionFile = ClassLoader.getSystemResourceAsStream("version");
					return IOUtils.toString(versionFile);
				} catch (Exception e) {
					return "UNKNOWN_VERSION";
				} finally {
					if (versionFile != null) {
						try {
							versionFile.close();
						} catch (Exception e2) {
						}
					}
				}
			};

		}.loadApplicationAndStart(new LoaderConfig(config, "STANDARD\nSAS", new Runnable() {
			@Override
			public void run() {
				//
				// MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				// try {
				// ObjectName name = new ObjectName("com.sicpa.standard.sasscl.monitoring.mbean.sasscl:type=SassclApp");
				// mbs.registerMBean(BeanProvider.getBean(BeansName.STATS_MBEAN), name);
				//
				// name = new ObjectName("com.sicpa.standard.sasscl.monitoring.mbean.sasscl:type=SassclRemoteControl");
				// mbs.registerMBean(BeanProvider.getBean(BeansName.REMOTE_CONTROL_MBEAN), name);
				//
				// } catch (Exception e) {
				// logger.error("", e);
				// }
			}
		}));

	}
}
