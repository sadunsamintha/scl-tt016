beans{

	def custoId=props['custo.id'].toUpperCase()
	switch(custoId) {
		case 'SASSCL-CORE':
			importBeans('spring/custo/core/core.groovy')
			break
		case 'NONE':
			break
		default:
			throw new RuntimeException('unkown custo id:'+custoId)
	}
}