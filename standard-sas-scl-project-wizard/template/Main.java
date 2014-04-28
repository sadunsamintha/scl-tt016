package ${package};

import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.io.IOUtils;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.sasscl.ioc.*;
import com.sicpa.standard.sasscl.MainApp;
import com.sicpa.standard.sasscl.SasSclLoaderConfig;

public class Main {

	private static StdLogger logger;

	static {
		File log = new File("./log");

		if (!log.exists()) {
			log.mkdir();
		}

		logger = new StdLogger(Main.class);
	}

	public static void main(final String[] args) {
		logger.info("Starting application");

		SicpaLookAndFeel.install();
		SpringConfig config = new ${springConfig}();
		${customSpringConfig}
		new MainApp() {
			protected String getApplicationVersion() {
				InputStream versionFile = null;
				try {
					versionFile = ClassLoader.getSystemResourceAsStream("version");
					return IOUtils.toString(versionFile);
				} catch (Exception e) {
					logger.error("", e);
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
		}.loadApplicationAndStart(new SasSclLoaderConfig(config, "${applicationName}", new Runnable() {
			@Override
			public void run() {
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				try {
					ObjectName name = new ObjectName("com.sicpa.standard.sasscl.monitoring.mbean.sasscl:type=SassclApp");
					mbs.registerMBean(BeanProvider.getBean(BeansName.STATS_MBEAN), name);

					name = new ObjectName("com.sicpa.standard.sasscl.monitoring.mbean.sasscl:type=SassclRemoteControl");
					mbs.registerMBean(BeanProvider.getBean(BeansName.REMOTE_CONTROL_MBEAN), name);

				} catch (Exception e) {
					logger.error("", e);
				}
				// add here customisation.
			}
		}, new Runnable() {
			@Override
			public void run() {
				// add here session customisation

			}
		}));
	}
}
