package com.sicpa.standard.sasscl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.SwingUtilities;

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
import com.sicpa.standard.gui.screen.loader.AbstractApplicationLoader;
import com.sicpa.standard.gui.screen.loader.LoadApplicationScreen;
import com.sicpa.standard.gui.utils.WindowsUtils;
import com.sicpa.standard.sasscl.ioc.PropertyPlaceholderResourcesSASSCL;

public abstract class MainAppWithProfile extends MainApp implements IProfileSelectorListener {
	private static Logger logger = LoggerFactory.getLogger(MainAppWithProfile.class);

	private static final String PROFILE_NAME_PROPERTIES_KEY_XML = "profile.name";
	private static final String PROFILE_PATH_PROPERTIES_KEY_XML = "profile.path";

	private static final String PROFILE_NAME_PROPERTIES_KEY_GROOVY = "profileName";
	private static final String PROFILE_PATH_PROPERTIES_KEY_GROOVY = "profilePath";

	private static final String PROFILE_ALL_PROPERTIES_KEY_GROOVY = "props";

	private static final String CUSTO_FILE = "context-override.groovy";

	public MainAppWithProfile() {
		PropertyPlaceholderResourcesSASSCL.init();
	}

	public void selectProfile() {

		List<Profile> profiles = Profile.getAllAvailableProfiles();
		if (profiles.isEmpty()) {
			logger.error("no profile available");
			System.exit(-1);
		} else if (profiles.size() == 1) {
			onProfileSelected(new ProfileSelectedEvent(profiles.get(0)));
		} else {
			SwingUtilities.invokeLater(() -> {
				ProfileSelectorView ps = new ProfileSelectorView();
				ps.setLocationRelativeTo(null);
				ps.setVisible(true);
				ps.addProfileSelectorListener(MainAppWithProfile.this);

			});
		}
	}

	@Deprecated
	protected void initLog() {
		// it is made useless by initLogFromProfile

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
	}

	@Override
	protected void startLoading(final String title, final AbstractApplicationLoader loader) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (AppUtils.isHeadless()) {
					loader.execute();
				} else {
					LoadApplicationScreen screen = new LoadApplicationScreen(loader, true);
					WindowsUtils.setOpaque(screen, true);
					screen.setText(title);
					screen.start();
				}
			}
		});
	}

	private void initFromProfile(Profile profile, List<String> filesToLoad) {

		ClasspathHacker.addFile(profile.getPath() + "/spring");
		ClasspathHacker.addFile(profile.getPath() + "/config");

		initLogger(profile);

		File springOverrideFile = new File(profile.getPath() + "/spring/" + CUSTO_FILE);
		if (springOverrideFile.exists()) {
			filesToLoad.add(springOverrideFile.getName());
		}

		PropertyPlaceholderResourcesSASSCL.addProperties(PROFILE_PATH_PROPERTIES_KEY_XML, profile.getPath());
		PropertyPlaceholderResourcesSASSCL.addProperties(PROFILE_NAME_PROPERTIES_KEY_XML, profile.getName());
		PropertyPlaceholderResourcesSASSCL.addFolder(profile.getPath() + "/config");
	}

	private List<String> createSpringFilesToLoad() {
		List<String> config = new ArrayList<>();
		config.add("spring/customizableProperties.xml");
		config.add("spring/activation.xml");
		config.add("spring/statistics.xml");
		config.add("spring/remoteServer.xml");
		config.add("spring/production.xml");
		config.add("spring/provider.xml");
		config.add("spring/camera.xml");
		config.add("spring/config.xml");
		config.add("spring/scheduler.xml");
		config.add("spring/alert.xml");
		config.add("spring/alertCameraCountTask.xml");
		config.add("spring/alertDuplicatedCodeTask.xml");
		config.add("spring/alertCameraIddleTask.xml");
		config.add("spring/storage.xml");
		config.add("spring/misc.xml");
		config.add("spring/view.xml");
		config.add("spring/devicesController.xml");
		config.add("spring/hardwareController.xml");
		config.add("spring/plc.xml");
		config.add("spring/plcSecureModule.xml");
		config.add("spring/plcJmxInfo.xml");
		config.add("spring/plcLineNotificationsTemplate.xml");
		config.add("spring/plcCabinetNotifications.xml");
		config.add("spring/plcCabinetErrorRegister.xml");
		config.add("spring/plcLineErrorRegister.xml");
		config.add("spring/plcRequests.xml");
		config.add("spring/plcCabinetParameters.xml");
		config.add("spring/plcLineParametersTemplate.xml");
		config.add("spring/flowControl.xml");
		config.add("spring/barcode.xml");

		// PLC variables descriptors for cabinet
		config.add("spring/descriptors/plcCabinetVariablesDescriptors.xml");

		config.add("spring/descriptors/plcLineVariablesDescriptorsTemplate.xml");

		config.add("spring/messages.xml");
		config.add("spring/duplicatedCodeFilter.xml");
		config.add("spring/monitoring.xml");
		config.add("spring/security.xml");

		config.add("spring/crypto.xml");
		config.add("spring/errorsRepository.xml");

		config.add("spring/productionConfig.xml");

		config.add("spring/plcVarMap.xml");

		// SCL

		config.add("spring/coding.xml");
		config.add("spring/printer.xml");
		config.add("spring/printer-domino.xml");
		config.add("spring/printer-leibinger.xml");
		config.add("spring/leibingerProtocol.xml");
		config.add("spring/postPackage.xml");
		config.add("spring/schedulerSCL.xml");

		return config;
	}
}
