import com.sicpa.standard.sasscl.*
import com.sicpa.standard.sasscl.sicpadata.generator.validator.EncoderSequenceValidator;
beans{

	bootstrap(Bootstrap){
		server=ref('remoteServer')
		startupDevicesGroup=ref('startupDevicesGroup')
		storage=ref('storage')
		productionParameters=ref('productionParameters')
		plcLoader=ref('plcValuesLoader')
		skuListProvider=ref('skuListProvider')
		authenticatorProvider=ref('authenticatorProvider')
		remoteServerSheduledJobs=ref('remoteServerSheduledJobs')
		subsystemIdProvider=ref('subsystemIdProvider')
		statistics=ref('statistics')
		cryptoProviderManager=ref('cryptoProviderManager')
		linePlcVarGroup=ref('linePlcVarGroup')
		cabPlcVarGroups=ref('cabPlcVarGroups')
		jmxBean=ref('statsMBean')
		encoderSequenceValidator=ref('encoderSequenceValidator')
		skuSelectionBehavior=ref('skuSelectionBehavior')
	}

	encoderSequenceValidator(EncoderSequenceValidator){
		currentEncoderFolder=profilePath+'/internal/encoders/current'
		enabled=props['encoder.sequence.validator.enabled']
	}
}
