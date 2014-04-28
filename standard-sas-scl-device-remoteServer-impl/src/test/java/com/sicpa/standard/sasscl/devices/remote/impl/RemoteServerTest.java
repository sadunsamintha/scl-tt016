package com.sicpa.standard.sasscl.devices.remote.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sicpa.standard.sasscl.common.storage.productPackager.DefaultProductsPackager;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping.DefaultProductionModeMapping;
import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.ISicpaDataGeneratorRequestor;
import com.sicpa.standard.sasscl.devices.remote.impl.statusmapping.DefaultRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.CodeTypeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.NavigationNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.standard.sasscl.sicpadata.CryptoServiceProviderManager;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataContent;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataReader;
import com.sicpa.standard.sicpadata.api.exception.UnknownModeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownVersionException;
import com.sicpa.std.common.api.activation.business.ActivationServiceHandler;
import com.sicpa.std.common.api.activation.dto.ActivationResultsDto;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.dto.productionData.ProcessedProductsStatusDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsResultDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;
import com.sicpa.std.common.api.base.dto.BaseDto;
import com.sicpa.std.common.api.base.dto.ComponentBehaviorDto;
import com.sicpa.std.common.api.coding.business.CodingServiceHandler;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import com.sicpa.std.common.api.config.business.ConfigurationBusinessHandler;
import com.sicpa.std.common.api.multilingual.business.ProvideTranslationBusinessHandler;
import com.sicpa.std.common.api.multilingual.common.CustomResourceBundle;
import com.sicpa.std.common.api.multilingual.exception.MultilingualException;
import com.sicpa.std.common.api.sku.dto.MarketTypeDto;
import com.sicpa.std.common.api.staticdata.codetype.dto.CodeTypeDto;
import com.sicpa.std.common.api.staticdata.dto.StaticDataCompositeBehaviorDto;
import com.sicpa.std.common.api.staticdata.dto.StaticDataLeafBehaviorDto;
import com.sicpa.std.common.api.staticdata.dto.StaticDataNodeValueDto;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;
import com.sicpa.std.server.util.locator.ServiceLocator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceLocator.class })
public class RemoteServerTest {

	private CodingServiceHandler codingBean;
	private ActivationServiceHandler activationBean;
	private RemoteServer remoteServer;
	private LoginContext login;
	private ConfigurationBusinessHandler businessHandler;
	private ProvideTranslationBusinessHandler translationService;
	private IRemoteServerProductStatusMapping productStatusMapping;
	private IProductionModeMapping productionModeMapping;
	private ISicpaDataGeneratorRequestor sdGenReceiver;

	@Before
	public void setup() throws LoginException {
		setupLoginService();
		setupLocator();
		setupRemoteServer();
	}

	private void setupRemoteServer() {
		RemoteServerModel model = new RemoteServerModel();
		model.setPassword("pw");
		model.setUsername("username");
		remoteServer = new RemoteServer(model) {
			@Override
			public boolean isConnected() {
				return true;
			}

			@Override
			protected LoginContext createLoginContext(CallbackHandler callbackHandler) throws LoginException {
				return login;
			}
		};

		productStatusMapping = new DefaultRemoteServerProductStatusMapping();
		remoteServer.setProductStatusMapping(productStatusMapping);

		productionModeMapping = new DefaultProductionModeMapping();
		remoteServer.setProductionModeMapping(productionModeMapping);

		remoteServer.setConfig(new GlobalBean());

		sdGenReceiver = Mockito.mock(ISicpaDataGeneratorRequestor.class);
		remoteServer.setSdGenReceiver(sdGenReceiver);

		CryptoServiceProviderManager cryptoService = Mockito.mock(CryptoServiceProviderManager.class);
		remoteServer.setCryptoServiceProviderManager(cryptoService);

	}

	boolean logindone = false;
	boolean logoutdone = false;

