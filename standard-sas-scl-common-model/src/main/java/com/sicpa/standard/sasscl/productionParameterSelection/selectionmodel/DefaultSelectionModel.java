package com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel;

import static java.util.Collections.emptyList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class DefaultSelectionModel extends AbstractSelectionFlowModel {
	public static final int PRODUCTION_MODE_SCREEN_INDEX = 0;
	private static final int NO_SELECTION = 0;

	private Map<Object, Permission> mapPermissions;
	private IProductionParametersNode parameterTreeRootNode;

	public DefaultSelectionModel(IProductionParametersNode root) {
		this.parameterTreeRootNode = root;
		this.mapPermissions = new HashMap<>();
		populatePermissions();
	}

	private void populatePermissions() {
		mapPermissions.put(ProductionMode.STANDARD, SasSclPermission.PRODUCTION_MODE_STANDARD);
		mapPermissions.put(ProductionMode.MAINTENANCE, SasSclPermission.PRODUCTION_MODE_MAINTENANCE);
		mapPermissions.put(ProductionMode.EXPORT, SasSclPermission.PRODUCTION_MODE_EXPORT);
		mapPermissions.put(ProductionMode.COUNTING, SasSclPermission.PRODUCTION_MODE_EXPORT);
		mapPermissions.put(ProductionMode.REFEED_NORMAL, SasSclPermission.PRODUCTION_MODE_REFEED_NORMAL);
		mapPermissions.put(ProductionMode.REFEED_CORRECTION, SasSclPermission.PRODUCTION_MODE_REFEED_CORRECTION);
	}

	private boolean shouldAddParameterToList(Object param) {
		if (hasAllPermission()) {
			return true;
		} else {
			Permission p = mapPermissions.get(param);
			if (p == null) {
				return true;
			} else {
				return SecurityService.hasPermission(p);
			}
		}
	}

	private boolean hasAllPermission() {
		return SecurityService.hasPermission(SasSclPermission.PRODUCTION_MODE_ALL);
	}

	@Override
	public String getTitle(SelectableItem[] selection) {
		if (selection == null || selection.length == 0) {
			return Messages.get("view.productionMode");
		} else {
			return Messages.get("view.sku");
		}
	}

	@Override
	public boolean isLeaf(SelectableItem[] selections) {
		if (selections.length == NO_SELECTION) {
			return false;
		}
		return hasNoChild(selections);
	}

	private boolean hasNoChild(SelectableItem[] selections) {
		IProductionParametersNode node = ((IProductionParametersNode) selections[selections.length - 1]);
		return node.getChildren() == null || node.getChildren().isEmpty();
	}

	@Override
	protected void populate(SelectableItem[] selections, List<SelectableItem> nextOptions) {
		if (selections.length == PRODUCTION_MODE_SCREEN_INDEX) {
			addProductionsMode(nextOptions);
		} else {
			List<? extends IProductionParametersNode> items = getNextItemsToDisplay(selections);
			items.stream().filter(item -> shouldAddParameterToList(item.getValue()))
					.forEach(item -> nextOptions.add(item));
		}
	}

	private List<? extends IProductionParametersNode> getNextItemsToDisplay(SelectableItem[] selections) {
		List<? extends IProductionParametersNode> res = ((IProductionParametersNode) selections[selections.length - 1])
				.getChildren();
		if (res == null) {
			return emptyList();
		} else {
			return res;
		}
	}

	protected void addProductionsMode(List<SelectableItem> productionModeNodes) {
		if (parameterTreeRootNode != null && parameterTreeRootNode.getChildren() != null) {
			for (IProductionParametersNode item : parameterTreeRootNode.getChildren()) {
				if (shouldAddParameterToList(item.getValue())) {
					productionModeNodes.add(item);
				}
			}
		}
	}

	public Map<Object, Permission> getPermissions() {
		return mapPermissions;
	}
}