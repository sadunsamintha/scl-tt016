import com.sicpa.standard.sasscl.TestBootstrap
import com.sicpa.standard.sasscl.AbstractFunctionnalTest.ExecutorExitNoJVMExit;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.utils.mapping.TestProductionConfigMapping
import com.sicpa.standard.sasscl.utils.printer.PrinterSimulatorThatProvidesCodes;

beans {

	bootstrap(TestBootstrap){
	}

	productionConfigMapping(TestProductionConfigMapping)

	addAlias('executorExitAlias','executorExit')
	executorExit(ExecutorExitNoJVMExit){b->
		b.parent=ref('executorExitAlias')
	}

	addAlias('printerSimulatorControllerAlias','printerSimulatorController')
	printerSimulatorController(PrinterSimulatorThatProvidesCodes){b->
		b.parent=ref('executorExitAlias')
		b.scope='prototype'
	}

	//		remoteServerSimulator(RemoteServerSimulator)

}