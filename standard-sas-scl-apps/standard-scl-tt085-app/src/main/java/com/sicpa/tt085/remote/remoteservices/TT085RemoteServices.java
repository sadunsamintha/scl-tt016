package com.sicpa.tt085.remote.remoteservices;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb.client.EJBClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrActivationServiceHandler;
import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrAuthenticatedProductsResultDto;
import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrCountedProductsResultDto;
import com.sicpa.standard.client.common.timeout.Timeout;
import com.sicpa.standard.client.common.timeout.TimeoutLifeCheck;
import com.sicpa.standard.sasscl.devices.remote.impl.BasicClientSecurityInterceptor;
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.IRemoteServices;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.ProcessedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.dto.productionData.ProcessedProductsStatusDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsResultDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;
import com.sicpa.std.common.api.coding.business.CodingServiceHandler;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoResultDto;
import com.sicpa.std.common.api.common.CommonServerRuntimeException;
import com.sicpa.std.common.api.monitoring.dto.EventDto;
import com.sicpa.std.common.api.monitoring.exception.MonitoringException;
import com.sicpa.std.common.api.multilingual.common.CustomResourceBundle;
import com.sicpa.std.common.api.multilingual.dto.AvailableLanguageDto;
import com.sicpa.std.common.api.physical.dto.SubsystemDto;
import com.sicpa.std.common.api.security.business.LoginServiceHandler;
import com.sicpa.std.common.api.security.dto.LoginDto;

public class TT085RemoteServices implements ITT085RemoteServices {

	private static final Logger logger = LoggerFactory.getLogger(TT085RemoteServices.class);
	
	private final String LOGIN_BUSINESS_SERVICE_URL = "ejb:/standard-msca-services/loginBusinessServiceMSCA!com.sicpa.std.common.api.security.business.LoginServiceHandler";
	private final String SERVICE_ACTIVATION_BUSINESS_SERVICE = "ejb:/standard-msca-services/trActivationBusinessServiceMSCA!com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrActivationServiceHandler";
	private final String SERVICE_COMMON_CODING_BUSINESS_SERVICE = "ejb:/standard-msca-services/codingBusinessServiceMSCA!com.sicpa.std.common.api.coding.business.CodingServiceHandler";
	
	
	private LoginServiceHandler loginService;
	private TrActivationServiceHandler activationService;
	private CodingServiceHandler codingService;
	
	private Context context;
	
	private String userMachine;
	private String passwordMachine;
	private Properties properties;
	
	@Override
	public void login() throws Exception {
		initClientSecurityContext();
		setupContext();
		lookupMasterServices();
	}
	
	@Override
	@Timeout
	public LoginDto logUserIn(String login, String password) throws Exception {
		return loginService.login(login, password);
	}
	
	@Override
	@TimeoutLifeCheck
	public boolean isAlive() {
		return loginService.isAlive();
	}
	
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
	
	private void lookupMasterServices() throws NamingException {
		loginService = createLoginService();
		codingService = createCodingService();
//		translationService = createTranslationBean();
//		configService = createConfigService();
		activationService = createActivationBean();
//		eventService = createEventService();
	}
	
	@Override
	public CodingServiceHandler getCodingService() {
		return codingService;
	}
	
	private LoginServiceHandler createLoginService() throws NamingException {
		return (LoginServiceHandler) getService(LOGIN_BUSINESS_SERVICE_URL);
	}
	
	private TrActivationServiceHandler createActivationBean() throws NamingException {
		return (TrActivationServiceHandler) getService(SERVICE_ACTIVATION_BUSINESS_SERVICE);
	}
	
	private CodingServiceHandler createCodingService() throws NamingException {
		return (CodingServiceHandler) getService(SERVICE_COMMON_CODING_BUSINESS_SERVICE);
	}
	
	private Object getService(String serviceName) throws NamingException {
		return context.lookup(serviceName);
	}
	
	public void setUserMachine(String userMachine) {
		this.userMachine = userMachine;
	}

