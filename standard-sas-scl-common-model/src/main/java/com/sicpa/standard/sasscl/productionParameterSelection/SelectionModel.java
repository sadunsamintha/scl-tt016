package com.sicpa.standard.sasscl.productionParameterSelection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class SelectionModel extends AbstractSelectionFlowModel {

	protected Map<Object, Permission> mapPermissions;

	protected IProductionParametersNode root;

	public SelectionModel(final IProductionParametersNode root) {
		this.root = root;
		this.mapPermissions = new HashMap<Object, Permission>();
		populatePermissions();
	}

	protected void populatePermissions() {
		this.mapPermissions.put(ProductionMode.STANDARD, SasSclPermission.PRODUCTION_MODE_STANDARD);
		this.mapPermissions.put(ProductionMode.MAINTENANCE, SasSclPermission.PRODUCTION_MODE_MAINTENANCE);
		this.mapPermissions.put(ProductionMode.EXPORT, SasSclPermission.PRODUCTION_MODE_EXPORT);
		this.mapPermissions.put(ProductionMode.COUNTING, SasSclPermission.PRODUCTION_MODE_EXPORT);
		this.mapPermissions.put(ProductionMode.REFEED_NORMAL, SasSclPermission.PRODUCTION_MODE_REFEED_NORMAL);
		this.mapPermissions.put(ProductionMode.REFEED_CORRECTION, SasSclPermission.PRODUCTION_MODE_REFEED_CORRECTION);
	}

	protected boolean shouldAddParameterToList(final Object param) {
		if (SecurityService.hasPermission(SasSclPermission.PRODUCTION_MODE_ALL)) {
			return true;
		} else {
			Permission p = this.mapPermissions.get(param);
			if (p == null) {
				return true;
			} else {
				return SecurityService.hasPermission(p);
			}
		}
	}

	@Override
	public String getTitle(final SelectableItem[] selection) {
		if (selection == null || selection.length == 0) {
			return Messages.get("view.productionMode");
		} else {
			return Messages.get("view.sku");
		}
	}

	@Override
	public boolean isLeaf(final SelectableItem[] selections) {
		if (selections.length == 0) {
			return false;
		}
		IProductionParametersNode current = ((IProductionParametersNode) selections[selections.length - 1]);
		return current.getChildren() == null || current.getChildren().isEmpty();
	}

	@Override
	protected void populate(final SelectableItem[] selections, final List<SelectableItem> nextOptions) {
		if (selections.length == 0) {
			if (this.root != null && this.root.getChildren() != null) {
				for (IProductionParametersNode item : this.root.getChildren()) {
					if (shouldAddParameterToList(item.getValue())) {
						nextOptions.add(item);
					}
				}
			}
		} else {

			List<? extends IProductionParametersNode> items = ((IProductionParametersNode) selections[selections.length - 1])
					.getChildren();
			if (items != null) {
				for (IProductionParametersNode item : items) {
					if (shouldAddParameterToList(item.getValue())) {
						nextOptions.add(item);
					}
				}
			}
		}
	}

	public Map<Object, Permission> getPermissions() {
		return mapPermissions;
	}
}