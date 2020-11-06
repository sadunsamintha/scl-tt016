beans {

	def plcBehavior=props['plc.behavior'].trim().toUpperCase()

	importBeans('spring/custo/tt065/plc/tt065.plcVars.groovy')
	importBeans('spring/custo/tt065/plc/tt065.plc-common.groovy')
	importBeans('spring/alert/alertNoCapsTask.groovy')

	if(plcBehavior == "SIMULATOR") {
		importBeans('spring/plc/plc-simulator.groovy')
	}
	else if(plcBehavior == "STANDARD") {
		importBeans('spring/plc/plc-standard.groovy')
	}
}