	// to be able to check that login and logout are called
	private void setupLoginService() throws LoginException {
		login = mock(LoginContext.class);
		doAnswer(new Answer<LoginContext>() {
			@Override
			public LoginContext answer(InvocationOnMock invocation) throws Throwable {
				logindone = true;
				return login;
			}
		}).when(login).login();

		doAnswer(new Answer<LoginContext>() {
			@Override
			public LoginContext answer(InvocationOnMock invocation) throws Throwable {
				logoutdone = true;
				return login;
			}
		}).when(login).logout();
	}

	// mock the call to the locator
	private void setupLocator() {
		final ServiceLocator locator = Mockito.mock(ServiceLocator.class);
		codingBean = Mockito.mock(CodingServiceHandler.class);
		activationBean = Mockito.mock(ActivationServiceHandler.class);
		translationService = mock(ProvideTranslationBusinessHandler.class);
		businessHandler = mock(ConfigurationBusinessHandler.class);

		when(locator.getService(ServiceLocator.SERVICE_COMMON_CODING_BUSINESS_SERVICE)).thenReturn(codingBean);
		when(locator.getService(ServiceLocator.SERVICE_ACTIVATION_BUSINESS_SERVICE)).thenReturn(activationBean);
		when(locator.getService(ServiceLocator.SERVICE_CONFIG_BUSINESS_SERVICE)).thenReturn(businessHandler);
		when(locator.getService(ServiceLocator.SERVICE_PROVIDE_TRANSLATION_BUSINESS_SERVICE)).thenReturn(
				translationService);

		PowerMockito.mockStatic(ServiceLocator.class, new Answer<ServiceLocator>() {
			@Override
			public ServiceLocator answer(InvocationOnMock invocation) throws Throwable {
				return locator;
			}
		});
	}

	// @After
	// public void checklogout() {
	// Assert.assertTrue("logout have been called", logoutdone);
	// }

	public static interface ITestRemoteServer {
		void check(InvocationOnMock invocation) throws Throwable;
	}

	public class MyAnswer<T> implements Answer<T> {

		ITestRemoteServer check;

		private MyAnswer(ITestRemoteServer check) {
			this.check = check;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T answer(InvocationOnMock invocation) throws Throwable {
			Assert.assertTrue("login should have been called", logindone);
			check.check(invocation);
			return (T) invocation.getMock();
		}
	}

	// -------------TESTS START HERE

	@SuppressWarnings("unchecked")
	@Test
	public void sendActivationProductionTest() throws RemoteServerException, ActivationException {

		Mockito.doAnswer(new MyAnswer<ActivationServiceHandler>(new ITestRemoteServer() {
			@Override
			public void check(InvocationOnMock invocation) throws Throwable {
				AuthenticatedProductsResultDto result = (AuthenticatedProductsResultDto) invocation.getArguments()[0];
				Assert.assertEquals(ProcessedProductsStatusDto.AUTHENTICATED_ACTIVATED, result
						.getProcessedProductsStatusDto().getValue());

				Assert.assertEquals(2, result.getProcessedProducts().size());
				Assert.assertEquals(Long.valueOf(1), result.getProcessedProducts().get(0).getSequence());
				Assert.assertEquals(Long.valueOf(2), result.getProcessedProducts().get(1).getSequence());
			}
		})).when(activationBean).registerProductionCycle(Mockito.any(ActivationResultsDto.class), Mockito.anyString());

		SKU sku = new SKU(1);
		sku.setCodeType(new CodeType(2));

		Code c1 = new Code("1");
		c1.setSequence(1);
		Code c2 = new Code("2");
		c2.setSequence(2);

		Product p = new Product(c1, ProductStatus.AUTHENTICATED, sku, "", new Date(), 13245l);
		Product p2 = new Product(c2, ProductStatus.AUTHENTICATED, sku, "", new Date(), 13245l);

		PackagedProducts products = new PackagedProducts(Arrays.asList(p, p2), 123456789l, ProductStatus.AUTHENTICATED,
				"147", 132l, true);

		remoteServer.sendProductionData(products);

		// checklogout();
	}

