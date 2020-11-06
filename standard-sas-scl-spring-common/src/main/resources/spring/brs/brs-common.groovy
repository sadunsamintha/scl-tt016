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

	brsWrongBarcodeThreshold(BrsWrongBarcodeThreshold,props['alert.wrongBarcode.enable'].trim(),props['alert.wrongBarcode.error.threshold'].trim(),ref('brsTimeWindow'))

	brsTimeWindow(BrsTimeWindow,props['alert.wrongBarcode.window.time'].trim())

	brsWrongBarcodeThresholdAddTask(InjectByMethodBean){
		target=ref('alert')
		methodName='addTask'
		params=ref('brsTimeWindow')
	}

	alertBrsProductWarningCount(ProductWarningCountAlertTask){
		unreadBarcodesThreshold=props['alert.unreadbarcodes.warning.threshold'].trim()
		isUnreadBarcodesEnable=props['alert.unreadbarcodes.warning.enable'].trim()
		productionConfigProvider=ref('productionConfigProvider')
		compliantProductResolver=ref('compliantProduct')
	}

	alertBrsProductErrorCount(ProductErrorCountAlertTask){
		unreadBarcodesThreshold=props['alert.unreadbarcodes.error.threshold'].trim()
		isUnreadBarcodesEnable=props['alert.unreadbarcodes.error.enable'].trim()
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