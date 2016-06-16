import com.sicpa.standard.sasscl.business.sku.selector.SkuSelector
import com.sicpa.standard.sasscl.business.sku.selector.buffer.SkuRecognizedBuffer

beans{

	skuRecognizedBuffer(SkuRecognizedBuffer){ bufferSize=props['sku.recognition.input.buffer'] }

	skuSelector(SkuSelector){
		productionChangeDetector=ref('productionChangeDetector')
		skuSelectionBehavior=ref('skuSelectionBehavior')
		skuBuffer=ref('skuRecognizedBuffer')
		unknownSkuProvider=ref('unknownSkuProvider')
	}
}