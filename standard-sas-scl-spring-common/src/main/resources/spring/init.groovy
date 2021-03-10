import com.sicpa.standard.client.common.config.history.BackupService
import com.sicpa.standard.client.common.eventbus.impl.DeadEventListener
import com.sicpa.standard.client.common.eventbus.service.EventBusService
import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.client.common.xstream.DefaultXstreamConfigurator
import com.sicpa.standard.sasscl.common.log.OperatorLogger
import com.sicpa.standard.sasscl.common.log.OperatorLoggerBehavior
import com.sicpa.standard.sasscl.config.xstream.CommonModelXStreamConfigurator
import com.sicpa.standard.sasscl.controller.productionconfig.xstream.ProductionConfigXstreamConfigurator
import com.sicpa.standard.sasscl.devices.brs.model.BrsXstreamConfigurator
import com.sicpa.standard.sasscl.devices.camera.CameraXStreamConfigurator
import com.sicpa.standard.sasscl.devices.camera.d900.D900CameraXStreamConfigurator
import com.sicpa.standard.sasscl.devices.printer.PrinterXStreamConfigurator
import com.sicpa.standard.sasscl.eventbus.EventBusWithUncaughtExceptionHandling
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyDefinition
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory

beans{

	registerSingleton('allProperties',props)
	initCustomizableModel()
	initEventBus()
	initXstream()
	initBackup(profilePath,Integer.parseInt(props['backup.max'].trim()))
	initOperatorLog()
}



def static initOperatorLog() {
	OperatorLoggerBehavior log=new OperatorLoggerBehavior();
	OperatorLogger.setLoggerBehavior(log);
}

def static initEventBus() {
	def eventBus=new EventBusWithUncaughtExceptionHandling()
	def deadEventListener = new DeadEventListener()
	eventBus.register(deadEventListener)
	EventBusService.set(eventBus)
}

def static initXstream() {
	def delegates=[
		new CommonModelXStreamConfigurator(),
		new CameraXStreamConfigurator(),
		new D900CameraXStreamConfigurator(),
		new ProductionConfigXstreamConfigurator(),
		new PrinterXStreamConfigurator(),
		new BrsXstreamConfigurator()
	]

	def xstreamConfigurator = new DefaultXstreamConfigurator()
	xstreamConfigurator.setDelegates(delegates)

	xstreamConfigurator.configure(ConfigUtils.getXStream())
}

def static initBackup(String profilePath,int maxBackup) {
	def backupService= new BackupService()
	backupService.setProfilePath(profilePath)
	backupService.setBackupFolder('backup-config')
	backupService.setConfigFolder('config')
	backupService.setBackupMax(maxBackup)
	backupService.doBackup();
}

def static initCustomizableModel() {
	def customizablePropertyDefinition = new CustomizablePropertyDefinition()
	CustomizablePropertyFactory.setCustomizablePropertyDefinition(customizablePropertyDefinition)
}


