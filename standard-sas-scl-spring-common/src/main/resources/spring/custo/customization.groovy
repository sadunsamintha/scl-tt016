beans{

	def custoId=props['custo.id'].toUpperCase()
	switch(custoId) {
		case 'SASSCL-CORE':
			importBeans('spring/custo/core/core.groovy')
			break
		case 'TT018':
			importBeans('spring/custo/tt018/tt018.groovy')
			break
		case 'TT016':
			importBeans('spring/custo/tt016/tt016.groovy')
			break
		case 'TT053':
			importBeans('spring/custo/tt053/tt053.groovy')
			break
		case 'NONE':
			break
		default:
			throw new RuntimeException('unkown custo id:'+custoId)
	}
}