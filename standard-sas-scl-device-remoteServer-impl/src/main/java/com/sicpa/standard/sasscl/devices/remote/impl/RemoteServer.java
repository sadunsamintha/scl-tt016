package com.sicpa.standard.sasscl.devices.remote.impl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.exception.InitializationRuntimeException;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.ISicpaDataGeneratorRequestor;
import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoAuthenticatorWrapper;
import com.sicpa.standard.sasscl.model.*;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.*;
import com.sicpa.standard.sasscl.sicpadata.CryptoServiceProviderManager;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.std.common.api.activation.business.ActivationServiceHandler;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.ProcessedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.dto.productionData.ProcessedProductsStatusDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsResultDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;
import com.sicpa.std.common.api.base.dto.BaseDto;
import com.sicpa.std.common.api.base.dto.ComponentBehaviorDto;
import com.sicpa.std.common.api.coding.business.CodingServiceHandler;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoResultDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoResultDto.InfoResult;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorOrderDto;
import com.sicpa.std.common.api.config.business.ConfigurationBusinessHandler;
import com.sicpa.std.common.api.monitoring.business.EventServiceHandler;
import com.sicpa.std.common.api.monitoring.dto.EventDto;
import com.sicpa.std.common.api.monitoring.dto.EventTypeDto;
import com.sicpa.std.common.api.multilingual.business.ProvideTranslationBusinessHandler;
import com.sicpa.std.common.api.multilingual.common.CustomResourceBundle;
import com.sicpa.std.common.api.multilingual.exception.MultilingualException;
import com.sicpa.std.common.api.security.business.LoginServiceHandler;
import com.sicpa.std.common.api.security.dto.LoginDto;
import com.sicpa.std.common.api.sku.dto.MarketTypeDto;
import com.sicpa.std.common.api.staticdata.codetype.dto.CodeTypeDto;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;
import com.sicpa.std.common.api.util.PropertyNames;
import com.sicpa.std.server.util.lifechecker.LifeChecker;
import org.jboss.ejb.client.EJBClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import static com.sicpa.standard.sasscl.model.ProductionMode.REFEED_CORRECTION;
import static com.sicpa.standard.sasscl.model.ProductionMode.REFEED_NORMAL;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.LAST_SENT_TO_REMOTE_SERVER;
import static com.sicpa.std.common.api.activation.business.ActivationServiceHandler.*;
import static com.sicpa.std.server.util.locator.ServiceLocator.*;

public class RemoteServer extends AbstractRemoteServer {

	private static final Logger logger = LoggerFactory.getLogger(RemoteServer.class);

	protected IRemoteServerProductStatusMapping productStatusMapping;

	protected RemoteServerModel model;

	protected CryptoServiceProviderManager cryptoServiceProviderManager;

	protected GlobalBean config;

	protected LifeChecker lifeChecker;

	protected final Map<ProductStatus, IPackageSender> packageSenders = new HashMap<ProductStatus, IPackageSender>();

	protected IProductionModeMapping productionModeMapping;

	protected ISicpaDataGeneratorRequestor sdGenReceiver;

	protected IStorage storage;

	protected String serverPropertiesFile = "config/server/standard-server.properties";

    protected Context context;

    private final Properties properties = new Properties();

	public RemoteServer(final String configFile) {

		try {
			model = ConfigUtils.load(configFile);
		} catch (Exception e) {
			throw new InitializationRuntimeException("Failed to load remote server model", e);
		}

        init();
	}

	public RemoteServer(final RemoteServerModel remoteServerModel) {

		model = remoteServerModel;
        init();
	}

    protected void init() {

        lifeChecker = new LifeChecker();
        initPackageSenders();

        if (isPropertiesFileLoaded()) {
            initClientSecurityContext();
            setupContext();
        }
    }

