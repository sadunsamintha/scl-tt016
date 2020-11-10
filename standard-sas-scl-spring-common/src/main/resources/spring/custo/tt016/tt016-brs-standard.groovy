import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.tt016.devices.brs.TT016BrsAdaptor

beans{
	def brsBehavior=props['brs.behavior'].toUpperCase()

	if (brsBehavior == "STANDARD") {
		brsAdaptor(TT016BrsAdaptor,ref('stdBrsModel')){ b->
			b.scope='prototype'
			b.initMethod='createReaders'
			brsLifeCheckInterval=props['brs.lifecheck.interval']
			brsLifeCheckTimeout=props['brs.lifecheck.timeout']
			brsLifeCheckNumberOfRetries=props['brs.lifecheck.retries']
			blockProduction = props['brs.disconnected.production.block']
			view=ref('messagesView')
		}
	}
}