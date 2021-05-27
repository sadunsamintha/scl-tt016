package com.sicpa.tt085.devices.remote.impl;

import static com.sicpa.std.common.api.activation.business.ActivationServiceHandler.LABELED_PRODUCT_TYPE;
import static com.sicpa.std.common.api.activation.business.ActivationServiceHandler.MARKED_PRODUCT_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrAuthenticatedProductsResultDto;
import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrCountedProductsResultDto;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.remote.datasender.DataRegisteringException;
import com.sicpa.standard.sasscl.devices.remote.datasender.IPackageSender;
import com.sicpa.standard.sasscl.devices.remote.datasender.IPackageSenderGlobal;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.std.common.api.activation.dto.ProcessedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.ProcessedProductsStatusDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;
import com.sicpa.tt085.model.provider.CountryProvider;
import com.sicpa.tt085.remote.remoteservices.ITT085RemoteServices;

public class TT085PackageSenderGlobal implements IPackageSenderGlobal, CountryProvider{

	private static final Logger logger = LoggerFactory.getLogger(TT085PackageSenderGlobal.class);
	private final IPackageSender senderActivated = (products) -> processActivatedProducts(products);
	private final IPackageSender senderCounted = (products) -> processCountedProducts(products);
	private final Map<ProductStatus, IPackageSender> packageSenders = new HashMap<>();
	private ITT085RemoteServices remoteServices;
	protected IRemoteServerProductStatusMapping productStatusMapping;
	
	public TT085PackageSenderGlobal() {
		initPackageSenders();
	}

	private void initPackageSenders() {
		addToActivatedPackager(ProductStatus.AUTHENTICATED);
		addToActivatedPackager(ProductStatus.REFEED);
		addToActivatedPackager(ProductStatus.SENT_TO_PRINTER_UNREAD);
		addToActivatedPackager(ProductStatus.SENT_TO_PRINTER_WASTED);
		addToActivatedPackager(ProductStatus.TYPE_MISMATCH);
		addToActivatedPackager(ProductStatus.INK_DETECTED);

		addToCounterPackager(ProductStatus.COUNTING);
		addToCounterPackager(ProductStatus.EXPORT);
		addToCounterPackager(ProductStatus.MAINTENANCE);
		addToCounterPackager(ProductStatus.NOT_AUTHENTICATED);
		addToCounterPackager(ProductStatus.UNREAD);
	}

	@Override
	public void sendPackage(PackagedProducts products) throws DataRegisteringException {
		logger.info("products.getProductStatus() >>>> " + products.getProductStatus());
		logger.info("packageSenders.get(products.getProductStatus()) >>>> " + packageSenders.get(products.getProductStatus()));
		logger.info("packageSenders >>>> " + packageSenders);
		if (null == packageSenders.get(products.getProductStatus())) {
			logger.error("No sender found for the package type in file: " + products.getFileName());
			return;
		}
		CustoBuilder.addPropertyToClass(Product.class, country );
		packageSenders.get(products.getProductStatus()).sendPackage(products);
	}

	@Override
	public void addToActivatedPackager(ProductStatus status) {
		packageSenders.put(status, senderActivated);
	}

	@Override
	public void addToCounterPackager(ProductStatus status) {
		packageSenders.put(status, senderCounted);
	}

	private void processActivatedProducts(PackagedProducts products) throws DataRegisteringException {
		 TrAuthenticatedProductsResultDto authenticatedProductsResultDto = generateAuthenticatedProductsResultDto(products);
		try {
			remoteServices.registerActivationProducts(authenticatedProductsResultDto);
		} catch (ActivationException e) {
			throw new DataRegisteringException(e);
		}
	}

