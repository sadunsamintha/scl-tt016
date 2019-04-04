import com.sicpa.standard.sasscl.business.production.impl.TT079Production

beans{

	production(TT079Production){
		storage=ref('storage')
		remoteServer=ref('remoteServer')
		productsPackager=ref('productsPackager')
		subsystemIdProvider=ref('subsystemIdProvider')
		productionSendBatchSize= props['productionSendBatchSize']
		productionDataSerializationErrorThreshold=props['productionDataSerializationErrorThreshold']
		pp=ref('productionParameters')
	}
	
}



