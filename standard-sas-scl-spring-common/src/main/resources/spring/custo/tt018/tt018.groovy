import com.sicpa.standard.sasscl.tt018.*
import com.sicpa.tt018.scl.view.AlbaniaSelectProductionParametersHandPickingView
import com.sicpa.tt018.scl.business.activation.impl.AlbaniaMaintenanceActivationBehavior
import com.sicpa.tt018.scl.business.activation.impl.AlbaniaExportActivationBehavior
import com.sicpa.tt018.scl.model.productionParameters.AlbaniaSelectionModelFactory
import com.sicpa.tt018.scl.business.activation.impl.AlbaniaSCLActivationBehaviour
import com.sicpa.tt018.scl.camera.simulator.AlbaniaCameraSimulatorController
beans{

	importBeans('spring/tt018/apoLifeChecker.xml')
	importBeans('spring/tt018/tt018ApplicationContext.xml')
	importBeans('spring/tt018/tt018.RemoteServer.xml')


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