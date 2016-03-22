import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterSimulator
import com.sicpa.standard.sasscl.utils.ConfigUtilEx

beans{

	printerSimulatorAdaptor(PrinterAdaptorSimulator,ref('printerSimulatorController')){b->
		b.scope='prototype'
		mappingExtendedCodeBehavior= ref('mappingExtendedCodeBehavior')
	}
	addAlias('printerLeibinger','printerSimulatorAdaptor')
	addAlias('printerDomino','printerSimulatorAdaptor')

	printerSimulatorModel(ConfigUtilEx,profilePath+'/config/printer/simulator/printerSimulator_####.xml',ref('deviceModelNamePostfixProperty')){ b->
		b.factoryMethod='load'
		b.scope='prototype'
	}

	printerSimulatorController(PrinterSimulator,ref('printerSimulatorModel')){b->
		b.scope='prototype'
		simulatorGui= ref('simulatorGui')
	}
}