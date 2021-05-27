package com.sicpa.tt085.productionParameterSelection.selectionmodel;

import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel.DefaultSelectionModel;
import com.sicpa.tt085.model.TT085ProductionMode;
import com.sicpa.tt085.security.TT085SasSclPermission;

public class TT085DefaultSelectionModel extends DefaultSelectionModel {

  public TT085DefaultSelectionModel(IProductionParametersNode root) {
    super(root);
    getPermissions().put(TT085ProductionMode.REFEED_EXPORT_CODING, TT085SasSclPermission.PRODUCTION_MODE_REFEED_EXPORT_CODING);
    getPermissions().put(TT085ProductionMode.REFEED_EXPORT_CODING_CORRECTION, TT085SasSclPermission.PRODUCTION_MODE_REFEED_EXPORT_CODING_CORRECTION);
  }
}