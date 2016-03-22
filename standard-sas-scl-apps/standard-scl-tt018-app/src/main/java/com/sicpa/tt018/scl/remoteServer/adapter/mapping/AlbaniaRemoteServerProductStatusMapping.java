package com.sicpa.tt018.scl.remoteServer.adapter.mapping;

import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductStatuses;
import com.sicpa.tt018.scl.model.AlbaniaProductStatus;
import com.sicpa.tt018.scl.utils.Mapping;

public class AlbaniaRemoteServerProductStatusMapping extends Mapping<ProductStatus, ProductStatuses> {
	@Override
	protected void populateMap() {
		addEntry(ProductStatus.AUTHENTICATED, ProductStatuses.ACTIVATED);

		addEntry(ProductStatus.NOT_AUTHENTICATED, ProductStatuses.EJECTED);
		addEntry(ProductStatus.UNREAD, ProductStatuses.EJECTED);

		addEntry(ProductStatus.EXPORT, null);
		addEntry(ProductStatus.MAINTENANCE, null);

		addEntry(AlbaniaProductStatus.SOFT_DRINK, null);

		addEntry(AlbaniaProductStatus.SENT_TO_PRINTER_BLOB, ProductStatuses.BLOB_ACTIVATED);

		addEntry(ProductStatus.SENT_TO_PRINTER_UNREAD, ProductStatuses.EJECTED);
		addEntry(ProductStatus.SENT_TO_PRINTER_WASTED, ProductStatuses.PRINTER_WASTED);

	}

}
