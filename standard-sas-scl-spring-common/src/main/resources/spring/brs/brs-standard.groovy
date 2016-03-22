import com.sicpa.standard.sasscl.devices.brs.BrsAdaptor
import com.sicpa.standard.client.common.utils.ConfigUtils


beans{

	brsAdaptor(BrsAdaptor,ref('stdBrsModel')){ b->
		b.scope='prototype'
		b.initMethod='createReaders'
		brsLifeCheckInterval=props['brs.lifecheck.interval']
		brsLifeCheckTimeout=props['brs.lifecheck.timeout']
		brsLifeCheckNumberOfRetries=props['brs.lifecheck.retries']
	}

	stdBrsModel(ConfigUtils,profilePath+'/config/brs/brsConfig.xml'){ b->
		b.factoryMethod='load'
	}
}