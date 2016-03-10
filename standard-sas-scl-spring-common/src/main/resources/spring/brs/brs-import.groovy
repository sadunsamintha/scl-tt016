beans{


	def brsBehavior=props['brs.behavior'].toUpperCase()

	if(brsBehavior != "NONE") {
		importBeans('spring/brs/brs-common.groovy')
		if(brsBehavior == "SIMULATOR") {
			importBeans('spring/brs/brs-simulator.groovy')
		}
		else if(brsBehavior == "STANDARD") {
			importBeans('spring/brs/brs-standard.groovy')
		}
	}
}