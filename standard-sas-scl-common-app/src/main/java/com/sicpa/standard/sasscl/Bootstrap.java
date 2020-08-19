package com.sicpa.standard.sasscl;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.gui.components.virtualKeyboard.SpinnerNumericVirtualKeyboard;
import com.sicpa.standard.sasscl.business.statistics.StatisticsRestoredEvent;
import com.sicpa.standard.sasscl.business.statistics.impl.Statistics;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.device.group.IGroupDevicesController;
import com.sicpa.standard.sasscl.controller.flow.IBootstrap;
import com.sicpa.standard.sasscl.controller.productionconfig.validator.ProductionParametersValidator;
import com.sicpa.standard.sasscl.controller.scheduling.RemoteServerScheduledJobs;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.event.UserLoginEvent;
import com.sicpa.standard.sasscl.event.UserLogoutEvent;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.mbean.scl.SclAppMBean;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.sicpadata.generator.validator.IEncoderSequenceValidator;
import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.sasscl.view.lineid.LineIdWithAuthenticateButton;
import com.sicpa.standard.sicpadata.spi.manager.IServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.manager.StaticServiceProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

import static com.sicpa.standard.sasscl.devices.remote.IRemoteServer.ERROR_DEFAULT_SUBSYSTEM_ID;

public class Bootstrap implements IBootstrap {

    protected static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private static final String JMX_BEAN_NAME = "com.sicpa.standard.sasscl.monitoring.mbean:type=SclApp";

    private IRemoteServer server;
    private IGroupDevicesController startupDevicesGroup;
    protected IStorage storage;
    private IPlcValuesLoader plcLoader;
    private SkuListProvider skuListProvider;
    private AuthenticatorProvider authenticatorProvider;
    private SubsystemIdProvider subsystemIdProvider;
    protected Statistics statistics;
    private IServiceProviderManager cryptoProviderManager;
    private List<PlcVariableGroup> linePlcVarGroup;
    private List<PlcVariableGroup> cabPlcVarGroups;
    private SclAppMBean jmxBean;
    private IEncoderSequenceValidator encoderSequenceValidator;
    private ISkuSelectionBehavior skuSelectionBehavior;
    private String defaultLang = "en";
    protected ProductionParameters productionParameters;
    protected RemoteServerScheduledJobs remoteServerSheduledJobs;
    protected ProductionParametersValidator productionParametersValidator;

