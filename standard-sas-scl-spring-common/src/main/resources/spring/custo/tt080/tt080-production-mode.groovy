import com.sicpa.tt080.sas.business.activation.impl.*
import com.sicpa.standard.sasscl.business.statistics.mapper.*
import com.sicpa.tt080.sasscl.business.statistics.mapper.*
import com.sicpa.tt080.sasscl.devices.remote.mapping.*
import com.sicpa.standard.sasscl.devices.remote.impl.*
import com.sicpa.tt080.sasscl.devices.remote.impl.*
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior
import com.sicpa.tt080.sas.business.activation.impl.activationBehavior.validator.*

beans{
    //Override cores beans
    //RemoteServerStatus Mapping
    coreRemoteServerProductStatusMapping(DefaultRemoteServerProductStatusMapping)
    remoteServerProductStatusMapping(TT080RemoteServerProductStatusMapping, ref('coreRemoteServerProductStatusMapping'))

    //ProductionMode Mapping
    productionModeMapping(TT080ProductionModeMapping)

    //ProductStatus Statistics Mapping
    coreProductStatusToStatisticsKeyMapper(DefaultProductStatusToStatisticsKeyMapper)
    productStatusToStatisticsKeyMapper(TT080ProductStatusToStatisticsKeyMapper, ref('coreProductStatusToStatisticsKeyMapper'))

    //FreeZone Custom Product Validator
    freeZoneProductionValidator(FreeZoneTypeValidator){
        productionParameters=ref('productionParameters')
    }

    //SAS "Zona Franca" ProductionBehaviour
    freezoneActivationBehavior(StandardActivationBehavior){
        productionParameters=ref('productionParameters')
        authenticatorProvider=ref('authenticatorProvider')
        productValidator=ref('freeZoneProductionValidator')
        invalidCodeHandler=ref('activationInvalidCodeHandler')
        productFinalizer=ref('productFinalizer')
        authenticatorModeProvider=ref('authenticatorModeProvider')
        productionConfigProvider=ref('productionConfigProvider')
    }
}