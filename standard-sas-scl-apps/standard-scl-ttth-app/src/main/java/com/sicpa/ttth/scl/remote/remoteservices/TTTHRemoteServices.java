package com.sicpa.ttth.scl.remote.remoteservices;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.ejb.client.EJBClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import com.sicpa.gssd.ttth.common.api.activation.business.TTTHActivationServiceHandler;
import com.sicpa.gssd.ttth.common.api.activation.dto.ValidCodedProductsDto;
import com.sicpa.standard.client.common.timeout.Timeout;
import com.sicpa.standard.client.common.timeout.TimeoutLifeCheck;
import com.sicpa.standard.sasscl.devices.remote.impl.BasicClientSecurityInterceptor;
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.IRemoteServices;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsResultDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;
import com.sicpa.std.common.api.coding.business.CodingServiceHandler;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoResultDto;
import com.sicpa.std.common.api.common.CommonServerRuntimeException;
import com.sicpa.std.common.api.config.business.ConfigurationBusinessHandler;
import com.sicpa.std.common.api.monitoring.business.EventServiceHandler;
import com.sicpa.std.common.api.monitoring.dto.EventDto;
import com.sicpa.std.common.api.monitoring.exception.MonitoringException;
import com.sicpa.std.common.api.multilingual.business.ProvideTranslationBusinessHandler;
import com.sicpa.std.common.api.multilingual.common.CustomResourceBundle;
import com.sicpa.std.common.api.multilingual.dto.AvailableLanguageDto;
import com.sicpa.std.common.api.physical.dto.SubsystemDto;
import com.sicpa.std.common.api.security.business.LoginServiceHandler;
import com.sicpa.std.common.api.security.dto.LoginDto;
import com.sicpa.std.common.api.util.PropertyNames;
import com.sicpa.std.server.util.locator.ServiceLocator;

import static com.sicpa.std.server.util.locator.ServiceLocator.*;

public class TTTHRemoteServices implements IRemoteServices {

    private static final Logger logger = LoggerFactory.getLogger(com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.RemoteServices.class);

    private LoginServiceHandler loginService;
    private ProvideTranslationBusinessHandler translationService;
    private CodingServiceHandler codingService;
    private ConfigurationBusinessHandler configService;
    private TTTHActivationServiceHandler activationService;
    private EventServiceHandler eventService;
    private Context context;

    private String userMachine;
    private String passwordMachine;
    private Properties properties;

    private void initClientSecurityContext() {
        EJBClientContext.getCurrent().registerInterceptor(1, new BasicClientSecurityInterceptor());
        logger.info("connecting to remote server using:" + userMachine);
        BasicClientSecurityInterceptor.setPrincipal(userMachine, passwordMachine.toCharArray());
    }

    private void setupContext() throws NamingException {
        // jndi context
        Properties jndiProperties = new Properties();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        context = new InitialContext(jndiProperties);
    }

    @Override
    @TimeoutLifeCheck
    public boolean isAlive() {
        return loginService.isAlive();
    }

    @Override
    @Timeout
    public LoginDto logUserIn(String login, String password) throws Exception {
        return loginService.login(login, password);
    }

    @Override
    @Timeout
    public void registerEvent(EventDto evt) throws MonitoringException {
        eventService.register(evt);
    }

    @Override
    @Timeout
    public SicpadataGeneratorInfoResultDto registerGeneratorsCycle(List<SicpadataGeneratorInfoDto> dtos) {
        return codingService.registerGeneratorsCycle(dtos);
    }

    @Override
    @Timeout
    public SicpadataReaderDto provideSicpadataReader() throws ActivationException, CommonServerRuntimeException {
        return activationService.provideSicpadataReader();
    }

    @Override
    @Timeout
    public Collection<AvailableLanguageDto> getAvailableLanguages() throws Exception {
        return translationService.getAvailableLanguages();
    }

    @Override
    @Timeout
    public CustomResourceBundle getResourceBundle(String lang) throws Exception {
        return translationService.getResourceBundle(lang);
    }

    @Override
    @Timeout
    public AuthorizedProductsDto provideAuthorizedProducts() {
        return activationService.provideAuthorizedProducts();
    }


    @Timeout
    public ValidCodedProductsDto getActualCodedQuantity(String dailyBatchJobId) {
        return activationService.provideValidCodedQuantity(dailyBatchJobId);
    }


    @Override
    public void login() throws Exception {
        initClientSecurityContext();
        setupContext();
        lookupMasterServices();
    }

    private void lookupMasterServices() throws NamingException {
        loginService = createLoginService();
        codingService = createCodingService();
        translationService = createTranslationBean();
        configService = createConfigService();
        activationService = createActivationBean();
        eventService = createEventService();
    }

    @Override
    public CodingServiceHandler getCodingService() {
        return codingService;
    }

    @Override
    @Timeout
    public String getCryptoPassword() {
        Map<String, String> serverConfiguration = configService.getConfiguration(null);
        return serverConfiguration.get(PropertyNames.SICPADATA_ADMIN_PWD);
    }

    @Override
    @Timeout
    public SubsystemDto getSubsystem() throws Exception {
        logger.info("connecting to remote server using:" + userMachine);
        LoginDto dto = loginService.login(userMachine, passwordMachine);
        return dto.getUserDto().getSubsystem();
    }

    private CodingServiceHandler createCodingService() throws NamingException {
        return (CodingServiceHandler) getService(SERVICE_COMMON_CODING_BUSINESS_SERVICE);
    }

    private TTTHActivationServiceHandler createActivationBean() throws NamingException {
        return (TTTHActivationServiceHandler) getService(SERVICE_ACTIVATION_BUSINESS_SERVICE);
    }

    private ConfigurationBusinessHandler createConfigService() throws NamingException {
        return (ConfigurationBusinessHandler) getService(SERVICE_CONFIG_BUSINESS_SERVICE);
    }

    private ProvideTranslationBusinessHandler createTranslationBean() throws NamingException {
        return (ProvideTranslationBusinessHandler) getService(SERVICE_PROVIDE_TRANSLATION_BUSINESS_SERVICE);
    }

    private LoginServiceHandler createLoginService() throws NamingException {
        return (LoginServiceHandler) getService(ServiceLocator.SERVICE_LOGIN_BUSINESS_SERVICE);
    }

    private EventServiceHandler createEventService() throws NamingException {
        return (EventServiceHandler) getService(ServiceLocator.SERVICE_DMS_EVENT_BUSINESS_SERVICE);
    }

    private Object getService(String serviceName) throws NamingException {
        return context.lookup(properties.getProperty(serviceName));
    }

    public void setUserMachine(String userMachine) {
        this.userMachine = userMachine;
    }

    public void setPasswordMachine(String passwordMachine) {
        this.passwordMachine = passwordMachine;
    }

    @Override
    @Timeout
    public void registerActivationProducts(AuthenticatedProductsResultDto data) throws ActivationException {
        activationService.registerAuthenticatedProducts(data);
    }

    @Override
    @Timeout
    public void registerCountedProducts(CountedProductsResultDto data) throws ActivationException {
        activationService.registerCountedProducts(data);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private TTTHActivationServiceHandler getTargetObject(Object proxy, Class targetClass) throws Exception {
        if ( (AopUtils.isJdkDynamicProxy(proxy))) {
            return getTargetObject(((Advised)proxy).getTargetSource().getTarget(), targetClass);
        }
        return (TTTHActivationServiceHandler) proxy;
    }
}
