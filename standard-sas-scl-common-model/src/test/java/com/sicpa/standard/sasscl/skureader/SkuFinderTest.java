package com.sicpa.standard.sasscl.skureader;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class SkuFinderTest {

	SkuFinder finder = new SkuFinder();
	ProductionParameters productionParameters = new ProductionParameters();
	SkuListProvider skuListProvider = new SkuListProvider();

	static final int SKU_ID_DOMESTIC = 1;
	static final int SKU_ID_EXPORT = 4;
	static final int SKU_ID2_EXPORT = 5;

	static String APPEARANCE_CODE_1 = "123456";
	static String APPEARANCE_CODE_2 = "ABCDE";

	ProductionModeNode domestic = new ProductionModeNode(ProductionMode.STANDARD);
	ProductionModeNode export = new ProductionModeNode(ProductionMode.EXPORT);

	@Before
	public void setup() {
		finder.setProductionParameters(productionParameters);
		finder.setSkuListProvider(skuListProvider);

		ProductionParameterRootNode root = new ProductionParameterRootNode();

		domestic.addChildren(createSkuNode(SKU_ID_DOMESTIC, APPEARANCE_CODE_1));
		export.addChildren(createSkuNode(SKU_ID_EXPORT, APPEARANCE_CODE_1));
		export.addChildren(createSkuNode(SKU_ID2_EXPORT, APPEARANCE_CODE_2));

		root.addChildren(domestic, export);

		skuListProvider.set(root);

	}

	private SKUNode createSkuNode(int id, String appearance) {
		SKU sku = new SKU(id, id + "");
		sku.setAppearanceCode(appearance);
		return new SKUNode(sku);
	}

	@Test
	public void testUniqueSkuFoundById() throws Exception {
		selectProductionMode(ProductionMode.EXPORT);
		Optional<SKU> sku = findSkuById(SKU_ID_DOMESTIC);
		uniqueSkuFound(SKU_ID_EXPORT, sku);
	}

	@Test
	public void testNoSkuFoundInCurrentProductionModeById() throws Exception {
		selectProductionMode(ProductionMode.STANDARD);
		Optional<SKU> sku = findSkuById(SKU_ID2_EXPORT);
		noUniqueSkuFound(sku);
	}

	@Test
	public void testTooManySkuFoundById() throws Exception {
		givenSkusWithSameAppearanceInExport();
		selectProductionMode(ProductionMode.EXPORT);
		Optional<SKU> sku = findSkuById(SKU_ID_EXPORT);
		noUniqueSkuFound(sku);
	}

	@Test
	public void testNoSkuFoundById() throws Exception {
		selectProductionMode(ProductionMode.EXPORT);
		int unknownSkuId = 46;
		Optional<SKU> sku = findSkuById(unknownSkuId);
		noUniqueSkuFound(sku);
	}

	private void uniqueSkuFound(int id, Optional<SKU> sku) {
		assertEquals(id, sku.get().getId());
	}

	private void givenSkusWithSameAppearanceInExport() {
		int duplicateAppearanceSkuId = 456;
		export.addChildren(createSkuNode(duplicateAppearanceSkuId, APPEARANCE_CODE_1));
	}

	private void noUniqueSkuFound(Optional<SKU> sku) {
		assertFalse(sku.isPresent());
	}

	private Optional<SKU> findSkuById(int id) {
		return finder.getSkuFromId(id);
	}

	private void selectProductionMode(ProductionMode mode) {
		productionParameters.setProductionMode(mode);
	}
}
