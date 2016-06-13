import com.sicpa.standard.client.common.ioc.InjectByMethodBean
import com.sicpa.standard.sasscl.business.sku.alert.UnkownSkuRecognizedScheduledAlert

beans{

	unkownSkuScheduledAlert(UnkownSkuRecognizedScheduledAlert){
		enabled=props['alert.sku.recognition.enabled']
		maxUnreadCount=props['alert.sku.recognition.maxUnreadCount']
		sampleSize=props['alert.sku.recognition.sampleSize']
		delaySec=props['alert.sku.recognition.delayInSec']
		productionChangeDetector=ref('productionChangeDetector')
	}

	alertUnkownSkuScheduledAlertAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('unkownSkuScheduledAlert')
	}
}

