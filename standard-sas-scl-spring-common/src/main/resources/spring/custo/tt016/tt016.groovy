import com.sicpa.tt016.scl.TT016Bootstrap
import com.sicpa.tt016.refeed.TT016RefeedAvailabilityProvider

beans{

	def serverBehavior=props['remoteServer.behavior'].toUpperCase()

	if(serverBehavior == "STANDARD") {
		importBeans('spring/custo/tt016/tt016-server.groovy')
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT016Bootstrap){b->
		b.parent=ref('bootstrapAlias')
		mainPanelGetter=ref('mainPanelGetter')
		stopReasonViewController=ref('stopReasonViewController')
		codeTypeId=props['codeTypeId']
		unknownSkuProvider=ref('unknownSkuProvider')
		tt016RemoteServices=ref('remoteServices')
		refeedAvailabilityProvider=ref('refeedAvailabilityProvider')
	}

	importBeans('spring/custo/tt016/tt016-view.xml')
	importBeans('spring/offlineCounting.xml')

	addAlias('bisCredentialProvider','remoteServer')

	importBeans('spring/custo/tt016/tt016-camera.groovy')


	refeedAvailabilityProvider(TT016RefeedAvailabilityProvider){
		isRefeedAvailableInRemoteServer=props['refeedAvailable']
		isHeuftSystem=props['heuftSystem']
	}
}