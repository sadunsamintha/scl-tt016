package com.sicpa.standard.sasscl.provider.impl;

import java.util.HashSet;
import java.util.Set;

import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;

public class SkuListProvider extends AbstractProvider<ProductionParameterRootNode> {

	public SkuListProvider() {
		super("skuList");
	}

	public Set<SKU> getAvailableSKUs() {
		Set<SKU> res = new HashSet<SKU>();
		if (get() != null) {
			populateSkusList(res, get());
		}
		return res;
	}

	protected void populateSkusList(Set<SKU> skus, IProductionParametersNode node) {
		if (node.getChildren() != null) {
			for (IProductionParametersNode child : node.getChildren()) {
				if (child instanceof SKUNode) {
					skus.add((SKU) child.getValue());
				} else {
					if (child != null) {
						populateSkusList(skus, child);
					}
				}
			}
		}
	}

	public Set<CodeType> getAvailableCodeTypes() {
		Set<CodeType> res = new HashSet<CodeType>();
		if (get() != null) {
			populateCodeTypesList(res, get());
		}
		return res;
	}

	public void populateCodeTypesList(Set<CodeType> codeTypes, final IProductionParametersNode node) {
		if (node instanceof ProductionModeNode) {
			if (!((ProductionMode) node.getValue()).isWithSicpaData()) {
				// do not ask encoder for production mode that does not required printing
				return;
			}
		}

		if (node instanceof SKUNode) {
			codeTypes.add(((SKU) node.getValue()).getCodeType());
		} else {
			if (node.getChildren() != null) {
				for (IProductionParametersNode child : node.getChildren()) {
					populateCodeTypesList(codeTypes, child);
				}
			}
		}
	}
}
