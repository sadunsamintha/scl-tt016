package com.sicpa.tt018.scl.remoteServer.utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.CodeListEncoder;
import com.sicpa.tt018.interfaces.scl.master.dto.AlbaniaEncoderDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.MarketTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.SkuProductDTO;
import com.sicpa.tt018.scl.model.encoder.AlbaniaEncoderWrapper;

public class AlbaniaRemoteServerUtilities {
	public static List<IEncoder> wrapEncoderList(final List<AlbaniaEncoderDTO> encoders, final int year,
			final int subsystemID, final ICryptoFieldsConfig cryptoFieldsConfig, final String cryptoPassword,
			CodeType codeType) {
		final List<IEncoder> ret = new LinkedList<IEncoder>();
		for (final AlbaniaEncoderDTO albaniaEncoder : encoders) {

			ret.add(new AlbaniaEncoderWrapper(albaniaEncoder.getCryptoEncoder().getBatchId(), System
					.currentTimeMillis(), cryptoPassword, cryptoFieldsConfig, year, subsystemID, new Integer(""
					+ codeType.getId()), albaniaEncoder.getCryptoEncoder()));
		}
		return ret;
	}

	public static IEncoder wrapEncoder(AlbaniaEncoderDTO encoder, final int year, final int subsystemID,
			final ICryptoFieldsConfig cryptoFieldsConfig, final String cryptoPassword, int codeTypeId) {
		return new AlbaniaEncoderWrapper(encoder.getCryptoEncoder().getBatchId(), System.currentTimeMillis(),
				cryptoPassword, cryptoFieldsConfig, year, subsystemID, codeTypeId, encoder.getCryptoEncoder());
	}

	public static boolean isEmpty(final MarketTypeDTO marketTypeDTO) {
		return marketTypeDTO == null || marketTypeDTO.getSkuList() == null || marketTypeDTO.getSkuList().isEmpty();
	}

	public static boolean isComplete(final SkuProductDTO skuProductDTO) {
		return skuProductDTO != null && !StringUtils.isEmpty(skuProductDTO.getBrand())
				&& !StringUtils.isEmpty(skuProductDTO.getDescription());
	}

	public static boolean isEmpty(final PackagedProducts products) {
		return products == null || products.getProducts() == null || products.getProducts().isEmpty();
	}

	public static Boolean isListEncoder(IEncoder encoder) {
		if (encoder == null) {
			return false;
		}
		return encoder instanceof CodeListEncoder;
	}

	public static class SkuDtoSorter implements Comparator<SkuProductDTO> {
		@Override
		public int compare(final SkuProductDTO sku1, final SkuProductDTO sku2) {
			// Both null
			if (sku1 == null && sku2 == null) {
				return 0;
			}
			// Brand 1 < Brand 2
			if (sku1 == null || sku1.getBrand() == null || sku1.getBrand().compareTo(sku2.getBrand()) < 0) {
				return -1;
			}
			// Brand 1 > Brand 2
			if (sku1.getBrand().compareTo(sku2.getBrand()) > 0) {
				return 1;
			}
			// -------------------------------//
			// Brand 1 == Brand 2
			// -------------------------------//
			// Variant 1 < Variant 2
			if (sku1.getVariant() == null || sku1.getVariant().compareTo(sku2.getVariant()) < 0) {
				return -1;
			}
			// Variant 1 > Variant 2
			if (sku1.getVariant().compareTo(sku2.getVariant()) > 0) {
				return 1;
			}
			// -------------------------------//
			// Variant 1 == Variant 2
			// -------------------------------//
			// Description 1 < Description 2
			if (sku1.getDescription() == null || sku1.getDescription().compareTo(sku2.getDescription()) < 0) {
				return -1;
			}
			// Description 1 > Description 2
			if (sku1.getDescription().compareTo(sku2.getDescription()) > 0) {
				return 1;
			}
			// -------------------------------//
			// Description 1 == Description 2
			// -------------------------------//
			// SKU are equals
			return 0;
		}

		public static List<SkuProductDTO> sort(List<SkuProductDTO> list) {
			Collections.sort(list, new SkuDtoSorter());
			return list;
		}
	}

}
