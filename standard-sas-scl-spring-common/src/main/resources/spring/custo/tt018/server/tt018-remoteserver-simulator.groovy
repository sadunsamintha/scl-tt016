import com.sicpa.tt018.interfaces.scl.master.dto.CodeTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.MarketTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.SkuProductDTO;
import com.sicpa.tt018.scl.remoteServer.simu.AlbaniaRemoteServerModelSimulator;
import com.sicpa.tt018.scl.remoteServer.simu.AlbaniaRemoteServerSimulator

beans{

	def model= createSimulatorModel()
	
	remoteServer(AlbaniaRemoteServerSimulator){
		subSystemId = props['master.subsystemId'].trim()
		lifeChecker = ref('lifeChecker')
		storage = ref('storage')
		cryptoPassword = props['crypto.password'].trim()
		cryptoFieldsConfig = ref('cryptoFieldsConfig')
		simulatorModel = model
		remoteServerAdapter = ref('remoteServerAdaptor')
	}
}


def AlbaniaRemoteServerModelSimulator createSimulatorModel(){
	def skus=[
		[id:11,desc:'55cl',brand:'Boxer',variant:'Blond',blob:false,packid:1,ct:32],
		[id:12,desc:'25cl',brand:'Boxer',variant:'Blond',blob:true,packid:1,ct:32],
		[id:13,desc:'33cl',brand:'Boxer',variant:'Stout',blob:false,packid:1,ct:33],
		[id:21,desc:'33cl',brand:'Leffe',variant:'Blond',blob:true,packid:2,ct:34]
	]


	def model=new AlbaniaRemoteServerModelSimulator();
	def market = new MarketTypeDTO(id:4,description:'domestic_mass');
	def skuList=[]

	for(skuInfo in skus){
		SkuProductDTO sku = new SkuProductDTO();
		sku.setCustomerSkuId(skuInfo['id']);
		sku.setDescription(skuInfo['desc']);
		sku.setBrand(skuInfo['brand']);
		sku.setVariant(skuInfo['variant']);
		sku.setBlobMode(skuInfo['blob']);
		sku.setProductPackagingsId(skuInfo['packid']);
		CodeTypeDTO ct = new CodeTypeDTO();
		ct.setId(skuInfo['ct']);
		sku.setCodeType(ct);
		skuList.add(sku)
	}

	market.setSkuList(skuList)
	model.setMarketTypeDTO(market);
	return model
}
