beans{

	def bisBehavior=props['bis.behavior'].toUpperCase()
	if(bisBehavior != "NONE") {

		importBeans('spring/bis/bis-common.groovy')

		if(bisBehavior == "SIMULATOR") {
			importBeans('spring/bis/bis-simulator.groovy')
		}
		else if(bisBehavior == "STANDARD") {
			importBeans('spring/bis/bis-standard.groovy')
		}
	}
}