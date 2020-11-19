import com.sicpa.tt016.production.TT016Production

beans{
    production(TT016Production){
        storage=ref('storage')
        remoteServer=ref('remoteServer')
        productsPackager=ref('productsPackager')
        subsystemIdProvider=ref('subsystemIdProvider')
        productionSendBatchSize=props['productionSendBatchSize'].trim()
        productionDataSerializationErrorThreshold=props['productionDataSerializationErrorThreshold'].trim()
    }
}



