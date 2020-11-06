beans{

	def skuRecognitionBehavior=props['sku.recognition.behavior'].trim().toUpperCase()
	if(skuRecognitionBehavior != "NONE") {
		importBeans('spring/skurecognition/skuRecognition-common.groovy')
		if(skuRecognitionBehavior == "CHECK") {
			importBeans('spring/skurecognition/skuCheck.groovy')
		}else if(skuRecognitionBehavior == "SELECTOR"){
			importBeans('spring/skurecognition/skuSelector.groovy')
		}else{
			throw new IllegalArgumentException("unknown skuRecognitionBehavior:"+skuRecognitionBehavior);
		}
	}
}