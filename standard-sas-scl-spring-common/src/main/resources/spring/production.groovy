import com.sicpa.standard.sasscl.business.production.impl.Production;import com.sicpa.standard.sasscl.business.production.impl.ProductionWithUnknownSkuHandling;


beans{


	production(Production){
		storage=ref('storage')
		remoteServer=ref('remoteServer')
		productsPackager=ref('productsPackager')
		subsystemIdProvider=ref('subsystemIdProvider')
		productionSendBatchSize= props['productionSendBatchSize'].trim()
		productionDataSerializationErrorThreshold=props['productionDataSerializationErrorThreshold'].trim()
	}


	def productionBehavior=props['production.behavior'].trim().toUpperCase()
	if(productionBehavior=='WITH_UNKNOWN_BUFFER'){

		addAlias('productionAlias','production')
		production(ProductionWithUnknownSkuHandling){b->
			b.parent=ref('productionAlias')
			unknownSkuProvider=ref('unknownSkuProvider')
			unknownsBufferSize=props['production.with_unknown_buffer.buffer.size'].trim()
			stillUnknownCheckDelaySec=props['production.with_unknown_buffer.task.unknown.delay.sec'].trim()
		}
	}
}



