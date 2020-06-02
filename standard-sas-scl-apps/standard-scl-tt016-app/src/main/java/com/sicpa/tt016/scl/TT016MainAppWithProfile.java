package com.sicpa.tt016.scl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.app.profile.IProfileSelectorListener;
import com.sicpa.standard.client.common.app.profile.LoaderConfigWithProfile;
import com.sicpa.standard.client.common.app.profile.Profile;
import com.sicpa.standard.client.common.app.profile.ProfileSelectedEvent;
import com.sicpa.standard.client.common.app.profile.ProfileSelectorView;
import com.sicpa.standard.client.common.groovy.GroovyLoggerConfigurator;
import com.sicpa.standard.client.common.groovy.SwingEnabledGroovyApplicationContext;
import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.client.common.utils.ClasspathHacker;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.loader.AbstractApplicationLoader;
import com.sicpa.standard.gui.screen.loader.LoadApplicationScreen;
import com.sicpa.standard.gui.utils.WindowsUtils;
import com.sicpa.standard.sasscl.ioc.PropertyPlaceholderResourcesSASSCL;
import com.sicpa.standard.sasscl.profile.SasSclProfile;

public class TT016MainAppWithProfile extends TT016MainApp implements IProfileSelectorListener {
	private static final Logger logger = LoggerFactory.getLogger(TT016MainAppWithProfile.class);

	private static final String PROFILE_NAME_PROPERTIES_KEY_XML = "profile.name";
	private static final String PROFILE_PATH_PROPERTIES_KEY_XML = "profile.path";

	private static final String PROFILE_NAME_PROPERTIES_KEY_GROOVY = "profileName";
	private static final String PROFILE_PATH_PROPERTIES_KEY_GROOVY = "profilePath";

	private static final String PROFILE_ALL_PROPERTIES_KEY_GROOVY = "props";

	private static final String CUSTO_FILE = "context-override.groovy";
	
	public static String profilePath;

	public TT016MainAppWithProfile() {
		makeSureLogFolderExist();
		SicpaLookAndFeelCusto.install();
		LoadApplicationScreen.DOUBLE_BUFFERING_OFF = false;
		PropertyPlaceholderResourcesSASSCL.init();
	}

	private void makeSureLogFolderExist() {
		File log = new File("./log");
		if (!log.exists()) {
			log.mkdir();
		}
	}

	@Override
	public String getApplicationVersion() {
		try (InputStream versionFile = ClassLoader.getSystemResourceAsStream("version")) {
			return IOUtils.toString(versionFile);
		} catch (Exception e) {
			logger.error("", e);
			return "N/A";
		}
	}

	public void selectProfile() {
		selectProfile("");
	}

	public void selectProfile(String profileToRun) {

		List<Profile> profiles = SasSclProfile.getAllAvailableProfiles();
		if (profiles.isEmpty()) {
			logger.error("no profile available");
			System.exit(-1);
		} else {
			if (StringUtils.isNotBlank(profileToRun)) {
				Profile p = extractProfile(profileToRun, profiles);
				onProfileSelected(new ProfileSelectedEvent(p));
			} else {
				if (profiles.size() == 1) {
					onProfileSelected(new ProfileSelectedEvent(profiles.get(0)));
				} else {
					displayProfileSelectionScreen();
				}
			}
		}
	}

	private Profile extractProfile(String profileName, List<Profile> profiles) {
		for (Profile p : profiles) {
			if (p.getName().equals(profileName)) {
				return p;
			}
		}
		throw new IllegalArgumentException("no profile found for name :" + profileName);
	}

	private void displayProfileSelectionScreen() {
		SwingUtilities.invokeLater(() -> {
			ProfileSelectorView ps = new ProfileSelectorView();
			ps.setLocationRelativeTo(null);
			ps.setVisible(true);
			ps.addProfileSelectorListener(TT016MainAppWithProfile.this);
		});
	}

	private void initLogger(Profile profile) {
		StringMap map = new StringMap();
		map.put(PROFILE_PATH_PROPERTIES_KEY_GROOVY, profile.getPath());
		map.put(PROFILE_NAME_PROPERTIES_KEY_GROOVY, profile.getName());
		GroovyLoggerConfigurator lc = new GroovyLoggerConfigurator(map);
		lc.initLogger();
	}

