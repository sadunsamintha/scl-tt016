import com.sicpa.standard.sasscl.devices.bis.BisUserSender


beans{
	
	importBeans('spring/alert/alertUnknownSkuTask.groovy')

	bisUserSender(BisUserSender){b->
		b.initMethod='init'
		bisProvider=ref('bisProvider')
		credentialProvider=ref('bisCredentialProvider')
		sendUserCredentialPeriodMinutes=props['bis.credential.sending.period.min']
	}
}