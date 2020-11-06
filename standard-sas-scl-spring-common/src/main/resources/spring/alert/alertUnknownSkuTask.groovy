import com.sicpa.standard.client.common.ioc.InjectByMethodBean
import com.sicpa.standard.sasscl.business.sku.alert.UnkownSkuRecognizedScheduledAlert

beans{

	unkownSkuScheduledAlert(UnkownSkuRecognizedScheduledAlert){
		enabled=props['alert.sku.recognition.unknown.enabled'].trim()
		threshold=props['alert.sku.recognition.unknown.maxUnknownCount'].trim()
		sampleSize=props['alert.sku.recognition.unknown.sampleSize'].trim()
		productionChangeDetector=ref('productionChangeDetector')
	}

	alertUnkownSkuScheduledAlertAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('unkownSkuScheduledAlert')
	}
}

