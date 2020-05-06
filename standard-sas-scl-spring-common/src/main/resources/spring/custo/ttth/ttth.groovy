import com.sicpa.ttth.scl.TTTHBootstrap

beans {
	addAlias('bootstrapAlias','bootstrap')
	bootstrap(TTTHBootstrap){b->
		b.parent=ref('bootstrapAlias')
	}

	importBeans('spring/custo/ttth/ttth-printer-common.groovy')
}