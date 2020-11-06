import javax.swing.ImageIcon;

import com.sicpa.standard.client.common.utils.ConfigUtils
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;

import static com.sicpa.standard.sasscl.model.ProductionMode.*
beans{

	int SKU_BY_CODE_TYPE=2

	def root=new ProductionParameterRootNode()

	def productionModes = [
		[mode:STANDARD, ct:[1, 2]],
		[mode:EXPORT, ct:[4, 5]],
		[mode:COUNTING, ct:[7, 8]],
		[mode:REFEED_NORMAL, ct:[1, 2]],
		[mode:REFEED_CORRECTION, ct:[1, 2]],
		[mode:EXPORT_CODING, ct:[10, 11]]
	]

	int skuId=0

	for (e in productionModes){
		def pmn=new ProductionModeNode(e['mode'])
		root.getChildren().add(pmn)
		for(ctid in e['ct']){
			for(int i=0;i<SKU_BY_CODE_TYPE;i++){
				def barcode='000'+skuId
				def description='ct:'+ctid+' - skuid:'+skuId
				def sku=new SKU(skuId,description,[barcode])
				def ct=new CodeType(ctid)
				sku.setCodeType(ct)
				sku.setImage(new ImageIcon(ImageUtils.createRandomStrippedImage(60, 30)));
				sku.setAppearanceCode(description);
				def skuNode=new SKUNode(sku)
				pmn.getChildren().add(skuNode)
				skuId++
			}
		}
	}


	simulatorRemoteModel(RemoteServerSimulatorModel){ b->
		numberOfCodesByEncoder=props['server.simulator.codes.by.encoder'].trim()
		useCrypto=props['server.simulator.crypto.enabled'].trim()
		productionParameters=root
	}

	remoteServer(RemoteServerSimulator,ref('simulatorRemoteModel')){
		simulatorGui=ref('simulatorGui')
		cryptoFieldsConfig=ref('cryptoFieldsConfig')
		productionParameters=ref('productionParameters')
		serviceProviderManager=ref('cryptoProviderManager')
		storage=ref('storage')
		fileSequenceStorageProvider=ref('fileSequenceStorageProvider')
		remoteServerSimulatorOutputFolder = profilePath+'/simulProductSend'
		cryptoMode=props['server.simulator.cryptoMode'].trim()
		cryptoModelPreset=props['server.simulator.cryptoModelPreset'].trim()
		hrdEnable=props['hrd.enable'].trim()
	}
}