	public void setPasswordMachine(String passwordMachine) {
		this.passwordMachine = passwordMachine;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	@Override
	@Timeout
	public SubsystemDto getSubsystem() throws Exception {
		logger.info("connecting to remote server using:" + userMachine);
		LoginDto dto = loginService.login(userMachine, passwordMachine);
		return dto.getUserDto().getSubsystem();
	}

	@Override
	@Timeout
	public AuthorizedProductsDto provideAuthorizedProducts() {
		AuthorizedProductsDto authProductsDto = activationService.provideAuthorizedProducts();
		return authProductsDto;
	}
	
	@Override
	public SicpadataReaderDto provideSicpadataReader() throws ActivationException, CommonServerRuntimeException {
		return activationService.provideSicpadataReader();
	}

	@Override
	public void registerActivationProducts(TrAuthenticatedProductsResultDto data) throws ActivationException {
		try {
			activationService.registerAuthenticatedProducts(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	@Timeout
	public void registerCountedProducts(TrCountedProductsResultDto trCountedProductsResultDto) throws ActivationException {
		try {
			activationService.registerCountedProducts(trCountedProductsResultDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@Timeout
	public SicpadataGeneratorInfoResultDto registerGeneratorsCycle(List<SicpadataGeneratorInfoDto> dtos) {
		return codingService.registerGeneratorsCycle(dtos);
	}
	
	public void processCountedProducts(List<CountedProductsDto> countedProductsDto, PackagedProducts products) {
		TrCountedProductsResultDto trCountedProductsResultDto = new TrCountedProductsResultDto();
		for(CountedProductsDto cp : countedProductsDto) {
		 populateResultDtoInfo(trCountedProductsResultDto, products);
		 trCountedProductsResultDto.setProcessedProducts(cp);
		 ProcessedProductsStatusDto processedProductStatusDto = new ProcessedProductsStatusDto();
		 processedProductStatusDto.setValue(getTurkeyRemoteServerProductStatus(products
					.getProductStatus()));
		 trCountedProductsResultDto.setProcessedProductsStatusDto(processedProductStatusDto);
		 try {
			activationService.registerCountedProducts(trCountedProductsResultDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	}
	
	protected void populateResultDtoInfo(final ProcessedProductsResultDto dto, final PackagedProducts products) {
        dto.setSubsystemId(products.getSubsystem());
        dto.setIdTransaction(System.currentTimeMillis());
        dto.setIdProductionBatch(String.valueOf(System.currentTimeMillis()));
    }
	
	public int getTurkeyRemoteServerProductStatus(ProductStatus prodStatus) {
		switch(prodStatus.getId()) {
		
		//UNREAD  --> COUNTED_EJECTED_UNREAD
		case 0:
			return ProcessedProductsStatusDto.COUNTED_EJECTED_UNREAD;
			
		//NOT_AUTHENTICATED  --> COUNTED_EJECTED_AUTHENTICATIONFAIL
		case 1:
			return ProcessedProductsStatusDto.COUNTED_EJECTED_AUTHENTICATIONFAIL;
			
		//AUTHENTICATED --> AUTHENTICATED_ACTIVATED
		case 2:
			
			return ProcessedProductsStatusDto.AUTHENTICATED_ACTIVATED;
			
		//TYPE_MISMATCH --> AUTHENTICATED_ACTIVATED_TYPEMISSMATCH
		case 3:
			return ProcessedProductsStatusDto.AUTHENTICATED_ACTIVATED_TYPEMISSMATCH;
			
		//EXPORT --> COUNTED_ACTIVATED_EXPORT
		case 4:
			return ProcessedProductsStatusDto.COUNTED_ACTIVATED_EXPORT;
		//MAINTENANCE --> COUNTED_MAINTENANCE
		case 5:
			return ProcessedProductsStatusDto.COUNTED_MAINTENANCE;
			
		//SENT_TO_PRINTER_WASTED --> AUTHENTICATED_INTERNAL_SENTPRINTER_WASTED
		case 6:
			return ProcessedProductsStatusDto.AUTHENTICATED_INTERNAL_SENTPRINTER_WASTED;
			
		//SENT_TO_PRINTER_UNREAD --> AUTHENTICATED_INTERNAL_SENTPRINTER_NOCAMERA
		case 7:
			return ProcessedProductsStatusDto.AUTHENTICATED_INTERNAL_SENTPRINTER_NOCAMERA;
			
		//COUNTING  --> COUNTED_ACTIVATED_COUNTING
		case 8:
			return ProcessedProductsStatusDto.COUNTED_ACTIVATED_COUNTING;
			
		//NO_INK --> COUNTED_EJECTED_NOINK	
		case 9:
			return ProcessedProductsStatusDto.COUNTED_EJECTED_NOINK;
			
		//OFFLINE
		case 10:
			return 32;
			
		//REFEED  --> AUTHENTICATED_REFEED_AUTHORIZED
		case 11:
			return ProcessedProductsStatusDto.AUTHENTICATED_REFEED_AUTHORIZED;
			
		//EJECTED_PRODUCER  --> AUTHENTICATED_EJECTED_BYPRODUCER
		case 12:
			return ProcessedProductsStatusDto.AUTHENTICATED_EJECTED_BYPRODUCER;
			
		//INK_DETECTED --> There is no matching status in TURKEY LEGACY MASTER
		case 100:
			return ProcessedProductsStatusDto.AUTHENTICATED_ACTIVATED;
		
		default:
			return -1;
		
		}
	}

	@Override
	public Collection<AvailableLanguageDto> getAvailableLanguages() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomResourceBundle getResourceBundle(String lang) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerActivationProducts(AuthenticatedProductsResultDto data) throws ActivationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerCountedProducts(CountedProductsResultDto data) throws ActivationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerEvent(EventDto evt) throws MonitoringException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCryptoPassword() {
		// TODO Auto-generated method stub
		return null;
	}
}
