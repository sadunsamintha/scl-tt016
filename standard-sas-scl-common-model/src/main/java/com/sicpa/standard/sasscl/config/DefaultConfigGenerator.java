package com.sicpa.standard.sasscl.config;

import com.sicpa.standard.client.common.utils.LogUtils;

/**
 * generate config files, or update them if you have new properties
 * 
 * @author DIelsch
 * 
 */
public class DefaultConfigGenerator {
	public static void main(final String[] args) {
		LogUtils.initLogger();
		//
		// String folder = "src/main/config/config/";
		// loadCopySave(new CameraSimulatorConfig(), (folder + "cameraSimulator.xml"));
		// loadCopySave(new CameraModel(), folder + "stdCamera.xml");

		// loadCopySave(new Scheduler(), folder + "scheduler.xml");
		//
		// loadCopySave(new GlobalConfig(), folder + "global.xml");
		//
		// loadCopySave(new PrinterModel(), folder + "stdPrinter.xml");
		//
		// loadCopySave(new PrinterSimulatorConfig(), folder + "printerSimulator.xml");
		//
		// loadCopySave(new Scheduler(), folder + "scheduler.xml");
		//
		// loadCopySave(new RemoteServerModel(), folder + "remoteServer.xml");
		//
		// RemoteServerSimulatorModel remoteServerSimulatorModel = new RemoteServerSimulatorModel();
		// try {
		// remoteServerSimulatorModel.setProductionParameters(getTreeProductionParameters());
		// } catch (StorageException e) {
		// e.printStackTrace();
		// }
		// loadCopySave(remoteServerSimulatorModel, folder + "remoteServerSimulator.xml");

	}

	// private static ProductionParameterRootNode getTreeProductionParameters() throws StorageException {
	//
	// ProductionParameterRootNode root = new ProductionParameterRootNode();
	//
	// ProductionModeNode cd1 = new ProductionModeNode(ProductionMode.STANDARD);
	// ProductionModeNode cd2 = new ProductionModeNode(ProductionMode.EXPORT);
	// ProductionModeNode cd3 = new ProductionModeNode(ProductionMode.MAINTENANCE);
	//
	// SKUNode s1 = generateSKUNode(1, "SKU #1", 1);
	// SKUNode s2 = generateSKUNode(2, "SKU #2", 1);
	// SKUNode s3 = generateSKUNode(3, "SKU #3", 1);
	// SKUNode s4 = generateSKUNode(4, "SKU #4", 1);
	// SKUNode s5 = generateSKUNode(5, "SKU #5", 1);
	//
	// // link
	// root.addChildren(cd1, cd2, cd3);
	//
	// cd1.addChildren(s1, s2, s3);
	// cd2.addChildren(s1, s2, s4, s5);
	//
	// return root;
	// }
	//
	// // generate production paramters for remote server simulator model
	// private static SKUNode generateSKUNode(final int id, final String description, final int codeType)
	// throws StorageException {
	// ArrayList<String> barcodes = new ArrayList<String>();
	// barcodes.add("000" + id);
	// SKU sku = new SKU(new CodeType(codeType), id, description, barcodes);
	// SKUNode res = new SKUNode(sku);
	// res.setFileImage("config/images/sku" + res.getId() + ".png");
	// BufferedImage img = ImageUtils.createRandomStrippedImage(50);
	//
	// try {
	// File f = new File("src/main/config/" + res.getFileImage());
	// f.getParentFile().mkdirs();
	// ImageIO.write(img, "png", f);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return res;
	// }

	// private static void loadCopySave(final Object o, final String file) {
	// try {
	// URL url = ClassLoader.getSystemResource(file);
	// if (url != null) {
	// File f = new File(url.toURI());
	// ConfigUtils.copyProperty(ConfigUtils.load(f), o);
	// }
	//
	// File newFile = new File(file);
	// newFile.getParentFile().mkdirs();
	// ConfigUtils.save(o, newFile);
	// ConfigUtils.copyProperty(ConfigUtils.load(newFile), o);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
