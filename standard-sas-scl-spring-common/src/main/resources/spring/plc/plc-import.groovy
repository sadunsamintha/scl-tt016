beans {
	def plcBehavior=props['plc.behavior'].toUpperCase()

	importBeans('spring/plc/plc-common.groovy')
	if(plcBehavior == "SIMULATOR") {		importBeans('spring/plc/plc-simulator.groovy')
	}	else if(plcBehavior == "STANDARD") {		importBeans('spring/plc/plc-standard.groovy')
	}
}