	@Test
	public void sendEjectedProductionTest() throws RemoteServerException, ActivationException {

		// prepare

		Mockito.doAnswer(new MyAnswer<ActivationServiceHandler>(new ITestRemoteServer() {
			@Override
			public void check(InvocationOnMock invocation) throws Throwable {
				CountedProductsResultDto result = (CountedProductsResultDto) invocation.getArguments()[0];
				Assert.assertEquals(ProcessedProductsStatusDto.COUNTED_EJECTED_UNREAD, result
						.getProcessedProductsStatusDto().getValue());

				Assert.assertEquals(1, result.getProcessedProducts().size());
				Assert.assertEquals(2, result.getProcessedProducts().get(0).getQuantity().longValue());
			}
		})).when(activationBean).registerProductionCycle(Mockito.any(ActivationResultsDto.class), Mockito.anyString());

		SKU sku = new SKU(1);
		sku.setCodeType(new CodeType(2));

		Code c1 = new Code("1");
		c1.setSequence(1);
		Code c2 = new Code("2");
		c2.setSequence(2);

		Product p1 = new Product(c1, ProductStatus.UNREAD, sku, "", new Date(), 13245l);
		Product p2 = new Product(c2, ProductStatus.UNREAD, sku, "", new Date(), 13245l);

		DefaultProductsPackager packager = new DefaultProductsPackager();
		List<PackagedProducts> packs = packager.getPackagedProducts(Arrays.asList(p1, p2));

		// execute
		remoteServer.sendProductionData(packs.get(0));
	}

	@Test
	public void getAuthenticatorTest() throws RemoteServerException, ActivationException, UnknownModeException,
			UnknownVersionException, CryptographyException {

		DecodedCameraCode result = mock(DecodedCameraCode.class);

		ICryptoFieldsConfig fieldsConfig = mock(ICryptoFieldsConfig.class);
		when(fieldsConfig.getFields(any(IBSicpadataContent.class))).thenReturn(result);

		remoteServer.setCryptoFieldsConfig(fieldsConfig);

		IBSicpadataReader dataReader = mock(IBSicpadataReader.class);

		// the DTO is returned by the service requested to the master
		SicpadataReaderDto dto = mock(SicpadataReaderDto.class);
		when(dto.getSicpadataReader()).thenReturn(dataReader);

		when(activationBean.provideSicpadataReader()).thenReturn(dto);

		// IAthenticator = dataReader + fieldsConfig

		// execute
		IAuthenticator authenticator = remoteServer.getAuthenticator();

		// verify
		assertNotNull(authenticator);
		assertEquals(authenticator.decode(null, "ABC"), result);
	}

	private SicpadataGeneratorDto prepareAndGetGeneratorDto() {
		DecodedCameraCode result = mock(DecodedCameraCode.class);

		ICryptoFieldsConfig fieldsConfig = mock(ICryptoFieldsConfig.class);
		when(fieldsConfig.getFields(any(IBSicpadataContent.class))).thenReturn(result);

		remoteServer.setCryptoFieldsConfig(fieldsConfig);

		IBSicpadataGenerator generator = mock(IBSicpadataGenerator.class);

		SicpadataGeneratorDto generatorDto = mock(SicpadataGeneratorDto.class);
		when(generatorDto.getSicpadataGeneratorObject()).thenReturn(generator);
		when(generatorDto.getMaxAvailable()).thenReturn(10000L);

		return generatorDto;
	}

	@Test
	public void getLanguageBundleTest() throws MultilingualException, RemoteServerException {
		// prepare
		@SuppressWarnings("unchecked")
		Map<String, CustomResourceBundle> resourcesMap = mock(Map.class);
		when(translationService.getResourceBundles()).thenReturn(resourcesMap);

		// action
		Map<String, ? extends ResourceBundle> receivedResources = remoteServer.getLanguageBundles();

		// verify
		assertEquals(resourcesMap, receivedResources);
	}

