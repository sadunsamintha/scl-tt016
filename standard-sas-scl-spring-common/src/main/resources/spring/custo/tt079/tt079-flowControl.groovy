import com.sicpa.standard.client.common.controller.predicate.start.GlobalStartProductionValidator
import com.sicpa.standard.sasscl.controller.flow.statemachine.executor.TT079ExecutorStarting
import com.sicpa.standard.sasscl.controller.flow.statemachine.executor.ExecutorStarted

beans{
	
	globalStartProductionValidator(GlobalStartProductionValidator){
	validators=[ref('maxRemoteServerDownTimeListener'),ref('parametersAvailableStartValidator')]
	}
	
	
	executorStarting(TT079ExecutorStarting){
		productionBatchProvider=ref('productionBatchProvider') 
		hardwareController=ref('hardwareController')
		startValidators=ref('globalStartProductionValidator')
		timeoutDelay = '#{${startProduction.timeoutInSec} * 1000}'
	}
	
	executorStarted(ExecutorStarted){
		alert=ref('alert')
	}

}