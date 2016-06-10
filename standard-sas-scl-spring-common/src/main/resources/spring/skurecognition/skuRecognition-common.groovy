import com.sicpa.standard.sasscl.business.sku.ProductionChangeDetector

beans{

	productionChangeDetector(ProductionChangeDetector){ delayMinute=props['sku.recognition.production.change.timer.minute'] }
}