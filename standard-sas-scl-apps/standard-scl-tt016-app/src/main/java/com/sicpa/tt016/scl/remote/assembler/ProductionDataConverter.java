package com.sicpa.tt016.scl.remote.assembler;

import static com.sicpa.tt016.common.dto.NonActivationSessionDTO.EXPORT_SESSION;
import static com.sicpa.tt016.common.dto.NonActivationSessionDTO.MAINTENANCE_SESSION;
import static com.sicpa.tt016.common.dto.NonActivationSessionDTO.OFFLINE_SESSION;
import static com.sicpa.tt016.common.model.EjectionReason.UNREADABLE_INT;
import static com.sicpa.tt016.common.model.EjectionReason.UNREADABLE_WITH_CODE_INT;
import static com.sicpa.tt016.common.model.ProductStatus.VALID_ACTIV_INT;
import static com.sicpa.tt016.common.model.ProductStatus.VALID_TYPE_MISMATCH_INT;
import static com.sicpa.tt016.scl.remote.remoteservices.ITT016RemoteServices.PRODUCTION_MODE_STANDARD;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt016.common.dto.*;
import com.sicpa.tt016.common.model.ActivationEjection;
import com.sicpa.tt016.common.model.CodeType;
import com.sicpa.tt016.common.model.EjectionReason;
import com.sicpa.tt016.common.model.SKU;
import com.sicpa.tt016.common.model.Subsystem;

public class ProductionDataConverter {

	public CodingActivationSessionDTO convertAuthenticated(PackagedProducts products, int subsystemId) {
		List<CodingActivationDTO> activated = new ArrayList<>();
		for (Product p : products.getProducts()) {
			activated.add(convertProduct(p, subsystemId, products.getProductStatus()));
		}
		CodingActivationSessionDTO res = new CodingActivationSessionDTO(activated);
		res.setQty(activated.size());
		return res;
	}

	private CodingActivationDTO convertProduct(Product product, int subsystemId, ProductStatus status) {
		int remoteStatus = getRemoteProductStatus(status);
		int codeTypeId = getCodeTypeId(product);
		long encoderId = product.getCode().getEncoderId();
		long seq = product.getCode().getSequence();
		int skuId = product.getSku().getId();

		CodingActivationDTO remoteProduct = new CodingActivationDTO(encoderId, seq, product.getActivationDate(),
				remoteStatus, codeTypeId, skuId, subsystemId, null);

		return remoteProduct;
	}

	private int getRemoteProductStatus(ProductStatus status) {
		int remoteStatus = VALID_ACTIV_INT;

		if (status.equals(ProductStatus.UNREAD)) {
			remoteStatus = UNREADABLE_WITH_CODE_INT;
		} else if (status.equals(ProductStatus.NOT_AUTHENTICATED)) {
			remoteStatus = UNREADABLE_WITH_CODE_INT;
		} else if (status.equals(ProductStatus.TYPE_MISMATCH)) {
			remoteStatus = VALID_TYPE_MISMATCH_INT;
		}
		return remoteStatus;
	}

	private int getCodeTypeId(Product product) {
		int codeTypeId;
		com.sicpa.standard.sasscl.model.CodeType ctFromCode = product.getCode().getCodeType();
		if (ctFromCode == null) {
			// SCL - from the selected sku
			codeTypeId = (int) product.getSku().getCodeType().getId();
		} else {
			codeTypeId = (int) ctFromCode.getId();
		}
		return codeTypeId;
	}

	public IEjectionDTO convertEjection(PackagedProducts products, int subsystemId) {
		int qty = products.getProducts().size();
		CodeType ct = new CodeType(getCodeTypeId(products));
		SKU sku = new SKU(getSkuId(products));
		Subsystem subsystem = new Subsystem(subsystemId);

		ActivationEjection ejection = new ActivationEjection(0L, qty, new EjectionReason(UNREADABLE_INT), new Date(),
				ct, sku, subsystem, PRODUCTION_MODE_STANDARD);

		ActivationEjectionDTO ejectionDTO = new ActivationEjectionDTO(ejection);
		ejectionDTO.setTimestamps(getDates(products));

		return ejectionDTO;
	}

	public ExportSessionDTO convertExport(PackagedProducts products, int subsystemId) {
		int qty = products.getProducts().size();
		int skuId = getSkuId(products);

		ExportSessionDTO session = new ExportSessionDTO(1L, EXPORT_SESSION, qty, new Date(), skuId, subsystemId);
		session.setTimestamps(getDates(products));
		return session;
	}

	public MaintenanceSessionDTO convertMaintenance(PackagedProducts products, int subsystemId) {
		int qty = products.getProducts().size();

		MaintenanceSessionDTO session = new MaintenanceSessionDTO(1L, MAINTENANCE_SESSION, qty, new Date(), subsystemId);
		session.setTimestamps(getDates(products));
		return session;
	}

	public OfflineSessionDTO convertOffline(PackagedProducts products, int subsystemId) {
		int qty = products.getProducts().size();
		int skuId = getSkuId(products);

		OfflineSessionDTO session = new OfflineSessionDTO(1L, OFFLINE_SESSION, qty, new Date(), skuId, subsystemId);
		session.setTimestamps(getDates(products));

		return session;
	}

	private List<Date> getDates(PackagedProducts products) {
		List<Date> dates = new ArrayList<>();
		for (Product p : products.getProducts()) {
			dates.add(p.getActivationDate());
		}
		return dates;
	}

	private int getSkuId(PackagedProducts products) {
		return products.getProducts().get(0).getSku().getId();
	}

	private int getCodeTypeId(PackagedProducts products) {
		return (int) products.getProducts().get(0).getSku().getCodeType().getId();
	}
}
