package com.sicpa.tt016.scl;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.AbstractApplicationContext;

import com.sicpa.standard.client.common.app.profile.LoaderConfigWithProfile;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.ioc.PropertiesFile;
import com.sicpa.standard.client.common.launcher.CommonMainApp;
import com.sicpa.standard.client.common.launcher.display.IProgressDisplay;
import com.sicpa.standard.client.common.launcher.spring.ILoadingMonitor;
import com.sicpa.standard.client.common.launcher.spring.impl.DefaultLoadingMonitor;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.gui.screen.loader.AbstractApplicationLoader;
import com.sicpa.standard.gui.screen.machine.impl.SPL.AbstractSplFrame;
import com.sicpa.standard.sasscl.controller.flow.IBootstrap;
import com.sicpa.standard.sasscl.event.ApplicationVersionEvent;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.ioc.PropertyPlaceholderResourcesSASSCL;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.tt016.util.TT016LangUtils;
import com.sicpa.tt016.view.lineid.TT016LineIdWithAuthenticateButton;

public class TT016MainApp extends CommonMainApp<LoaderConfigWithProfile> {

	private static final Logger logger = LoggerFactory.getLogger(TT016MainApp.class);

	private Properties loadedProperties;

	public TT016MainApp() {
		PropertyPlaceholderResourcesSASSCL.init();
	}

	@Override
	public AbstractApplicationLoader createLoader(LoaderConfigWithProfile config, String... profiles) {
		return new Loader(config, true, profiles);
	}

	@Override
	@Deprecated
	protected void initLog() {

	}

	private void initCustomGuiComponent() {
		AbstractSplFrame.mapPanelClasses.put(AbstractSplFrame.KEY_LINE_ID, TT016LineIdWithAuthenticateButton.class);
	}

	@Override
	protected void doInitEnd() {

		initCustomGuiComponent();

		IBootstrap bootstrap = BeanProvider.getBean(BeansName.BOOTSTRAP);
		bootstrap.executeSpringInitTasks();

		showMainFrame();

		logger.info("Application is started");
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.INFO, SystemEventType.APP_STARTED));
	}

	private MainFrame getMainFrame() {
		IGUIComponentGetter compGetter = BeanProvider.getBean(BeansName.MAIN_FRAME);
		return (MainFrame) compGetter.getComponent();
	}

	private void showMainFrame() {
		MainFrame main = getMainFrame();
		MainFrameController controller = null;
		if (main != null) {
			controller = main.getController();
		}
		if (controller != null) {
			logger.info("application version:" + getApplicationVersion());
			EventBusService.post(new ApplicationVersionEvent(getApplicationVersion()));
			controller.setVisible(true);
		}
	}

	@Override
	protected void initSpring(LoaderConfigWithProfile config, String... profiles) {

		setLoadingProgress("init language", 0);
		initLanguage();
		setLoadingProgress("init spring", 0);

		BeanProvider.initSpring(config.getContext(), createProgressMonitor(progressDisplay));
		if (config.getInitTask() != null) {
			config.getInitTask().run();
		}

	}

	private void initLanguage() {
		TT016LangUtils.initLanguageFiles(getProperties().getProperty("language"));
	}

	protected Properties getProperties() {
		if (loadedProperties != null) {
			return loadedProperties;
		}

		loadedProperties = new PropertiesFile(PropertyPlaceholderResourcesSASSCL.getAllFiles(),
				PropertyPlaceholderResourcesSASSCL.getAllFolders(),
				PropertyPlaceholderResourcesSASSCL.getOverridingProperties());

		return loadedProperties;
	}

	@Override
	protected ILoadingMonitor createProgressMonitor(IProgressDisplay display) {
		return new DefaultLoadingMonitor(display, 0);
	}

	private void setLoadingProgress(String loadingItem, int progress) {
		progressDisplay.setText(loadingItem);
		progressDisplay.setProgress(progress);
	}

	protected void addPropertyPlaceholder(AbstractApplicationContext context) {
		PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		configurer.setProperties(getProperties());
		context.addBeanFactoryPostProcessor(configurer);
	}
}
