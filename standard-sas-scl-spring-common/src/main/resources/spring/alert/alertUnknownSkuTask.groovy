import com.sicpa.standard.client.common.ioc.InjectByMethodBean
import com.sicpa.standard.sasscl.business.sku.alert.UnkownSkuRecognizedScheduledAlert

beans{

	unkownSkuScheduledAlert(UnkownSkuRecognizedScheduledAlert){
		enabled=props['alert.sku.recognition.enabled']
		threshold=props['alert.sku.recognition.maxUnreadCount']
		sampleSize=props['alert.sku.recognition.sampleSize']
		productionChangeDetector=ref('productionChangeDetector')
	}

	alertUnkownSkuScheduledAlertAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('unkownSkuScheduledAlert')
	}
}

