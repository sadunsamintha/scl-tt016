package com.sicpa.tt080.sasscl.devices.remote.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.std.common.api.activation.dto.productionData.ProcessedProductsStatusDto;
import com.sicpa.tt080.sasscl.model.TT080ProductStatus;

public class TT080RemoteServerProductStatusMapping implements IRemoteServerProductStatusMapping {

  public static int AUTHENTICATED_DECLARED = 101;

  private final IRemoteServerProductStatusMapping defaultMapping;
  final Map<ProductStatus, Integer> customMap = new HashMap<>();

  public TT080RemoteServerProductStatusMapping(final IRemoteServerProductStatusMapping defaultMapping) {
    this.defaultMapping = defaultMapping;
    addProjectCustomMappings();
  }

  private void addProjectCustomMappings() {
    this.add(TT080ProductStatus.DECLARED, AUTHENTICATED_DECLARED); //101 -> 101
  }

  /**
   * Return the remote server representation of a {@link ProductStatus}.
   *
   * This will search both, local mapping as defaultMapping, however local mapping will have greater priority
   * on the return. Case no status is found, on both mapping, <code>-1</code> will be returned.
   *
   * @param productStatus
   * @return
   */
  @Override
  public int getRemoteServerProdutcStatus(final ProductStatus productStatus) {
    return Optional.ofNullable(this.customMap.get(productStatus)).orElseGet(() ->
        defaultMapping.getRemoteServerProdutcStatus(productStatus)
    );
  }

  @Override
  public void add(final ProductStatus status, int idOnRemote) {
    final ProductStatus safeStatus = Optional.of(status).get();
    this.customMap.put(safeStatus, idOnRemote);
  }
}
