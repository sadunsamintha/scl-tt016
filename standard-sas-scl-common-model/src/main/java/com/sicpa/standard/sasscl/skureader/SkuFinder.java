package com.sicpa.standard.sasscl.skureader;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class SkuFinder implements ISkuFinder {

	private static final Logger logger = LoggerFactory.getLogger(SkuFinder.class);

	private SkuListProvider skuListProvider;
	private ProductionParameters productionParameters;

	@Override
	public ProductionMode getCurrentProductionMode(){
		return productionParameters.getProductionMode();
	}
	
	@Override
	public Optional<SKU> getSkuFromBarcode(String barcode) {
		List<SKU> skus = filter(skuListProvider.getAvailableSKUsForProductionMode(productionParameters
				.getProductionMode()), sku -> sku.getBarCodes().contains(barcode));
		if (skus.isEmpty()) {
			logger.error("no sku found for barcode:" + barcode);
			return empty();
		} else if (skus.size() > 1) {
			logger.error("too many skus found for barcode:" + barcode);
			return empty();
		} else {
			return Optional.of(skus.get(0));
		}
	}

	@Override
	public Optional<SKU> getSkuFromId(int id) {

		Optional<SKU> recognizedSku = getSkuById(id);
		if (!recognizedSku.isPresent()) {
			logger.error("no sku found for id :" + id);
			return Optional.empty();
		}

		String appearance = recognizedSku.get().getAppearanceCode();
		List<SKU> possibleSkus = getSkusByAppearance(appearance);
		List<SKU> skusFromCurrentProductionMode = getSkuMatchingProductionMode(id, possibleSkus);

		if (skusFromCurrentProductionMode.isEmpty()) {
			logger.error("no sku found for current production mode matching id:" + id);
			return Optional.empty();
		} else if (skusFromCurrentProductionMode.size() > 1) {
			logger.error("too many sku found for current production mode matching id:" + id);
			return Optional.empty();
		} else {
			return Optional.of(skusFromCurrentProductionMode.get(0));
		}
	}

	private List<SKU> getSkuMatchingProductionMode(int id, List<SKU> possibleSkus) {
		List<Integer> possibleSkusId = possibleSkus.stream().map(sku -> sku.getId()).collect(toList());

		Set<SKU> skusForCurProdMod = getSkusForCurrentProductionMode();
		List<SKU> res = skusForCurProdMod.stream().filter(sku -> possibleSkusId.contains(sku.getId()))
				.collect(toList());
		return res;
	}

	private Set<SKU> getSkusForCurrentProductionMode() {
		return skuListProvider.getAvailableSKUsForProductionMode(productionParameters.getProductionMode());
	}

	private List<SKU> getSkusByAppearance(String appearance) {
		return filter(skuListProvider.getAvailableSKUs(), sku -> sku.getAppearanceCode().equals(appearance));
	}

	private Optional<SKU> getSkuById(int id) {
		List<SKU> res = filter(skuListProvider.getAvailableSKUs(), sku -> sku.getId() == id);
		if (res.isEmpty()) {
			return empty();
		} else {
			return Optional.of(res.get(0));
		}
	}

	private List<SKU> filter(Collection<SKU> skus, Predicate<SKU> filter) {
		return skus.stream().filter(filter).collect(toList());
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
	
}
