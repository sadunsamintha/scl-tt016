import com.sicpa.standard.client.common.ioc.InjectByMethodBean
import com.sicpa.standard.sasscl.business.sku.alert.UnkownSkuIdentifiedScheduledAlert

beans{

	unkownSkuScheduledAlert(UnkownSkuIdentifiedScheduledAlert){
		enabled=props['alert.sku.recognition.enabled']
		maxUnreadCount=props['alert.sku.recognition.maxUnreadCount']
		sampleSize=props['alert.sku.recognition.sampleSize']
		delaySec=props['alert.sku.recognition.delayInSec']
	}

	alertUnkownSkuScheduledAlertAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('unkownSkuScheduledAlert')
	}
}