    @Override
    public void executeSpringInitTasks() {
        initPlc();
        initProductionParameter();
        initAuthenticator();
        initCrypto();
        validateEncoderSequence();
        addConnectionListenerOnServer();
        connectStartupDevices();

        if (skuSelectionBehavior.isLoadPreviousSelection()) {
            restorePreviousSelectedProductionParams();
        }

        installJMXBeans();
        initLoginListener();
        setJVMParameters();
        loginDefaultLoginUser();
    }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt){
        defaultLang = evt.getLanguage();
        setJVMParameters();
        logger.info("destroy_spinner_keyboard_static_instance_with_old_locale");
        SpinnerNumericVirtualKeyboard.destroyInstance();
    }

    private void setJVMParameters() {
        System.setProperty("local.language",defaultLang);
        logger.info("set_jvm_parameter,param=local.language,value=" + defaultLang);
    }

    private void loginDefaultLoginUser() {
        String login = SecurityService.getCurrentUser().getLogin();

        OperatorLogger.log("Default user login: {}", login);
        MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.USER_LOGIN, "Default login: " + login));
        EventBusService.post(new UserLoginEvent());
    }

    private void initLoginListener() {
        SecurityService.addLoginListener(new ILoginListener() {
            @Override
            public void loginSucceeded(String login) {
                OperatorLogger.log("User login: {}", login);
                MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.USER_LOGIN, "Login: " + login));
                EventBusService.post(new UserLoginEvent());
            }

            @Override
            public void logoutCompleted(String login) {
                OperatorLogger.log("User logout: {}", login);
                MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.USER_LOGIN, "Logout: " + login));
                EventBusService.post(new UserLogoutEvent());
            }
        });
    }

    private void addConnectionListenerOnServer() {
        server.addDeviceStatusListener(evt -> {
            if (evt.getStatus() == DeviceStatus.CONNECTED) {
                initRemoteServerConnected();
            }
        });
    }

    private void connectStartupDevices() {
        startupDevicesGroup.start();
    }

    protected void restorePreviousSelectedProductionParams() {
        ProductionParameters previous = storage.getSelectedProductionParameters();
        if (productionParametersValidator.validate(previous)) {
            productionParameters.setBarcode(previous.getBarcode());
            productionParameters.setSku(previous.getSku());
            productionParameters.setProductionMode(previous.getProductionMode());
            EventBusService.post(new ProductionParametersEvent(previous));
            restoreStatistics();
        }
    }

    private void initPlc() {
        generateAllEditableVariableGroup(plcLoader.getValues());
    }

    private void initProductionParameter() {
        skuListProvider.set(storage.getProductionParameters());
    }

    private void initAuthenticator() {
        authenticatorProvider.set(storage.getAuthenticator());
    }

    protected void initRemoteServerConnected() {
        initSubsystemId();
        remoteServerSheduledJobs.executeInitialTasks();
    }

    protected void initSubsystemId() {
        long subsystemId = getSubsystemIdFromRemoteServer();
        if (subsystemId > ERROR_DEFAULT_SUBSYSTEM_ID) {
            setAndSaveSubsystemId(subsystemId);
        }
    }

    private void setAndSaveSubsystemId(long subsystemId) {
        subsystemIdProvider.set(subsystemId);
        saveSubsystemId(subsystemId);
    }

    private void saveSubsystemId(long id) {
        try {
        	Properties properties = new Properties();
            File globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();
            properties.load(new FileInputStream(globalPropertiesFile));
            
            PropertiesUtils.savePropertiesKeepOrderAndComment(globalPropertiesFile, "subsystemId", Long.toString(id));
            
            if (StringUtils.isBlank(properties.getProperty("lineId"))) {
            	PropertiesUtils.savePropertiesKeepOrderAndComment(globalPropertiesFile, "lineId", Long.toString(id));
            	updateLineIdText(Long.toString(id));
            }
        } catch (Exception ex) {
            logger.error("Failed to save subsystem Id, line Id", ex);
        }
    }
    
    private void updateLineIdText(String id) {
    	String lineId = Messages.get("lineId") + MainFrameController.LINE_LABEL_SEPARATOR + id;
    	((LineIdWithAuthenticateButton) getMainFrame().getLineIdPanel()).getLabelLineId().setText(lineId);
	}
    
    private MainFrame getMainFrame() {
		IGUIComponentGetter compGetter = BeanProvider.getBean(BeansName.MAIN_FRAME);
		return (MainFrame) compGetter.getComponent();
	}

    private long getSubsystemIdFromRemoteServer() {
        return server.getSubsystemID();
    }

    protected void restoreStatistics() {
        StatisticsValues statsValues = storage.getStatistics();
        boolean restored = false;
        if (statsValues != null) {
            logger.debug("Restoring statistics {} ", statsValues.getMapValues());
            restored = true;
        } else {
            statsValues = new StatisticsValues();
        }
        statistics.setValues(statsValues);
        if (restored) {
            EventBusService.post(new StatisticsRestoredEvent(statsValues));
        }
    }

    private void initCrypto() {
        StaticServiceProviderManager.register(cryptoProviderManager);
    }

    private void validateEncoderSequence() {
        encoderSequenceValidator.validateAndFixSequence();
    }

    private void generateAllEditableVariableGroup(Map<Integer, StringMap> values) {
        generateCabinetGroup(values.get(0));
        generateAllLinesEditableVariable(values);
    }

    private void generateAllLinesEditableVariable(Map<Integer, StringMap> values) {
        for (int i = 1; i < plcLoader.getLineCount() + 1; i++) {
            generateLineEditableVariables(i, linePlcVarGroup, values.get(i));
        }
    }

    private void generateCabinetGroup(StringMap values) {
        for (PlcVariableGroup grp : cabPlcVarGroups) {
            for (PlcVariableDescriptor desc : grp.getPlcVars()) {
                desc.initValue(values.get(desc.getVarName()));
            }
        }
        EventBusService.post(new PlcVariableGroupEvent(cabPlcVarGroups, "cabinet"));
    }

    private void generateLineEditableVariables(int index, List<PlcVariableGroup> lineVarGroups, StringMap values) {
        List<PlcVariableGroup> groups = PlcLineHelper.replaceLinePlaceholder(lineVarGroups, index);
        for (PlcVariableGroup grp : groups) {
            for (PlcVariableDescriptor desc : grp.getPlcVars()) {
                desc.initValue(values.get(desc.getVarName()));
            }
        }
        EventBusService.post(new PlcVariableGroupEvent(groups, "" + index));
    }

    protected void installJMXBeans() {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName(JMX_BEAN_NAME);
            mbs.registerMBean(jmxBean, name);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public void setServer(IRemoteServer server) {
        this.server = server;
    }

    public void setStartupDevicesGroup(IGroupDevicesController startupDevicesGroup) {
        this.startupDevicesGroup = startupDevicesGroup;
    }

    public void setStorage(IStorage storage) {
        this.storage = storage;
    }

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }

    public void setPlcLoader(IPlcValuesLoader plcLoader) {
        this.plcLoader = plcLoader;
    }

    public void setSkuListProvider(SkuListProvider skuListProvider) {
        this.skuListProvider = skuListProvider;
    }

    public void setAuthenticatorProvider(AuthenticatorProvider authenticatorProvider) {
        this.authenticatorProvider = authenticatorProvider;
    }

    public void setRemoteServerSheduledJobs(RemoteServerScheduledJobs remoteServerSheduledJobs) {
        this.remoteServerSheduledJobs = remoteServerSheduledJobs;
    }

    public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
        this.subsystemIdProvider = subsystemIdProvider;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public void setCryptoProviderManager(IServiceProviderManager cryptoProviderManager) {
        this.cryptoProviderManager = cryptoProviderManager;
    }

    public void setLinePlcVarGroup(List<PlcVariableGroup> linePlcVarGroup) {
        this.linePlcVarGroup = linePlcVarGroup;
    }

    public void setCabPlcVarGroups(List<PlcVariableGroup> cabPlcVarGroups) {
        this.cabPlcVarGroups = cabPlcVarGroups;
    }

    public void setJmxBean(SclAppMBean jmxBean) {
        this.jmxBean = jmxBean;
    }

    public void setEncoderSequenceValidator(IEncoderSequenceValidator encoderSequenceValidator) {
        this.encoderSequenceValidator = encoderSequenceValidator;
    }

    public void setSkuSelectionBehavior(ISkuSelectionBehavior skuSelectionBehavior) {
        this.skuSelectionBehavior = skuSelectionBehavior;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public void setProductionParametersValidator(ProductionParametersValidator productionParametersValidator) {
        this.productionParametersValidator = productionParametersValidator;
    }
}
