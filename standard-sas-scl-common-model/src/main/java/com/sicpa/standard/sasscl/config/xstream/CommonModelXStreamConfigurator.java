package com.sicpa.standard.sasscl.config.xstream;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.security.SecurityModel;
import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.client.common.xstream.IXStreamConfigurator;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.custom.Customizable;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.thoughtworks.xstream.XStream;

public class CommonModelXStreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream xstream) {
		configureCustomizable(xstream);
		configureSKU(xstream);
		configureCode(xstream);
		configureProductStatus(xstream);
		configureCodeType(xstream);
		configureProductionMode(xstream);
		configurePackagedProducts(xstream);
		configureProduct(xstream);
		configureStatisticsValues(xstream);
		configureStatisticsKey(xstream);
		configureSecurityModel(xstream);
		configureUser(xstream);
		configurePermission(xstream);
		configureNode(xstream);
	}

	public void configureCustomizable(XStream xstream) {
		xstream.aliasField("customProperties", Customizable.class, "customPropertiesMap");
	}

	public void configureSKU(XStream xstream) {
		xstream.useAttributeFor(SKU.class, "id");
		xstream.useAttributeFor(SKU.class, "description");
	}

	public void configureCode(XStream xstream) {
		xstream.useAttributeFor(Code.class, "stringCode");
		xstream.useAttributeFor(Code.class, "encoderId");
		xstream.useAttributeFor(Code.class, "sequence");
	}

	public void configureProductStatus(XStream xstream) {
		xstream.useAttributeFor(ProductStatus.class, "id");
		xstream.useAttributeFor(ProductStatus.class, "description");
		xstream.alias("ProductsStatus", ProductStatus.class);
	}

	public void configureCodeType(XStream xstream) {
		xstream.useAttributeFor(CodeType.class, "id");
		xstream.useAttributeFor(CodeType.class, "description");
	}

	public void configureProductionMode(XStream xstream) {
		xstream.useAttributeFor(ProductionMode.class, "id");
		xstream.useAttributeFor(ProductionMode.class, "description");
		xstream.useAttributeFor(ProductionMode.class, "withSicpaData");

	}

	public void configurePackagedProducts(XStream xstream) {
		xstream.alias("PackagedProducts", PackagedProducts.class);
		xstream.addImplicitCollection(PackagedProducts.class, "products");
		xstream.useAttributeFor(PackagedProducts.class, "UID");
		xstream.useAttributeFor(PackagedProducts.class, "productionBatchId");
		xstream.useAttributeFor(PackagedProducts.class, "subsystem");
		xstream.useAttributeFor(PackagedProducts.class, "printed");
	}

	public void configureProduct(XStream xstream) {
		xstream.alias("Product", Product.class);
		xstream.useAttributeFor(Product.class, "activationDate");
		xstream.useAttributeFor(Product.class, "qc");
	}

	public void configureStatisticsValues(XStream xstream) {
		xstream.alias("StatisticsValues", StatisticsValues.class);
	}

	public void configureStatisticsKey(XStream xstream) {
		xstream.alias("StatisticsKey", StatisticsKey.class);
		xstream.useAttributeFor(StatisticsKey.class, "description");
	}

	public void configureSecurityModel(XStream xstream) {
		xstream.alias("SecurityModel", SecurityModel.class);
		xstream.useAttributeFor(SecurityModel.class, "loginDefaultUser");
		xstream.addImplicitCollection(SecurityModel.class, "users");
	}

	public void configureUser(XStream xstream) {
		xstream.alias("User", User.class);
		xstream.useAttributeFor(User.class, "login");
		xstream.useAttributeFor(User.class, "password");
		xstream.addImplicitCollection(User.class, "permissions");
	}

	public void configurePermission(XStream xstream) {
		xstream.alias("Permission", Permission.class);
		xstream.useAttributeFor(Permission.class, "name");
	}

	public void configureNode(XStream xstream) {
		xstream.alias("ProductionModeNode", ProductionModeNode.class);
		xstream.alias("SKUNode", SKUNode.class);

		xstream.useAttributeFor(AbstractProductionParametersNode.class, "text");
		xstream.useAttributeFor(AbstractProductionParametersNode.class, "fileImage");
	}
}
