package com.sicpa.tt065.scl;

import com.sicpa.standard.client.common.app.profile.*;
import com.sicpa.standard.client.common.groovy.GroovyLoggerConfigurator;
import com.sicpa.standard.client.common.groovy.SwingEnabledGroovyApplicationContext;
import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.client.common.utils.ClasspathHacker;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.screen.loader.AbstractApplicationLoader;
import com.sicpa.standard.gui.screen.loader.LoadApplicationScreen;
import com.sicpa.standard.gui.utils.WindowsUtils;
import com.sicpa.standard.sasscl.MainApp;
import com.sicpa.standard.sasscl.MainAppWithProfile;
import com.sicpa.standard.sasscl.ioc.PropertyPlaceholderResourcesSASSCL;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TT065MainAppWithProfile extends MainAppWithProfile {

	@Override
	protected List<String> createSpringFilesToLoad() {
		List<String> config =  super.createSpringFilesToLoad();
		config.remove("spring/plc/plc-import.groovy");
		return config;
	}
}
