import com.sicpa.standard.sasscl.business.sku.ProductionChangeDetector
import com.sicpa.standard.sasscl.business.sku.selector.SkuSelector
import com.sicpa.standard.sasscl.business.sku.selector.buffer.SkuRecognizedBuffer

beans{

	skuRecognizedBuffer(SkuRecognizedBuffer){ bufferSize=props['sku.recognition.input.buffer'] }
	productionChangeDetector(ProductionChangeDetector){ delayMinute=props['sku.recognition.production.change.timer.minute'] }

	skuSelector(SkuSelector){
		productionParameters=ref('productionParameters')
		productionChangeDetector=ref('productionChangeDetector')
		skuBuffer=ref('skuRecognizedBuffer')
	}
}