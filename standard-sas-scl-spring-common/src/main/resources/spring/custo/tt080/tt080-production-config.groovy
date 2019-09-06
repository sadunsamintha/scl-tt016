import com.sicpa.standard.sasscl.controller.productionconfig.mapping.ProductionConfigMapping
import com.sicpa.tt080.sasscl.controller.productionconfig.mapping.TT080ProductionConfigMapping

beans{
    //ProductionConfig mapping
    coreProductionConfigMapping(ProductionConfigMapping)
    productionConfigMapping(TT080ProductionConfigMapping, ref('coreProductionConfigMapping'))
}