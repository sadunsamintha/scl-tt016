package com.sicpa.standard.sasscl.provider.impl;

import java.util.HashSet;
import java.util.Set;

import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;

public class SkuListProvider extends AbstractProvider<ProductionParameterRootNode> {

	public SkuListProvider() {
		super("skuList");
	}

	public Set<SKU> getAvailableSKUs() {
		Set<SKU> res = new HashSet<>();
		if (get() != null) {
			populateSkusList(res, get());
		}
		return res;
	}

	private void populateSkusList(Set<SKU> skus, IProductionParametersNode node) {
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
		Set<CodeType> res = new HashSet<>();
		if (get() != null) {
			populateCodeTypesList(res, get());
		}
		return res;
	}

	private void populateCodeTypesList(Set<CodeType> codeTypes, IProductionParametersNode node) {
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
