package com.sicpa.standard.sasscl;

import java.util.List;
import java.util.Properties;

import com.sicpa.standard.client.common.app.profile.LoaderConfigWithProfile;
import com.sicpa.standard.client.common.app.profile.Profile;
import com.sicpa.standard.client.common.groovy.SwingEnabledGroovyApplicationContext;
import com.sicpa.standard.client.common.launcher.CommonMainApp;
import com.sicpa.standard.client.common.launcher.display.IProgressDisplay;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class MainAppForFunctionnalTest extends MainAppWithProfile {

	public MainAppForFunctionnalTest() {
		progressDisplay = new IProgressDisplay() {
			@Override
			public void setText(String text) {
			}

			@Override
			public void setProgress(int progress) {
			}
		};
	}

	@Override
	protected void checkInstance() {
	}

	@Override
	public String getApplicationVersion() {
		return null;
	}

	@Override
	protected void checkApplicationEnvironnement() {
	}

	public void load(String simpleName) {

		Profile profile = new Profile("testProfile", "profiles/test");
		List<String> filesToLoad = createSpringFilesToLoad();

		initFromProfile(profile, filesToLoad);

		SwingEnabledGroovyApplicationContext context = new SwingEnabledGroovyApplicationContext(
				filesToLoad.toArray(new String[filesToLoad.size()]));
		context.addBinding("profilePath", profile.getPath());
		context.addBinding("profileName", profile.getName());

		Properties props = getProperties();
		context.addBinding("props", props);

		addPropertyPlaceholder(context);

		CommonMainApp<?>.Loader loader = (CommonMainApp<?>.Loader) createLoader(new LoaderConfigWithProfile(context,
				"functional test:\n" + simpleName));
		loader.loadApplication();
		ThreadUtils.invokeAndWait(() -> loader.done());
	}
}
