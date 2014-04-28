package com.sicpa.standard.sasscl.model.productionParameters.impl;

import javax.swing.ImageIcon;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.CategoryNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.CodeTypeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;

public class ProductionParametersImpTest {

	@SuppressWarnings("rawtypes")
	private AbstractProductionParametersNode paramsNode;

	private CategoryNode categoryNode;
	private CodeTypeNode codeTypeNode;
	private ProductionModeNode productionModeNode;
	private SKUNode skuNode;

	private CodeType codeType;
	private ProductionMode productionMode;
	private SKU sku;

	@Before
	public void setUp() {
		this.codeType = new CodeType(1);
		this.codeType.setDescription("type1");
		this.productionMode = ProductionMode.STANDARD;
		this.sku = new SKU(2, "SKU02");

		this.categoryNode = new CategoryNode(1, "category", new ImageIcon());
		this.codeTypeNode = new CodeTypeNode(this.codeType);
		this.productionModeNode = new ProductionModeNode(this.productionMode);
		this.skuNode = new SKUNode(this.sku);

		this.paramsNode = new ProductionParameterRootNode();
		this.paramsNode.addChildren(this.categoryNode);
		this.paramsNode.addChildren(this.codeTypeNode);
		this.paramsNode.addChildren(this.productionModeNode);
		this.paramsNode.addChildren(this.skuNode);
	}

	@Test
	public void testCategoryNode() {
		CategoryNode node = (CategoryNode) this.paramsNode.getChildren().get(0);
		Assert.assertEquals(1, node.getId());
		Assert.assertEquals(Messages.get("category"), node.getText());
		Assert.assertEquals("", node.getFormatedTextForSummary());
		Assert.assertFalse(node.isShownOnSummary());
	}

	@Test
	public void testCodeTypeNode() {
		CodeTypeNode node = (CodeTypeNode) this.paramsNode.getChildren().get(1);
		Assert.assertEquals(Messages.get("type1"), node.getText());
		Assert.assertEquals(this.codeType, node.getValue());
		// same functionality as getText
		Assert.assertEquals(Messages.get("type1"), node.getFormatedTextForSummary());
		// get codeTypeId
		Assert.assertEquals(this.codeType.getId(), node.getId());
		Assert.assertTrue(node.isShownOnSummary());
	}

	@Test
	public void testProductionModeNode() {
		ProductionModeNode node = (ProductionModeNode) this.paramsNode.getChildren().get(2);
		Assert.assertEquals(Messages.get(ProductionMode.STANDARD.getDescription()), node.getText());
		Assert.assertEquals(this.productionMode, node.getValue());
		// same functionality as getText
		Assert.assertEquals(Messages.get(ProductionMode.STANDARD.getDescription()), node.getFormatedTextForSummary());
		// get productionMode
		Assert.assertEquals(this.productionMode.getId(), node.getId());
		Assert.assertTrue(node.isShownOnSummary());
	}

	@Test
	public void testSKUNode() {
		SKUNode node = (SKUNode) this.paramsNode.getChildren().get(3);
		Assert.assertEquals("SKU02", node.getText());
		Assert.assertEquals(this.sku, node.getValue());
		// same functionality as getText
		Assert.assertEquals("SKU02", node.getFormatedTextForSummary());
		// get skuId
		Assert.assertEquals(this.sku.getId(), node.getId());
		Assert.assertTrue(node.isShownOnSummary());
	}
}
