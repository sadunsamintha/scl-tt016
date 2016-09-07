import com.sicpa.standard.client.common.ioc.InjectByMethodBean
import com.sicpa.standard.sasscl.business.alert.task.model.NoCapsAlertTaskModel
import com.sicpa.standard.sasscl.devices.plc.alert.NoCapsAlertTask

beans{

	noCapsAlertModel(NoCapsAlertTaskModel) {
		enabled=props['alert.noCaps.enabled']
		delayInSec=props['alert.noCaps.delayInSec']
		threshold=props['alert.noCaps.threshold']
		sampleSize=props['alert.noCaps.sampleSize']
	}

	noCapsAlert(NoCapsAlertTask){
		plcProvider=ref('plcProvider')
		model=ref('noCapsAlertModel')
		productCounterVarName="#{plcVarMap['NTF_LINE_COUNTER_TRIGS']}"
		noCapsCounterVarName="#{plcVarMap['NTF_LINE_NO_CAP_TRIGS']}";
	}

	noCapsScheduledAlertAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('noCapsAlert')
	}
}

