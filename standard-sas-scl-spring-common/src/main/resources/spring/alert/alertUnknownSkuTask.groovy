import com.sicpa.standard.client.common.ioc.InjectByMethodBean
import com.sicpa.standard.sasscl.business.sku.alert.UnkownSkuRecognizedScheduledAlert

beans{

	unkownSkuScheduledAlert(UnkownSkuRecognizedScheduledAlert){
		enabled=props['alert.sku.recognition.unknown.enabled']
		threshold=props['alert.sku.recognition.unknown.maxUnknownCount']
		sampleSize=props['alert.sku.recognition.unknown.sampleSize']
		productionChangeDetector=ref('productionChangeDetector')
	}

	alertUnkownSkuScheduledAlertAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('unkownSkuScheduledAlert')
	}
}

