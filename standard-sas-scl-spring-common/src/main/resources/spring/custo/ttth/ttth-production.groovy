import com.sicpa.standard.sasscl.business.production.impl.TTTHProduction

beans{
    production(TTTHProduction){
        storage=ref('storage')
        remoteServer=ref('remoteServer')
        productsPackager=ref('productsPackager')
        subsystemIdProvider=ref('subsystemIdProvider')
        productionSendBatchSize= props['productionSendBatchSize'].trim()
        productionDataSerializationErrorThreshold=props['productionDataSerializationErrorThreshold'].trim()
        pp=ref('productionParameters')
    }
}