	@Override
	public void onProfileSelected(ProfileSelectedEvent evt) {
		Profile profile = evt.getProfile();

		List<String> filesToLoad = createSpringFilesToLoad();

		initFromProfile(profile, filesToLoad);

		SwingEnabledGroovyApplicationContext context = new SwingEnabledGroovyApplicationContext(
				filesToLoad.toArray(new String[filesToLoad.size()]));
		context.addBinding(PROFILE_PATH_PROPERTIES_KEY_GROOVY, profile.getPath());
		context.addBinding(PROFILE_NAME_PROPERTIES_KEY_GROOVY, profile.getName());

		Properties props = getProperties();
		context.addBinding(PROFILE_ALL_PROPERTIES_KEY_GROOVY, props);

		addPropertyPlaceholder(context);
		loadApplicationAndStart(new LoaderConfigWithProfile(context, profile.getName()));
		
		profilePath = profile.getPath();
	}

	@Override
	protected void startLoading(String title, AbstractApplicationLoader loader) {
		if (AppUtils.isHeadless()) {
			startHeadless(loader);
		} else {
			startAndDisplayLoadScreen(title, loader);
		}
	}

	private void startHeadless(AbstractApplicationLoader loader) {
		loader.execute();
	}

	private void startAndDisplayLoadScreen(String title, AbstractApplicationLoader loader) {
		SwingUtilities.invokeLater(() -> {
			LoadApplicationScreen screen = new LoadApplicationScreen(loader, true);
			WindowsUtils.setOpaque(screen, true);
			screen.setText(title);
			screen.start();
		});
	}

	protected void initFromProfile(Profile profile, List<String> filesToLoad) {

		ClasspathHacker.addFile(profile.getPath() + "/spring");
		ClasspathHacker.addFile(profile.getPath() + "/config");

		initLogger(profile);
		logger.info("loading profile:" + profile.getName());

		File springOverrideFile = new File(profile.getPath() + "/spring/" + CUSTO_FILE);
		if (springOverrideFile.exists()) {
			filesToLoad.add(springOverrideFile.getName());
		}

		PropertyPlaceholderResourcesSASSCL.addProperties(PROFILE_PATH_PROPERTIES_KEY_XML, profile.getPath());
		PropertyPlaceholderResourcesSASSCL.addProperties(PROFILE_NAME_PROPERTIES_KEY_XML, profile.getName());
		PropertyPlaceholderResourcesSASSCL.addFolder(profile.getPath() + "/config");
	}

	protected List<String> createSpringFilesToLoad() {
		List<String> config = new ArrayList<>();
		config.add("spring/init.groovy");
		config.add("spring/activation.xml");
		config.add("spring/statistics.xml");
		config.add("spring/production.groovy");
		config.add("spring/provider.xml");
		config.add("spring/camera/camera-cognex-import.groovy");
		config.add("spring/config.xml");
		config.add("spring/scheduler.xml");
		config.add("spring/alert/alert.xml");
		config.add("spring/alert/alertCameraCountTask.xml");
		config.add("spring/alert/alertDuplicatedCodeTask.xml");
		config.add("spring/alert/alertCameraIddleTask.xml");
		config.add("spring/alert/alertPlcActivationCrossCheckTask.xml");
		config.add("spring/storage/storage-import.groovy");
		config.add("spring/misc.xml");
		config.add("spring/view.xml");
		config.add("spring/devicesController.xml");
		config.add("spring/hardwareController.xml");
		config.add("spring/plc/plc-import.groovy");
		config.add("spring/flowControl.xml");
		config.add("spring/barcode.xml");

		config.add("spring/messages.xml");
		config.add("spring/duplicatedCodeFilter.xml");
		config.add("spring/monitoring.xml");
		config.add("spring/security.xml");

		config.add("spring/crypto.xml");
		config.add("spring/errorsRepository.xml");

		config.add("spring/productionConfig.xml");

		config.add("spring/server/server-import.groovy");

		config.add("spring/brs/brs-import.groovy");
		config.add("spring/bis/bis-import.groovy");
		config.add("spring/skurecognition/skuRecognition-import.groovy");

		config.add("spring/skuSelectionBehavior.groovy");

		// SCL

		config.add("spring/coding.xml");
		config.add("spring/printer/printer-import.groovy");
		config.add("spring/postPackage.xml");

		config.add("spring/bootstrap.groovy");

		// custo has to be last import for bean overriding
		config.add("spring/custo/customization.groovy");

		return config;
	}
}
