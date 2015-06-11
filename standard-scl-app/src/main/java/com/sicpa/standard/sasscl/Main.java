package com.sicpa.standard.sasscl;

import com.sicpa.standard.client.common.launcher.LoaderConfig;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;
import com.sicpa.standard.sasscl.skucheck.SkuCheckSpringConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

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
		SpringConfig config = new SpringConfigSCL();
		
		// config.put("deviceIncidentContext", "spring/deviceIncidentConsole.xml");
		config.put("postPackage", "spring/postPackage.xml");
		// config.put(SpringConfig.OFFLINE_COUNTING, "spring" + File.separator + "offlineCounting.xml");

		// ------ Adding BIS ---------//
		// config.put(SpringConfig.BIS, "spring" + File.separator + "bis.xml");
		config.put(SpringConfig.SKU_CHECK, SkuCheckSpringConfig.SKU_CHECK_CORE_FILE);
		config.put(SpringConfig.SKU_CHECK_VIEW, SkuCheckSpringConfig.SKU_CHECK_VIEW_FILE);

		// ------ Adding BRS ---------//
		config.put("brs", "spring/brs.xml");
		config.put("brsConfigParameterDescriptors", "spring/brsConfigParameterDescriptors.xml");
		config.put("brsPlc", "spring/brsPlc.xml");
		config.put("brsPlcNotifications", "spring/brsPlcNotifications.xml");
		config.put("brsPlcParameters", "spring/brsPlcParameters.xml");
		config.put("brsPlcRequests", "spring/brsPlcRequests.xml");
		config.put("brsPlcVariablesDescriptors", "spring/brsPlcVariablesDescriptors.xml");
		config.put("brsPlcVariablesMapping", "spring/brsPlcVariablesMapping.xml");
		config.put("skuCheck", "spring/skuCheck.xml");
		config.put("skuCheckView", "spring/skuCheckView.xml");
		
		String[] profiles = new String[1];
		try {
			String fileName = "config/global.properties";			
			URL url = ClassLoader.getSystemResource(fileName);			
			File f = (url == null) ?  new File(fileName) : new File(url.toURI());
			Properties properties = new Properties();
			properties.load(new BufferedReader(new FileReader(f)));
			
			String key = "plcSecure.behavior";
			profiles[0] = key + "." + (String) properties.get(key);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		new MainApp() {
			protected void checkApplicationEnvironnement() {
			};

			protected String getApplicationVersion() {
				InputStream versionFile = null;
				try {
					versionFile = ClassLoader.getSystemResourceAsStream("version");
					return IOUtils.toString(versionFile);
				} catch (Exception e) {
					// logger.error("", e);
					return "UNKNOWN_VERSION";
				} finally {
					if (versionFile != null) {
						try {
							versionFile.close();
						} catch (Exception e2) {
						}
					}
				}
			}
		}.loadApplicationAndStart(new LoaderConfig(config, "STANDARD\nSCL", new Runnable() {
			@Override
			public void run() {

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

				// add here customisation

			}
		}), (String[]) profiles);
	}
}