    protected void setupContext()  {

        try {
            context = new InitialContext(properties);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private void initClientSecurityContext() {

        EJBClientContext.getCurrent().registerInterceptor(1, new BasicClientSecurityInterceptor());
        BasicClientSecurityInterceptor.setPrincipal(model.getUsername(), model.getPassword().toCharArray());
    }

    private boolean isPropertiesFileLoaded() {

        try {
            URL url = ClassLoader.getSystemResource(serverPropertiesFile);
            properties.load(new FileInputStream(new File(url.toURI())));

            return true;
        } catch (Exception e) {
            logger.error("failed to load " + serverPropertiesFile, e);
        }
        return false;
    }

    protected void initPackageSenders() {
		IPackageSender senderActivated = new IPackageSender() {
			@Override
			public void sendPackage(PackagedProducts products) throws ActivationException {
				processActivatedProducts(products);
			}
		};

		IPackageSender senderCounted = new IPackageSender() {
			@Override
			public void sendPackage(PackagedProducts products) throws ActivationException {
				processCountedProducts(products);
			}
		};

		packageSenders.put(ProductStatus.AUTHENTICATED, senderActivated);
		packageSenders.put(ProductStatus.REFEED, senderActivated);
		packageSenders.put(ProductStatus.SENT_TO_PRINTER_UNREAD, senderActivated);
		packageSenders.put(ProductStatus.SENT_TO_PRINTER_WASTED, senderActivated);
		packageSenders.put(ProductStatus.TYPE_MISMATCH, senderActivated);

		packageSenders.put(ProductStatus.COUNTING, senderCounted);
		packageSenders.put(ProductStatus.EXPORT, senderCounted);
		packageSenders.put(ProductStatus.MAINTENANCE, senderCounted);

		packageSenders.put(ProductStatus.NOT_AUTHENTICATED, senderCounted);
		packageSenders.put(ProductStatus.UNREAD, senderCounted);
		// packageSenders.put(ProductStatus.OFFLINE, senderCounted);
	}

	@Override
	protected void doConnect() throws RemoteServerException {
		startLifeChecker();
	}

	@Override
	protected void doDisconnect() throws RemoteServerException {
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	@Override
	public final IAuthenticator getAuthenticator() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {

			return doGetAuthenticator();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	public IAuthenticator doGetAuthenticator() throws ActivationException {

		getAndSaveCryptoPassword();

        SicpadataReaderDto auth = getActivationBean().provideSicpadataReader();

		return new StdCryptoAuthenticatorWrapper(auth.getSicpadataReader(), cryptoFieldsConfig);
	}

	@Override
	public final void downloadEncoder(final int batchesQuantity, final CodeType codeType, final int year)
			throws RemoteServerException {
		if (!isConnected()) {
			return;
		}
		try {

			doDownloadEncoder(batchesQuantity, codeType, year);
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	protected ConfigurationBusinessHandler getConfigBean() {
		return getService(SERVICE_CONFIG_BUSINESS_SERVICE);
	}

    @SuppressWarnings("unchecked")
    protected <T> T getService(String name) {

        try {
            return (T) context.lookup(name);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

	protected void getAndSaveCryptoPassword() {
		try {
            String pwd = fetchCryptoPassword();

			cryptoServiceProviderManager.setPassword(pwd);

			// save the password for offline prod
			if (!config.getSicpadataPassword().equals(pwd)) {
				config.setSicpadataPassword(pwd);
				ConfigUtils.save(config);
			}
		} catch (Exception e) {
			logger.error("", e);
		}

	}

    private String fetchCryptoPassword() {

        ConfigurationBusinessHandler configService = getConfigBean();

        Map<String, String> serverConfiguration = configService.getConfiguration(null);

        return serverConfiguration.get(PropertyNames.SICPADATA_ADMIN_PWD);
    }

    public void doDownloadEncoder(final int quantity, final CodeType codeType, final int year) {
		if (quantity <= 0) {
			return;
		}
		logger.info("requesting encoder qty={} codeType={} ,  year={}",
				new Object[] { quantity, codeType.getId(), year });
		try {

            sdGenReceiver.requestSicpadataGenerators(newSdGenOrder(quantity, codeType, year), getCodingBean());
		} catch (Exception e) {
			logger.error(MessageFormat.format(
					"request SicpadataGenerator, quantity:{0} codeType:{1}  year:{2}, failed", quantity,
					codeType.getId(), year), e);
			EventBusService.post(new MessageEvent("remoteserver.getEncoder.fail"));
		}
	}

    private SicpadataGeneratorOrderDto newSdGenOrder(int quantity, CodeType codeType, int year) {

        SicpadataGeneratorOrderDto dto = new SicpadataGeneratorOrderDto();
        dto.setQuantity(quantity);
        dto.setCodeTypeId(codeType.getId());
        dto.setYear(year);
        return dto;
    }

    @Override
	public final ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {

            return doGetTreeProductionParameters();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	public ProductionParameterRootNode doGetTreeProductionParameters() {
		AuthorizedProductsDto products = getActivationBean().provideAuthorizedProducts();
		// convert AuthorizedProductsDto to root node
		ProductionParameterRootNode root = convertDMSProductionParameter(products);
		pruneParametersTree(root);
		ProductionModeNode maintenanceNode = new ProductionModeNode(ProductionMode.MAINTENANCE);
		root.addChildren(maintenanceNode);
		return root;
	}

	@SuppressWarnings("unchecked")
	public ProductionParameterRootNode convertDMSProductionParameter(final AuthorizedProductsDto parentDTO) {
		ProductionParameterRootNode convertedRoot = new ProductionParameterRootNode();
		convertDMSProductionParameter(parentDTO, convertedRoot);
		return convertedRoot;
	}

	/**
	 *
	 * expected structure :
	 *
	 * <code>
	 *
	 * MarketTypeDto
	 * |
	 * |- CodeTypeDto
	 * 		|
	 * 		|- SKUTypeDto
	 *
	 * </code>
	 *
	 * @param parentDTO
	 * @param convertedParentRoot
	 */
    protected void convertDMSProductionParameter(final ComponentBehaviorDto<? extends BaseDto<Long>> parentDTO,
                                                 final AbstractProductionParametersNode<?> convertedParentRoot) {

        if (parentDTO.getChildren() == null)
            return;

        for (ComponentBehaviorDto<? extends BaseDto<Long>> child : parentDTO.getChildren())
            convert(convertedParentRoot, child);
    }

    private void convert(AbstractProductionParametersNode<?> convertedParentRoot,
                         ComponentBehaviorDto<? extends BaseDto<Long>> child) {

        if (child.getNodeValue() instanceof MarketTypeDto) {
            convertMarketTypeDto(child, convertedParentRoot);
            return;
        }
        if (child.getNodeValue() instanceof SkuProductDto) {
            convertSkuProductDto(child, convertedParentRoot);
            return;
        }
        if (child.getNodeValue() instanceof CodeTypeDto) {
            convertCodeTypeDto(child, convertedParentRoot);
            return;
        }
        convertNavigationDto(child, convertedParentRoot);
    }

    protected void convertNavigationDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		// navigation node only
		NavigationNode navigationNode = new NavigationNode(child.getNodeValue().getId() + "");
		convertedParentRoot.addChildren(navigationNode);
		convertDMSProductionParameter(child, navigationNode);
	}

	protected void convertCodeTypeDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		CodeTypeDto codeDto = (CodeTypeDto) child.getNodeValue();
		CodeType codeType = new CodeType(codeDto.getId().intValue());
		codeType.setDescription(codeDto.getTranslationCode());
		CodeTypeNode codeTypeConverted = new CodeTypeNode(codeType);
		convertedParentRoot.addChildren(codeTypeConverted);
		convertDMSProductionParameter(child, codeTypeConverted);
	}

	protected void convertSkuProductDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		SkuProductDto skuDto = (SkuProductDto) child.getNodeValue();
		SKU sku = new SKU(skuDto.getId().intValue(), skuDto.getTranslationCode(), Arrays.asList(skuDto
				.getSkuBarcode()));
		CodeType codeType = this.getCodeTypeForSku(child);

		// skip if fail to get code type
		if (codeType == null) {
			return;
		}

		sku.setCodeType(this.getCodeTypeForSku(child));
		SKUNode skuConverted = new SKUNode(sku);
		convertedParentRoot.addChildren(skuConverted);
		convertDMSProductionParameter(child, skuConverted);
	}

    protected void convertMarketTypeDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
                                        final AbstractProductionParametersNode<?> convertedParentRoot) {

        MarketTypeDto marketDto = (MarketTypeDto) child.getNodeValue();
        ProductionMode productionMode = productionModeMapping.getProductionModeFromRemoteId(marketDto.getId()
                .intValue());
        if (productionMode == null) {

            logger.error("no production mode for {}", marketDto.toString());
            return;
        }
        ProductionModeNode productionModeConverted = new ProductionModeNode(productionMode);
        convertedParentRoot.addChildren(productionModeConverted);
        convertDMSProductionParameter(child, productionModeConverted);

        if (ProductionMode.STANDARD.equals(productionMode)) {
            // if standard mode duplicate the tree for refeed
            copyTree(productionModeConverted, new ProductionModeNode(REFEED_NORMAL), convertedParentRoot);
            copyTree(productionModeConverted, new ProductionModeNode(REFEED_CORRECTION), convertedParentRoot);
        }
    }

	protected void copyTree(ProductionModeNode from, ProductionModeNode to,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		convertedParentRoot.addChildren(to);
		to.addChildren(from.getChildren());
	}

	protected CodeType getCodeTypeForSku(final ComponentBehaviorDto<? extends BaseDto<Long>> skuDto) {

        if (skuDto == null)
            return null;

        if (skuDto.getParent().getNodeValue() instanceof CodeTypeDto) {

            CodeTypeDto codeTypeDto = (CodeTypeDto) skuDto.getParent().getNodeValue();
            CodeType codeType = new CodeType(codeTypeDto.getId().intValue());
            codeType.setDescription(codeTypeDto.getTranslationCode());
            return codeType;
        }

        return getCodeTypeForSku(skuDto.getParent());
    }

	@Override
	public void sendEncoderInfos(List<EncoderInfo> infos) throws RemoteServerException {
		try {
			doSendEncoderInfos(infos);
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	public void doSendEncoderInfos(List<EncoderInfo> infos) throws RemoteServerException {

		List<SicpadataGeneratorInfoDto> dtos = new ArrayList<SicpadataGeneratorInfoDto>();

		for (EncoderInfo info : infos) {
			SicpadataGeneratorInfoDto dto = new SicpadataGeneratorInfoDto(info.getEncoderId(),
					(long) info.getCodeTypeId(), info.getSequence(), info.getFirstCodeDate(), info.getLastCodeDate());
			dtos.add(dto);
		}
		logger.debug("sending encoder info {}", infos);
		SicpadataGeneratorInfoResultDto res = getCodingBean().registerGeneratorsCycle(dtos);
		for (InfoResult ir : res.getInfoResult()) {
			if (!ir.isInfoSavedOk()) {
				storage.quarantineEncoder(ir.getId());
				throw new RemoteServerException(MessageFormat.format(
						"master failed to save encoder info for id={0} , msg={1}", ir.getId() + "",
						ir.getErrorMessage()));
			}
		}
	}

	@Override
	public final void sendProductionData(final PackagedProducts products) throws RemoteServerException {
		if (!isConnected()) {
			throw new RemoteServerException();
		}

		if (products == null || products.getProducts() == null || products.getProducts().isEmpty()) {
			return;
		}
		try {

			MonitoringService.addSystemEvent(
                    new BasicSystemEvent(LAST_SENT_TO_REMOTE_SERVER, String.valueOf(products.getProducts().size()))
            );

			doSendProductionData(products);

		} catch (Exception e) {
			RemoteServerException ex = new RemoteServerException(e);
			ex.setBusiness(e instanceof ActivationException);
			throw ex;
		}
	}

	public void doSendProductionData(final PackagedProducts products) throws ActivationException {

        if (null == packageSenders.get(products.getProductStatus())) {
            logger.error("No sender found for the package type in file: " + products.getFileName());
            return;
        }
        packageSenders.get(products.getProductStatus()).sendPackage(products);
    }

	protected void processActivatedProducts(final PackagedProducts products) throws ActivationException {

		AuthenticatedProductsResultDto authenticatedProductsResultDto = generateAuthenticatedProductsResultDto(products);

		String key = getActivationServiceKey(products);

		// call remote
        getActivationBean().registerProductionCycle(authenticatedProductsResultDto, key);
	}

	protected String getActivationServiceKey(PackagedProducts products) {

        if (products.isPrinted()) {
            // if SCL
            return MARKED_PRODUCT_TYPE;
        }
        // if SAS
        return LABELED_PRODUCT_TYPE;
    }

	protected AuthenticatedProductsResultDto generateAuthenticatedProductsResultDto(PackagedProducts products) {


		ArrayList<AuthenticatedProductDto> authenticatedProductsDto = new ArrayList<AuthenticatedProductDto>();
		ProcessedProductsStatusDto statusDto = null;
		// create a product dto for each product
		for (Product product : products.getProducts()) {
			if (statusDto == null) {
				int remoteId = productStatusMapping.getRemoteServerProdutcStatus(products.getProductStatus());
				statusDto = new ProcessedProductsStatusDto();
				statusDto.setValue(remoteId);
			}

            // Product Code Type is null for marked products so get it from selected SKU
            long codeTypeId = products.isPrinted() ? product.getSku().getCodeType().getId() : product.getCode().getCodeType().getId();
			authenticatedProductsDto.add(new AuthenticatedProductDto((long) product.getSku().getId(), codeTypeId,
                    product.getCode().getEncoderId(), product.getCode().getSequence(), product.getActivationDate()));
		}

        AuthenticatedProductsResultDto authenticatedProductsResultDto = new AuthenticatedProductsResultDto();
		populateResultDtoInfo(authenticatedProductsResultDto, products);
		authenticatedProductsResultDto.setProcessedProducts(authenticatedProductsDto);

		authenticatedProductsResultDto.setProcessedProductsStatusDto(statusDto);

		return authenticatedProductsResultDto;
	}

	protected void populateResultDtoInfo(final ProcessedProductsResultDto dto, final PackagedProducts products) {
		dto.setSubsystemId(products.getSubsystem());
		dto.setIdTransaction(products.getUID());
		dto.setIdProductionBatch(products.getProductionBatchId());
	}

	protected Map<ProductStatus, List<Product>> sortProductByStatus(final PackagedProducts products) {
		Map<ProductStatus, List<Product>> mapStatusProductList = new HashMap<ProductStatus, List<Product>>();
		for (Product product : products.getProducts()) {
			List<Product> list = mapStatusProductList.get(product.getStatus());
			if (list == null) {
				list = new ArrayList<Product>();
				mapStatusProductList.put(product.getStatus(), list);
			}
			list.add(product);
		}

		return mapStatusProductList;
	}

	protected CountedProductsDto createCountedProductDto(final ProductStatus status, final List<Product> list) {
		Date start = null;
		Date end = null;
		long skuId = 0;
		long codeTypeId = 0;

		for (Product product : list) {
			skuId = product.getSku().getId();// TODO NULL IF MAINTENANCE
			codeTypeId = product.getSku().getCodeType().getId();

			if (start == null) {
				start = end = product.getActivationDate();
			} else {
				if (product.getActivationDate().before(start)) {
					start = product.getActivationDate();
				}
				if (product.getActivationDate().after(end)) {
					end = product.getActivationDate();
				}
			}
		}
		return new CountedProductsDto(skuId, codeTypeId, (long) list.size(), start, end);
	}

	protected void processCountedProducts(final PackagedProducts products) throws ActivationException {

		if (products.getProductStatus().equals(ProductStatus.MAINTENANCE)) {
			// DMS does not handle maintenance mode for now
			return;
		}

        // create product dto

		CountedProductsResultDto countedProductsResultDto = new CountedProductsResultDto();
		ArrayList<CountedProductsDto> countedProductsDto = new ArrayList<CountedProductsDto>();

		countedProductsDto.add(createCountedProductDto(products.getProductStatus(), products.getProducts()));

		populateResultDtoInfo(countedProductsResultDto, products);
		countedProductsResultDto.setProcessedProducts(countedProductsDto);

		ProcessedProductsStatusDto processedProductStatusDto = new ProcessedProductsStatusDto();
		processedProductStatusDto.setValue(productStatusMapping.getRemoteServerProdutcStatus(products.getProductStatus()));
		countedProductsResultDto.setProcessedProductsStatusDto(processedProductStatusDto);

		// call remote
		getActivationBean().registerProductionCycle(countedProductsResultDto, COUNTING_PRODUCT_TYPE);
	}

	protected void checkConnection() throws RemoteServerException {
		try {
			lifeChecker.checkServicesAvailability(serverPropertiesFile);
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	protected RemoteServerModel getModel() {
		return model;
	}

	@Override
	public long getSubsystemID() {
		try {
			LoginDto dto = getLoginBean().authenticate(model.getUsername(), model.getPassword());
			return dto.getUserDto().getSubsystem().getId();
		} catch (com.sicpa.std.common.api.security.exception.SecurityException e) {
			logger.error("failed to get subsystem id", e);
		}
		return 0;
	}

	protected LoginServiceHandler getLoginBean() {

        return getService(SERVICE_LOGIN_BUSINESS_SERVICE);
	}

	protected class RemoteServerLifeChecker {
		Thread lifeCheker;

		protected void start() {
			if (lifeCheker == null) {
				lifeCheker = new Thread(new Runnable() {
					@Override
					public void run() {
						// is a daemon + server is supposed to be up at all time
						// => while true is ok
						while (true) {
							lifeCheckTick();
							if (model.getLifeCheckSleep() > 0) {
								ThreadUtils.sleepQuietly(1000L * model.getLifeCheckSleep());
							} else {
								ThreadUtils.sleepQuietly(10 * 1000);
							}
						}
					}
				});
				lifeCheker.setDaemon(true);
				lifeCheker.setName("RemoteServerLifeChecker");
				lifeCheker.start();
			}
		}
	}

	protected void startLifeChecker() {
		new RemoteServerLifeChecker().start();
	}

	@Override
	public void lifeCheckTick() {
		try {
			logger.debug("remote server life check");
			checkConnection();
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		} catch (Exception e) {
			logger.error("", e);
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	@Override
	public Map<String, ? extends ResourceBundle> getLanguageBundles() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {

			return doGetTranslationBean();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}

	}

	public Map<String, CustomResourceBundle> doGetTranslationBean() throws MultilingualException {
		return getTranslationBean().getResourceBundles();
	}

	public GlobalBean getConfig() {
		return config;
	}

	public void setConfig(final GlobalBean config) {
		this.config = config;
	}

	/**
	 *
	 * Get activation service handler from remote server
	 *
	 * @return instance of ActivationServiceHandler get from remote server
	 */
	protected ActivationServiceHandler getActivationBean() {

        return getService(SERVICE_ACTIVATION_BUSINESS_SERVICE);
	}

	/**
	 *
	 * Get coding service handler from remote server
	 *
	 * @return instance of CodingServerHandler get from remote server
	 */
	protected CodingServiceHandler getCodingBean() {

        return getService(SERVICE_COMMON_CODING_BUSINESS_SERVICE);
	}

	protected ProvideTranslationBusinessHandler getTranslationBean() {

        return getService(SERVICE_PROVIDE_TRANSLATION_BUSINESS_SERVICE);
	}

	public IRemoteServerProductStatusMapping getProductStatusMapping() {
		return productStatusMapping;
	}

	public void setProductStatusMapping(IRemoteServerProductStatusMapping productStatusMapping) {
		this.productStatusMapping = productStatusMapping;
	}

	public void setProductionModeMapping(IProductionModeMapping productionModeMapping) {
		this.productionModeMapping = productionModeMapping;
	}

	/**
	 * Trim the branches of the tree that do not contain at least one ProductionModeNode and one SKUNode in the path
	 * starting in the root node.
	 *
	 * @param tree
	 */
	protected void pruneParametersTree(ProductionParameterRootNode tree) {
		pruneParametersTree(tree, false, false);
	}

	/**
	 * This method trims the branches of the tree given the root node. It trims branches that don't contain at least one
	 * ProductionModeNode and one SKUNode in the path starting from the given node.
	 *
	 * @param node
	 *            the node to prune.
	 * @param ancestorWithProdMode
	 *            true indicates if the ancestors of the node contains a ProductionModeNode.
	 * @param ancestorWithSKU
	 *            true indicates if the ancestors of the node contains a SKUNode.
	 * @return true if the tree was pruned, false otherwise.
	 */
	protected boolean pruneParametersTree(IProductionParametersNode node, boolean ancestorWithProdMode,
			boolean ancestorWithSKU) {
		boolean isProdModeNode = node instanceof ProductionModeNode;
		boolean isSKUNode = node instanceof SKUNode;

		if (ancestorWithProdMode && ancestorWithSKU)
			return false;

		if (isProdModeNode && ancestorWithSKU)
			return false;

		if (isSKUNode && ancestorWithProdMode)
			return false;

		// search for child nodes to trim
		List<IProductionParametersNode> trimmedChildren = new ArrayList<IProductionParametersNode>();

		for (IProductionParametersNode childNode : node.getChildren()) {

			if (pruneParametersTree(childNode, isProdModeNode || ancestorWithProdMode, isSKUNode || ancestorWithSKU))
				trimmedChildren.add(childNode);
		}

		// trim the found nodes
		for (IProductionParametersNode child : trimmedChildren) {
			node.getChildren().remove(child);
		}

		return node.getChildren().isEmpty();
	}

	public void setCryptoServiceProviderManager(CryptoServiceProviderManager cryptoServiceProviderManager) {
		this.cryptoServiceProviderManager = cryptoServiceProviderManager;
	}

	public void setSdGenReceiver(ISicpaDataGeneratorRequestor sdGenReceiver) {
		this.sdGenReceiver = sdGenReceiver;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	protected EventServiceHandler getEventBean() {

        return getService(SERVICE_DMS_EVENT_BUSINESS_SERVICE);
	}

	@Override
	public void sentInfoToGlobalMonitoringTool(GlobalMonitoringToolInfo info) {
		try {
			EventDto evt = createEventDto(info);
			getEventBean().register(evt);
		} catch (Exception e) {
			logger.error("error sending global monitoring tool info:" + info, e);
		}
	}

	protected EventDto createEventDto(GlobalMonitoringToolInfo info) {
		EventDto event = new EventDto();
		event.setEventTime(new Date());
		EventTypeDto type = new EventTypeDto();
		if (info.isProductionStarted()) {
			event.setEventMessage(Messages.get("globalMonitoringTool.info.started"));
			type.setBusinessCode("eventype.production.started");
		} else {
			event.setEventMessage(Messages.get("globalMonitoringTool.info.notstarted"));
			type.setBusinessCode("eventype.production.stopped");
		}

		event.setEventType(type);

		return event;
	}

	/**
	 * Set Life Checker
	 *
	 * @param lifeChecker the lifeChecker to set
	 */
	public void setLifeChecker(LifeChecker lifeChecker) {
		this.lifeChecker = lifeChecker;
	}

}
