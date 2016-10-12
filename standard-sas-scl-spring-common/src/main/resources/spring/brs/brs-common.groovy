import com.sicpa.standard.sasscl.devices.brs.statistics.BrsStatisticsCollector
import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProductSkuResolver
import com.sicpa.standard.sasscl.devices.brs.barcodeCheck.*
import com.sicpa.standard.client.common.ioc.InjectByMethodBean
import com.sicpa.standard.sasscl.devices.brs.alert.ProductWarningCountAlertTask
import com.sicpa.standard.sasscl.devices.brs.alert.ProductErrorCountAlertTask



beans{

	brsStatisticsCollector(BrsStatisticsCollector)

	compliantProduct(CompliantProductSkuResolver)

	brsBarcodeCheck(BrsBarcodeCheck){ compliantProductResolver=ref('compliantProduct') }

	brsWrongBarcodeThreshold(BrsWrongBarcodeThreshold,props['alert.wrongBarcode.enable'],props['alert.wrongBarcode.error.threshold'],ref('brsTimeWindow'))

	brsTimeWindow(BrsTimeWindow,props['alert.wrongBarcode.window.time'])

	brsWrongBarcodeThresholdAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('brsTimeWindow')
	}

	alertBrsProductWarningCount(ProductWarningCountAlertTask){
		unreadBarcodesThreshold=props['alert.unreadbarcodes.warning.threshold']
		isUnreadBarcodesEnable=props['alert.unreadbarcodes.warning.enable']
		productionConfigProvider=ref('productionConfigProvider')
		compliantProductResolver=ref('compliantProduct')
	}

	alertBrsProductErrorCount(ProductErrorCountAlertTask){
		unreadBarcodesThreshold=props['alert.unreadbarcodes.error.threshold']
		isUnreadBarcodesEnable=props['alert.unreadbarcodes.error.enable']
		productionConfigProvider=ref('productionConfigProvider')
		compliantProductResolver=ref('compliantProduct')
	}


	alertBrsProducWarningCountAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('alertBrsProductWarningCount')
	}

	alertBrsProducErrorCountAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('alertBrsProductErrorCount')
	}
}