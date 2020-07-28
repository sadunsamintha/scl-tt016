import com.sicpa.standard.sasscl.controller.productionconfig.mapping.ProductionConfigMapping
import com.sicpa.tt084.sasscl.controller.productionconfig.mapping.TT084ProductionConfigMapping

beans{
    //ProductionConfig mapping
    coreProductionConfigMapping(ProductionConfigMapping)
    productionConfigMapping(TT084ProductionConfigMapping, ref('coreProductionConfigMapping'))
}