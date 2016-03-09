import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.client.common.xstream.DefaultXstreamConfigurator;
import com.sicpa.standard.sasscl.controller.productionconfig.xstream.ProductionConfigXstreamConfigurator;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerSimulatorXStreamConfigurator
import com.sicpa.standard.sasscl.config.xstream.CommonModelXStreamConfigurator
import com.sicpa.standard.sasscl.devices.camera.CameraXStreamConfigurator
import com.sicpa.standard.sasscl.devices.printer.PrinterXStreamConfigurator
import com.sicpa.standard.sasscl.devices.brs.model.BrsXstreamConfigurator


beans{
	def delegates=[
		new CommonModelXStreamConfigurator(),
		new RemoteServerSimulatorXStreamConfigurator(),
		new CameraXStreamConfigurator(),
		new ProductionConfigXstreamConfigurator(),
		new PrinterXStreamConfigurator(),
		new BrsXstreamConfigurator()
	]

	def xstreamConfigurator = new DefaultXstreamConfigurator()
	xstreamConfigurator.setDelegates(delegates)

	xstreamConfigurator.configure(ConfigUtils.getXStream())
}
