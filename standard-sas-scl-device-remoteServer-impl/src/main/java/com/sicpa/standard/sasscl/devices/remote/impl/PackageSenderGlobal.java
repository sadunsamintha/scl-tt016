package com.sicpa.standard.sasscl.devices.remote.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.remote.datasender.DataRegisteringException;
import com.sicpa.standard.sasscl.devices.remote.datasender.IPackageSender;
import com.sicpa.standard.sasscl.devices.remote.datasender.IPackageSenderGlobal;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.IDtoConverter;
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.IRemoteServices;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsResultDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;

public class PackageSenderGlobal implements IPackageSenderGlobal {

	private static final Logger logger = LoggerFactory.getLogger(PackageSenderGlobal.class);

	private final IPackageSender senderActivated = (products) -> processActivatedProducts(products);
	private final IPackageSender senderCounted = (products) -> processCountedProducts(products);

	private final Map<ProductStatus, IPackageSender> packageSenders = new HashMap<>();

	private IDtoConverter converter;
	private IRemoteServices remoteServices;

	public PackageSenderGlobal() {
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

		if (null == packageSenders.get(products.getProductStatus())) {
			logger.error("No sender found for the package type in file: " + products.getFileName());
			return;
		}
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
		AuthenticatedProductsResultDto authenticatedProductsResultDto = converter.convertToActivationDto(products);
		try {
			remoteServices.registerActivationProducts(authenticatedProductsResultDto);
		} catch (ActivationException e) {
			throw new DataRegisteringException(e);
		}
	}

	private void processCountedProducts(PackagedProducts products) throws DataRegisteringException {
		if (products.getProductStatus().equals(ProductStatus.MAINTENANCE)) {
			// DMS does not handle maintenance mode for now
			return;
		}

		CountedProductsResultDto countedProductsResultDto = converter.convertToCountedDto(products);
		try {
			remoteServices.registerCountedProducts(countedProductsResultDto);
		} catch (ActivationException e) {
			throw new DataRegisteringException(e);
		}
	}

	public void setConverter(IDtoConverter converter) {
		this.converter = converter;
	}

	public void setRemoteServices(IRemoteServices remoteServices) {
		this.remoteServices = remoteServices;
	}
}
