import com.sicpa.tt018.scl.TT018Bootstrap
import com.sicpa.tt018.scl.view.AlbaniaSelectProductionParametersHandPickingView
import com.sicpa.tt018.scl.business.activation.impl.AlbaniaMaintenanceActivationBehavior
import com.sicpa.tt018.scl.business.activation.impl.AlbaniaExportActivationBehavior
import com.sicpa.tt018.scl.model.productionParameters.AlbaniaSelectionModelFactory
import com.sicpa.tt018.scl.business.activation.impl.AlbaniaSCLActivationBehaviour
import com.sicpa.tt018.scl.camera.simulator.AlbaniaCameraSimulatorController
import com.sicpa.tt018.scl.devices.plc.impl.AlbaniaPlcLoader
beans{

	importBeans('spring/custo/tt018/aopLifeChecker.xml')
	importBeans('spring/custo/tt018/tt018ApplicationContext.xml')
	importBeans('spring/custo/tt018/tt018RemoteServer.xml')


	def cameraBehavior=props['camera.behavior'].toUpperCase()
	if(cameraBehavior == "SIMULATOR") {
		addAlias('cameraSimulatorControllerAlias','cameraSimulatorController')
		cameraSimulatorController(AlbaniaCameraSimulatorController){b->
			b.parent=ref('cameraSimulatorControllerAlias')
		}
	}

	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TT018Bootstrap){b->
		b.parent=ref('bootstrapAlias')
	}

	addAlias('plcValuesLoaderAlias','plcValuesLoader')
	plcValuesLoader(AlbaniaPlcLoader){b->
		b.parent=ref('plcValuesLoaderAlias')
		productionParameters=ref('productionParameters')
		varnameProductTypeSpecific=[
			'PARAM_LINE_CAMERA_DISTANCE',
			'PARAM_LINE_PRINTER_DISTANCE',
			'PARAM_LINE_EJECTION_EMISSION_DISTANCE',
			'PARAM_LINE_SENSOR_TYPE'
		]
		plcView=ref('plcVariablesPanel')
	}

	addAlias('selectProductionParametersHandPickingViewAlias','selectProductionParametersHandPickingView')
	selectProductionParametersHandPickingView(AlbaniaSelectProductionParametersHandPickingView){b->
		b.parent=ref('selectProductionParametersHandPickingViewAlias')
	}

	addAlias('selectionModelFactoryAlias','selectionModelFactory')
	selectionModelFactory(AlbaniaSelectionModelFactory){b->
		b.parent=ref('selectionModelFactoryAlias')
	}

	addAlias('maintenanceActivationBehaviorAlias','maintenanceActivationBehavior')
	maintenanceActivationBehavior(AlbaniaMaintenanceActivationBehavior){b->
		b.parent=ref('maintenanceActivationBehaviorAlias')
	}

	addAlias('exportActivationBehaviorAlias','exportActivationBehavior')
	exportActivationBehavior(AlbaniaExportActivationBehavior){b->
		b.parent=ref('exportActivationBehaviorAlias')
	}

	addAlias('standardActivationBehaviorAlias','standardActivationBehavior')
	standardActivationBehavior(AlbaniaSCLActivationBehaviour){b->
		b.parent=ref('standardActivationBehaviorAlias')
	}
}