import com.sicpa.standard.sasscl.devices.bis.BisUserSender
import com.sicpa.standard.sasscl.devices.bis.SkuBisProvider
import com.sicpa.standard.sasscl.skureader.SkuFinder;


beans{

	bisParent(){ b->
		b.abstract=true
		blockProduction = props['bis.disconnected.production.block']
		skuFinder=ref('skuFinder')
		unknownSkuId=props['bis.unknownSkuId']
		displayAlertMessage=props['bis.displayAlertMessage']
		skuBisProvider=ref('skuBisProvider')
	}

	def skuRecognitionBehavior=props['sku.recognition.behavior'].toUpperCase()
	if(skuRecognitionBehavior != "NONE") {
		importBeans('spring/alert/alertUnknownSkuTask.groovy')
	}

	bisUserSender(BisUserSender){b->
		b.initMethod='init'
		bisProvider=ref('bisProvider')
		credentialProvider=ref('bisCredentialProvider')
		sendUserCredentialPeriodMinutes=props['bis.credential.sending.period.min']
	}

	skuBisProvider(SkuBisProvider){ skuListProvider=ref('skuListProvider') }

	skuFinder(SkuFinder){
		skuListProvider=ref('skuListProvider')
		productionParameters=ref('productionParameters')
	}
}