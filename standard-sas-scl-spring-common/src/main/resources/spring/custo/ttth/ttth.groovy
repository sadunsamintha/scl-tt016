import com.sicpa.ttth.scl.TTTHBootstrap

beans {
    def serverBehavior = props['remoteServer.behavior'].toUpperCase()

    addAlias('bootstrapAlias', 'bootstrap')
    bootstrap(TTTHBootstrap) { b ->
        b.parent = ref('bootstrapAlias')
    }

    importBeans('spring/custo/ttth/ttth-production.groovy')

    if (serverBehavior == "STANDARD") {
        importBeans('spring/server/server-core5.groovy')
        importBeans('spring/custo/ttth/ttth-server.groovy')
    }
    if (serverBehavior == "SIMULATOR") {
        importBeans('spring/server/server-simulator.groovy')
    }

    importBeans('spring/custo/ttth/ttth-view.groovy')
    importBeans('spring/custo/ttth/ttth-printer-common.groovy')

    importBeans('spring/custo/ttth/ttth-storage-import.groovy')
}