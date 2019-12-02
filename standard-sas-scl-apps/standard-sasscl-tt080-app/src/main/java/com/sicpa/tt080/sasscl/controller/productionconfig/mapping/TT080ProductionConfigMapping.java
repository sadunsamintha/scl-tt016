package com.sicpa.tt080.sasscl.controller.productionconfig.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt080.sasscl.model.TT080ProductionMode;

public class TT080ProductionConfigMapping implements IProductionConfigMapping {

  private final IProductionConfigMapping baseMapping;
  private final Map<ProductionMode, String> customMappings = new HashMap<>();

  /**
   * Create a Custom Production mapping using another {@link IProductionConfigMapping} as base
   * @param baseMapping Base Mapping
   * @throws NullPointerException if {@param baseMapping} is null
   */
  public TT080ProductionConfigMapping(final IProductionConfigMapping baseMapping) {
    this.baseMapping = Optional.of(baseMapping).get();
    this.addCustomMappings();
  }

  private void addCustomMappings(){
    this.put(TT080ProductionMode.FREEZONE, "freezone");
  }

  @Override
  public String getProductionConfigId(final ProductionMode mode) {
    return Optional
        .ofNullable(customMappings.get(mode))
        .orElseGet(()->this.baseMapping.getProductionConfigId(mode));
  }

  /**
   * Map a {@link ProductionMode} to a configuration identifier
   * @param mode ProductionMode to be associated with
   * @param productionConfigId Identifier of this Mode
   *
   * @throws NullPointerException if mode or productionConfigId is null
   * @throws IllegalArgumentException if productionConfigId is empty
   */
  @Override
  public void put(final ProductionMode mode, final String productionConfigId) {
    final ProductionMode nonNullMode = Optional.of(mode).get();
    final String nonNullConfigId = Optional.of(productionConfigId).get();

    if(nonNullConfigId.trim().isEmpty()) throw new IllegalArgumentException("Configuration Id can't be empty");

    this.customMappings.put(nonNullMode,productionConfigId);
  }

  Map<ProductionMode, String> getCustomMappings(){
    return Collections.unmodifiableMap(this.customMappings);
  }
}