	@Test
	public void getParametersTest() throws RemoteServerException {

		/*
		 * Tree structure to test:
		 * 
		 * +RootNode (Level 0) | \___+Market1: Prod standard (Level 1) | | | \___+CodeType1 (Level 2) | | \___Sku1
		 * (Level 3) | | \___Sku2 | | | \___+CodeType2 | \___Sku3 | | \___+Market2: Prod Export | \___+CodeType1 |
		 * \___Sku4 | \___+CodeType3 \___Sku5 \___Sku6
		 */

		// prepare

		// set up Input Tree DTO (suposed to come from server).
		ComponentBehaviorDto<BaseDto<Long>> comp;

		// set up LEVEL 0 (root)
		AuthorizedProductsDto rootNode = new AuthorizedProductsDto(null);

		// set up LEVEL 1 (Production mode)

		// Node structure
		ComponentBehaviorDto<StaticDataNodeValueDto> dtoNodePMode1 = new StaticDataCompositeBehaviorDto<StaticDataNodeValueDto>();
		ComponentBehaviorDto<StaticDataNodeValueDto> dtoNodePMode2 = new StaticDataCompositeBehaviorDto<StaticDataNodeValueDto>();

		rootNode.add(dtoNodePMode1);
		rootNode.add(dtoNodePMode2);

		// values
		MarketTypeDto market1 = new MarketTypeDto();
		MarketTypeDto market2 = new MarketTypeDto();

		market1.setId((long) ProductionMode.STANDARD.getId());
		market2.setId((long) /* ProductionMode.EXPORT.getId() */5);

		dtoNodePMode1.setNodeValue(market1);
		dtoNodePMode2.setNodeValue(market2);

		// setup LEVEL 2 (codeType)

		// Node structure
		ComponentBehaviorDto<StaticDataNodeValueDto> dtoNodeCodeType1 = new StaticDataCompositeBehaviorDto<StaticDataNodeValueDto>();
		ComponentBehaviorDto<StaticDataNodeValueDto> dtoNodeCodeType2 = new StaticDataCompositeBehaviorDto<StaticDataNodeValueDto>();
		ComponentBehaviorDto<StaticDataNodeValueDto> dtoNodeCodeType1ex = new StaticDataCompositeBehaviorDto<StaticDataNodeValueDto>();
		ComponentBehaviorDto<StaticDataNodeValueDto> dtoNodeCodeType3 = new StaticDataCompositeBehaviorDto<StaticDataNodeValueDto>();

		dtoNodePMode1.add(dtoNodeCodeType1);
		dtoNodePMode1.add(dtoNodeCodeType2);
		dtoNodePMode2.add(dtoNodeCodeType1ex);
		dtoNodePMode2.add(dtoNodeCodeType3);

		// values
		CodeTypeDto codeType1 = new CodeTypeDto();
		CodeTypeDto codeType2 = new CodeTypeDto();
		CodeTypeDto codeType1ex = new CodeTypeDto();
		CodeTypeDto codeType3 = new CodeTypeDto();

		final Long codeTypeId1 = 92222L;
		final Long codeTypeId2 = 93333L;
		final Long codeTypeId3 = 94444L;

		codeType1.setId(codeTypeId1);
		codeType2.setId(codeTypeId2);
		codeType1ex.setId(codeTypeId1); // same Id as codeType1, but just in another node
		codeType3.setId(codeTypeId3);

		dtoNodeCodeType1.setNodeValue(codeType1);
		dtoNodeCodeType2.setNodeValue(codeType2);
		dtoNodeCodeType1ex.setNodeValue(codeType1ex);
		dtoNodeCodeType3.setNodeValue(codeType3);

		// set up LEVEL 3 (sku's)

		// node structure
		StaticDataLeafBehaviorDto<StaticDataNodeValueDto> dtoNodeSku1 = new StaticDataLeafBehaviorDto<StaticDataNodeValueDto>();
		StaticDataLeafBehaviorDto<StaticDataNodeValueDto> dtoNodeSku2 = new StaticDataLeafBehaviorDto<StaticDataNodeValueDto>();
		StaticDataLeafBehaviorDto<StaticDataNodeValueDto> dtoNodeSku3 = new StaticDataLeafBehaviorDto<StaticDataNodeValueDto>();
		StaticDataLeafBehaviorDto<StaticDataNodeValueDto> dtoNodeSku4 = new StaticDataLeafBehaviorDto<StaticDataNodeValueDto>();
		StaticDataLeafBehaviorDto<StaticDataNodeValueDto> dtoNodeSku5 = new StaticDataLeafBehaviorDto<StaticDataNodeValueDto>();
		StaticDataLeafBehaviorDto<StaticDataNodeValueDto> dtoNodeSku6 = new StaticDataLeafBehaviorDto<StaticDataNodeValueDto>();

		dtoNodeCodeType1.add(dtoNodeSku1);
		dtoNodeCodeType1.add(dtoNodeSku2);
		dtoNodeCodeType2.add(dtoNodeSku3);
		dtoNodeCodeType1ex.add(dtoNodeSku4);
		dtoNodeCodeType3.add(dtoNodeSku5);
		dtoNodeCodeType3.add(dtoNodeSku6);

		// values
		final Long skuId1 = 9999991L;
		final Long skuId2 = 9999992L;
		final Long skuId3 = 9999993L;
		final Long skuId4 = 9999994L;
		final Long skuId5 = 9999995L;
		final Long skuId6 = 9999996L;

		SkuProductDto sku1 = new SkuProductDto();
		sku1.setId(skuId1);
		SkuProductDto sku2 = new SkuProductDto();
		sku2.setId(skuId2);
		SkuProductDto sku3 = new SkuProductDto();
		sku3.setId(skuId3);
		SkuProductDto sku4 = new SkuProductDto();
		sku4.setId(skuId4);
		SkuProductDto sku5 = new SkuProductDto();
		sku5.setId(skuId5);
		SkuProductDto sku6 = new SkuProductDto();
		sku6.setId(skuId6);

		dtoNodeSku1.setNodeValue(sku1);
		dtoNodeSku2.setNodeValue(sku2);
		dtoNodeSku3.setNodeValue(sku3);
		dtoNodeSku4.setNodeValue(sku4);
		dtoNodeSku5.setNodeValue(sku5);
		dtoNodeSku6.setNodeValue(sku6);

		// simulate the remote server sends the DTO tree structure
		when(activationBean.provideAuthorizedProducts()).thenReturn(rootNode);

		// ACTION
		ProductionParameterRootNode receivedNode = remoteServer.getTreeProductionParameters();

		// VERIFY LEVEL 0
		assertNotNull(receivedNode);

		List<IProductionParametersNode> firstLevel = receivedNode.getChildren();

		// VERIFY LEVEL 1

		// 4 children, market1 (standard), copied subtree (refeed1), copied subtree (refeed2) and export + maintenance
		// added automaticaly.
		assertEquals(5, receivedNode.getChildren().size());

		assertNotNull(getNodeByProductionMode(firstLevel, ProductionMode.STANDARD));
		assertNotNull(getNodeByProductionMode(firstLevel, ProductionMode.REFEED_NORMAL));
		assertNotNull(getNodeByProductionMode(firstLevel, ProductionMode.REFEED_CORRECTION));
		assertNotNull(getNodeByProductionMode(firstLevel, ProductionMode.EXPORT));
		assertNotNull(getNodeByProductionMode(firstLevel, ProductionMode.MAINTENANCE));

		// VERIFY LEVEL 2
		List<IProductionParametersNode> secondLevelStandard = getNodeByProductionMode(firstLevel,
				ProductionMode.STANDARD).getChildren();
		List<IProductionParametersNode> secondLevelRNormal = getNodeByProductionMode(firstLevel,
				ProductionMode.REFEED_NORMAL).getChildren();
		List<IProductionParametersNode> secondLevelRCorrection = getNodeByProductionMode(firstLevel,
				ProductionMode.REFEED_CORRECTION).getChildren();
		List<IProductionParametersNode> secondLevelExport = getNodeByProductionMode(firstLevel, ProductionMode.EXPORT)
				.getChildren();

		assertEquals(2, secondLevelStandard.size());
		assertEquals(2, secondLevelRNormal.size());
		assertEquals(2, secondLevelRCorrection.size());
		assertEquals(2, secondLevelExport.size());

		assertNotNull(getNodeByCodeTypeId(secondLevelStandard, codeTypeId1));
		assertNotNull(getNodeByCodeTypeId(secondLevelStandard, codeTypeId1).getChildren());
		assertNotNull(getNodeByCodeTypeId(secondLevelStandard, codeTypeId2));
		assertNotNull(getNodeByCodeTypeId(secondLevelStandard, codeTypeId2).getChildren());

		assertNotNull(getNodeByCodeTypeId(secondLevelRNormal, codeTypeId1));
		assertNotNull(getNodeByCodeTypeId(secondLevelRNormal, codeTypeId1).getChildren());
		assertNotNull(getNodeByCodeTypeId(secondLevelRNormal, codeTypeId2));
		assertNotNull(getNodeByCodeTypeId(secondLevelRNormal, codeTypeId2).getChildren());

		assertNotNull(getNodeByCodeTypeId(secondLevelRCorrection, codeTypeId1));
		assertNotNull(getNodeByCodeTypeId(secondLevelRCorrection, codeTypeId1).getChildren());
		assertNotNull(getNodeByCodeTypeId(secondLevelRCorrection, codeTypeId2));
		assertNotNull(getNodeByCodeTypeId(secondLevelRCorrection, codeTypeId2).getChildren());

		assertNotNull(getNodeByCodeTypeId(secondLevelExport, codeTypeId1));
		assertNotNull(getNodeByCodeTypeId(secondLevelExport, codeTypeId1).getChildren());
		assertNotNull(getNodeByCodeTypeId(secondLevelExport, codeTypeId3));
		assertNotNull(getNodeByCodeTypeId(secondLevelExport, codeTypeId3).getChildren());

		// VERIFY LEVEL 3
		List<IProductionParametersNode> thirdLevelStandardCT1 = getNodeByCodeTypeId(secondLevelStandard, codeTypeId1)
				.getChildren();
		List<IProductionParametersNode> thirdLevelStandardCT2 = getNodeByCodeTypeId(secondLevelStandard, codeTypeId2)
				.getChildren();

		List<IProductionParametersNode> thirdLevelRNormalCT1 = getNodeByCodeTypeId(secondLevelRNormal, codeTypeId1)
				.getChildren();
		List<IProductionParametersNode> thirdLevelRNormalCT2 = getNodeByCodeTypeId(secondLevelRNormal, codeTypeId2)
				.getChildren();

		List<IProductionParametersNode> thirdLevelRCorrectionCT1 = getNodeByCodeTypeId(secondLevelRCorrection,
				codeTypeId1).getChildren();
		List<IProductionParametersNode> thirdLevelRCorrectionCT2 = getNodeByCodeTypeId(secondLevelRCorrection,
				codeTypeId2).getChildren();

		List<IProductionParametersNode> thirdLevelExportCT1ex = getNodeByCodeTypeId(secondLevelExport, codeTypeId1)
				.getChildren();
		List<IProductionParametersNode> thirdLevelExportCT3 = getNodeByCodeTypeId(secondLevelExport, codeTypeId3)
				.getChildren();

		assertEquals(2, thirdLevelStandardCT1.size());
		assertEquals(1, thirdLevelStandardCT2.size());
		assertEquals(2, thirdLevelRNormalCT1.size());
		assertEquals(1, thirdLevelRNormalCT2.size());
		assertEquals(2, thirdLevelRCorrectionCT1.size());
		assertEquals(1, thirdLevelRCorrectionCT2.size());
		assertEquals(1, thirdLevelExportCT1ex.size());
		assertEquals(2, thirdLevelExportCT3.size());

		// verify the sku's codetype id is correct
		assertEquals(codeTypeId1.longValue(), getNodeBySkuId(thirdLevelStandardCT1, skuId1).getValue().getCodeType()
				.getId());
		assertEquals(codeTypeId1.longValue(), getNodeBySkuId(thirdLevelStandardCT1, skuId2).getValue().getCodeType()
				.getId());
		assertEquals(codeTypeId2.longValue(), getNodeBySkuId(thirdLevelStandardCT2, skuId3).getValue().getCodeType()
				.getId());

		assertEquals(codeTypeId1.longValue(), getNodeBySkuId(thirdLevelRNormalCT1, skuId1).getValue().getCodeType()
				.getId());
		assertEquals(codeTypeId1.longValue(), getNodeBySkuId(thirdLevelRNormalCT1, skuId2).getValue().getCodeType()
				.getId());
		assertEquals(codeTypeId2.longValue(), getNodeBySkuId(thirdLevelRNormalCT2, skuId3).getValue().getCodeType()
				.getId());

		assertEquals(codeTypeId1.longValue(), getNodeBySkuId(thirdLevelRCorrectionCT1, skuId1).getValue().getCodeType()
				.getId());
		assertEquals(codeTypeId1.longValue(), getNodeBySkuId(thirdLevelRCorrectionCT1, skuId2).getValue().getCodeType()
				.getId());
		assertEquals(codeTypeId2.longValue(), getNodeBySkuId(thirdLevelRCorrectionCT2, skuId3).getValue().getCodeType()
				.getId());

		assertEquals(codeTypeId1.longValue(), getNodeBySkuId(thirdLevelExportCT1ex, skuId4).getValue().getCodeType()
				.getId());
		assertEquals(codeTypeId3.longValue(), getNodeBySkuId(thirdLevelExportCT3, skuId5).getValue().getCodeType()
				.getId());
		assertEquals(codeTypeId3.longValue(), getNodeBySkuId(thirdLevelExportCT3, skuId6).getValue().getCodeType()
				.getId());

		// verify there are not skus in the wrong branch
		assertEquals(null, getNodeBySkuId(thirdLevelStandardCT1, skuId3));
		assertEquals(null, getNodeBySkuId(thirdLevelStandardCT2, skuId2));
		assertEquals(null, getNodeBySkuId(thirdLevelExportCT1ex, skuId3));
		assertEquals(null, getNodeBySkuId(thirdLevelExportCT3, skuId3));
		assertEquals(null, getNodeBySkuId(thirdLevelExportCT3, skuId4));

		// verify the sku's are leaf nodes
		assertEquals(0, getNodeBySkuId(thirdLevelStandardCT1, skuId1).getChildren().size());
		assertEquals(0, getNodeBySkuId(thirdLevelRNormalCT2, skuId3).getChildren().size());
		assertEquals(0, getNodeBySkuId(thirdLevelRCorrectionCT1, skuId2).getChildren().size());
		assertEquals(0, getNodeBySkuId(thirdLevelExportCT3, skuId5).getChildren().size());
	}