	private TrAuthenticatedProductsResultDto generateAuthenticatedProductsResultDto(PackagedProducts products) {
        TrAuthenticatedProductsResultDto authenticatedProductsResultDto = new TrAuthenticatedProductsResultDto();
        ArrayList<AuthenticatedProductDto> authenticatedProductsDto = new ArrayList<AuthenticatedProductDto>();
        ProcessedProductsStatusDto statusDto = new ProcessedProductsStatusDto();
        statusDto.setValue(getTurkeyRemoteServerProductStatus(products
				.getProductStatus()));
        // create a product dto for each product
        for (Product product : products.getProducts()) {

        	long codeTypeId = product.getCode().getCodeType() != null ? product.getCode()
					.getCodeType().getId() : product.getSku().getCodeType().getId();
            authenticatedProductsDto.add(new AuthenticatedProductDto((long) product.getSku().getId(), codeTypeId,
            		product.getCode().getEncoderId(), product.getCode().getSequence(),
                    product.getActivationDate()));
            
            // Set Country for Export Coding
            if (product.getProperty(country) != null) {
            	authenticatedProductsResultDto.setCountryId(product.getProperty(country).getId());
        	}
        }
        populateResultDtoInfo(authenticatedProductsResultDto, products);
        authenticatedProductsResultDto.setProcessedProducts(authenticatedProductsDto);
        authenticatedProductsResultDto.setProcessedProductsStatusDto(statusDto);
        authenticatedProductsResultDto.setActivationType(getActivationServiceKey(products));
        return authenticatedProductsResultDto;
    }
	
	protected String getActivationServiceKey(PackagedProducts products) {

		if (products.isPrinted()) {
			// if SCL
			return MARKED_PRODUCT_TYPE;
		}
		// if SAS
		return LABELED_PRODUCT_TYPE;
	}

	
	private void processCountedProducts(PackagedProducts products) throws DataRegisteringException {
		if (products.getProductStatus().equals(ProductStatus.MAINTENANCE)) {
			// DMS does not handle maintenance mode for now
			return;
		}
		TrCountedProductsResultDto trCountedProductsResultDto = new TrCountedProductsResultDto();
		List<CountedProductsDto> countedProductsDto = createCountedProductDto(products.getProducts(),trCountedProductsResultDto);
		try {
			for(CountedProductsDto cp : countedProductsDto) {
				 populateResultDtoInfo(trCountedProductsResultDto, products);
				 trCountedProductsResultDto.setProcessedProducts(cp);
				 ProcessedProductsStatusDto processedProductStatusDto = new ProcessedProductsStatusDto();
				 processedProductStatusDto.setValue(getTurkeyRemoteServerProductStatus(products
							.getProductStatus()));
				 trCountedProductsResultDto.setProcessedProductsStatusDto(processedProductStatusDto);
				 remoteServices.registerCountedProducts(trCountedProductsResultDto);
			}
		} catch (ActivationException e) {
			throw new DataRegisteringException(e);
		} catch(Exception e) {
			e.printStackTrace();
		}
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
	
	protected void populateResultDtoInfo(final ProcessedProductsResultDto dto, final PackagedProducts products) {
        dto.setSubsystemId(products.getSubsystem());
        dto.setIdTransaction(System.currentTimeMillis());
        dto.setIdProductionBatch(String.valueOf(System.currentTimeMillis()));
    }
	
	private List<CountedProductsDto> createCountedProductDto(List<Product> products, TrCountedProductsResultDto trCountedProductsResultDto) {
		long skuId = 0;
        long codeTypeId = 0;
        HashMap<Long, CountedProductsDto> mapCountedProductsDto = new HashMap<Long, CountedProductsDto>();

        for(Product product : products) {
        	if(product.getProperty(country)!=null) {
        		trCountedProductsResultDto.setCountryId(product.getProperty(country).getId());
        	}
            skuId = product.getSku().getId();
            codeTypeId = product.getSku().getCodeType().getId();

            if(mapCountedProductsDto.containsKey(skuId)) {
                // Update already created CountedProductsDto
                CountedProductsDto countedProductsDto = mapCountedProductsDto.get(skuId);
                countedProductsDto.setQuantity(countedProductsDto.getQuantity() + 1);

                if (product.getActivationDate().before(countedProductsDto.getStartDateTime())) {
                    countedProductsDto.setStartDateTime(product.getActivationDate());
                }
                if (product.getActivationDate().after(countedProductsDto.getEndDateTime())) {
                    countedProductsDto.setEndDateTime(product.getActivationDate());
                }
            } else {
                mapCountedProductsDto.put(skuId,
                        new CountedProductsDto(skuId, codeTypeId, new Long(1), product.getActivationDate(), product.getActivationDate()));
            }
        }


        return new ArrayList<CountedProductsDto>(mapCountedProductsDto.values());
	}

	public void setRemoteServices(ITT085RemoteServices remoteServices) {
		this.remoteServices = remoteServices;
	}
	
	public void setProductStatusMapping(IRemoteServerProductStatusMapping productStatusMapping) {
		this.productStatusMapping = productStatusMapping;
	}
}
