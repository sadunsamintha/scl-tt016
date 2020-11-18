import com.sicpa.ttth.scl.TTTHBootstrap
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.DailyBatchRequestRepository
import com.sicpa.standard.sasscl.model.BatchJobHistory

beans {
    importBeans('spring/custo/ttth/plc/ttth.plc-import.groovy')

    def serverBehavior = props['remoteServer.behavior'].trim().toUpperCase()

    addAlias('bootstrapAlias', 'bootstrap')
    bootstrap(TTTHBootstrap) { b ->
        b.parent = ref('bootstrapAlias')
        dailyBatchRequestRepository = ref('dailyBatchRequestRepository')
        remoteServer = ref('remoteServer')
        getCodedCountInterval = props['getCodedCountInterval']
    }

    batchJobStatistics(DailyBatchJobStatistics) {}

    batchJobHistory(BatchJobHistory) {}

    dailyBatchRequestRepository(DailyBatchRequestRepository) {
        skuListProvider = ref ('skuListProvider')
        batchJobStatistics = ref ('batchJobStatistics')
        batchJobHistory = ref ('batchJobHistory')
    }

    importBeans('spring/custo/ttth/ttth-monitoring.groovy')

    importBeans('spring/custo/ttth/ttth-production.groovy')

    if (serverBehavior == "STANDARD") {
        importBeans('spring/server/server-core5.groovy')
        importBeans('spring/custo/ttth/ttth-server.groovy')
    }
    if (serverBehavior == "SIMULATOR") {
        importBeans('spring/server/server-simulator.groovy')
        importBeans('spring/custo/ttth/ttth-server-simulator.groovy')
    }

    importBeans('spring/custo/ttth/ttth-view.groovy')
    importBeans('spring/custo/ttth/ttth-printer-common.groovy')

    importBeans('spring/custo/ttth/ttth-storage-import.groovy')
}