	@Test
	public void testProductionTreePrune() throws RemoteServerException, LoginException {
		/*
		 * Tree structure to test:
		 * 
		 * +RootNode (Level 0) | \___+Market1: Prod standard (Level 1) | | | \___+CodeType1 (Level 2) | | \___Sku1
		 * (Level 3) | | | \___+CodeType2 | \___Sku2 | | \___NAV2 (Not prune) (level4) | | | | | \___Sku3 | \___NAV1 (TO
		 * PRUNE) | | \___+Market2: Prod Export (TO PRUNE) | | | \___+CodeType3 | | \___+Market3: Prod Maintenance |
		 * \___+CodeType4 (TO PRUNE) | \___+Sku 998 \___Sku999
		 * 
		 * expected structure after tree prune:
		 * 
		 * +RootNode (Level 0) | \___+Market1: Prod standard (Level 1) | | | \___+CodeType1 (Level 2) | | \___Sku1
		 * (Level 3) | | | \___+CodeType2 | \___Sku2 | | \___NAV2 (Not prune) (level4) | | | | | \___Sku3 | |
		 * \___+Market3: Prod Maintenance | \___+Sku 998 \___Sku999
		 */

		// prepare

		// LEVEL 0 (root)
		IProductionParametersNode node;
		ProductionParameterRootNode root = new ProductionParameterRootNode();

		// LEVEL 1 (production mode)

		ProductionModeNode pm1 = new ProductionModeNode(ProductionMode.STANDARD);
		ProductionModeNode pm2 = new ProductionModeNode(ProductionMode.EXPORT);
		ProductionModeNode pm3 = new ProductionModeNode(ProductionMode.MAINTENANCE); // must be pruned

		root.addChildren(pm1);
		root.addChildren(pm2);
		root.addChildren(pm3);

		// LEVEL 2 (code type)
		CodeTypeNode ct1 = new CodeTypeNode(new CodeType(1));
		CodeTypeNode ct2 = new CodeTypeNode(new CodeType(2));
		CodeTypeNode ct3 = new CodeTypeNode(new CodeType(3));
		SKUNode sku998 = new SKUNode(new SKU(998));
		CodeTypeNode ct4 = new CodeTypeNode(new CodeType(4));

		pm1.addChildren(ct1);
		pm1.addChildren(ct2);
		pm2.addChildren(ct3); // must be pruned
		pm3.addChildren(sku998); // must be pruned
		pm3.addChildren(ct4); // must be pruned

		// LEVEL 3 (sku's & 1 navigation node)

		SKUNode sku1 = new SKUNode(new SKU(1));
		SKUNode sku2 = new SKUNode(new SKU(2));
		SKUNode sku3 = new SKUNode(new SKU(3));
		NavigationNode nav1 = new NavigationNode("Navigation1");
		SKUNode sku999 = new SKUNode(new SKU(999));

		ct1.addChildren(sku1);
		ct2.addChildren(sku2);
		ct2.addChildren(sku3);
		ct2.addChildren(nav1); // must be pruned
		sku998.addChildren(sku999);

		// LEVEL 4
		NavigationNode nav2 = new NavigationNode("Navigation2");
		sku2.addChildren(nav2);

		// simulate the remote server sends the DTO tree structure
		logoutdone = true;
		remoteServer.pruneParametersTree(root);

		// verify

		// Level 0
		assertNotNull(root);
		assertEquals(2, root.getChildren().size());

		// Level 1
		assertEquals(pm1, getNodeByProductionMode(root.getChildren(), ProductionMode.STANDARD));
		assertEquals(2, pm1.getChildren().size());

		assertEquals(null, getNodeByProductionMode(root.getChildren(), ProductionMode.EXPORT));

		assertEquals(pm3, getNodeByProductionMode(root.getChildren(), ProductionMode.MAINTENANCE));
		assertEquals(1, pm3.getChildren().size());

		// Level 2
		assertEquals(ct1, getNodeByCodeTypeId(pm1.getChildren(), 1L));
		assertEquals(ct2, getNodeByCodeTypeId(pm1.getChildren(), 2L));

		assertEquals(1, ct1.getChildren().size());
		assertEquals(2, ct2.getChildren().size());

		assertEquals(sku998, pm3.getChildren().get(0));
		assertEquals(1, pm3.getChildren().get(0).getChildren().size());

		// LEVEL 3
		assertEquals(sku1, getNodeBySkuId(ct1.getChildren(), 1L));
		assertEquals(sku2, getNodeBySkuId(ct2.getChildren(), 2L));
		assertEquals(sku3, getNodeBySkuId(ct2.getChildren(), 3L));

		// LEVEL 4
		assertEquals(1, sku2.getChildren().size());
		assertEquals(nav2, sku2.getChildren().get(0));

	}

	private ProductionModeNode getNodeByProductionMode(List<IProductionParametersNode> list, ProductionMode mode) {
		for (IProductionParametersNode node : list) {
			if (node instanceof ProductionModeNode && ((ProductionMode) node.getValue()).getId() == mode.getId()) {
				return (ProductionModeNode) node;
			}
		}
		return null;
	}

	private CodeTypeNode getNodeByCodeTypeId(List<? extends IProductionParametersNode> secondLevel, Long codeTypeId) {
		for (IProductionParametersNode node : secondLevel) {
			if (node instanceof CodeTypeNode && ((CodeType) node.getValue()).getId() == codeTypeId) {
				return (CodeTypeNode) node;
			}
		}
		return null;
	}

	private SKUNode getNodeBySkuId(List<? extends IProductionParametersNode> secondLevel, Long skuId) {
		for (IProductionParametersNode node : secondLevel) {
			if (node instanceof SKUNode && ((SKU) node.getValue()).getId() == skuId) {
				return (SKUNode) node;
			}
		}
		return null;
	}

}
