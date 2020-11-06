import com.sicpa.standard.sasscl.devices.brs.BrsAdaptor
import com.sicpa.standard.client.common.utils.ConfigUtils


beans{

	brsAdaptor(BrsAdaptor,ref('stdBrsModel')){ b->
		b.scope='prototype'
		b.initMethod='createReaders'
		brsLifeCheckInterval=props['brs.lifecheck.interval'].trim()
		brsLifeCheckTimeout=props['brs.lifecheck.timeout'].trim()
		brsLifeCheckNumberOfRetries=props['brs.lifecheck.retries'].trim()
	}

	stdBrsModel(ConfigUtils,profilePath+'/config/brs/brsConfig.xml'){ b->
		b.factoryMethod='load'
	}
}