beans {

	def plcBehavior=props['plc.behavior'].trim().toUpperCase()

	importBeans('spring/custo/ttth/plc/ttth.plc-common.groovy')
	importBeans('spring/alert/alertNoCapsTask.groovy')

	if(plcBehavior == "SIMULATOR") {
		importBeans('spring/plc/plc-simulator.groovy')
	}
	else if(plcBehavior == "STANDARD") {
		importBeans('spring/plc/plc-standard.groovy')
	}
}

