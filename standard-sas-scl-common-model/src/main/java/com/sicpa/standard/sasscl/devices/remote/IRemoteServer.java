package com.sicpa.standard.sasscl.devices.remote;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

public interface IRemoteServer extends IDevice {

	/**
	 * Get the tree of selectable production parameters from remove server
	 * 
	 * @return tree of selectable production parameters
	 * 
	 * @throws RemoteServerException
	 *             throw exception when this method fails to connect to remote server
	 */
	ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException;

	/**
	 * 
	 * Get and store a list of IEncoder from remote server
	 * 
	 * 
	 * @param batchesQuantity
	 *            - Number of batch to be returned
	 * 
	 * @throws RemoteServerException
	 *             throw exception when this method fails to retrieve encoders from remote server
	 */
	void downloadEncoder(int batchesQuantity, CodeType codeType, int year) throws RemoteServerException;

	/**
	 * 
	 * Retrieve authenticator from remote server
	 * 
	 * @return IAuthenticator
	 * 
	 * @throws RemoteServerException
	 *             throw exception when this method fails to retrieve authenticator from remote server
	 */
	IAuthenticator getAuthenticator() throws RemoteServerException;

	/**
	 * 
	 * send activated product info to remote server
	 * 
	 * @param products
	 *            List of product to be sent to remote server
	 * 
	 * @throws RemoteServerException
	 *             throw exception if the sending fails
	 */
	void sendProductionData(PackagedProducts products) throws RemoteServerException;

	/**
	 * return a map that contains as a key the language key and as value the bundle that contains all the
	 * key+translation entry
	 * 
	 * @return
	 * @throws RemoteServerException
	 */
	Map<String, ? extends ResourceBundle> getLanguageBundles() throws RemoteServerException;

	/**
	 * send encoder info to the remote server
	 * 
	 * @param infos
	 *            list to send to the remote server
	 * @throws RemoteServerException
	 *             throw exception if the sending fails
	 */
	void sendEncoderInfos(List<EncoderInfo> infos) throws RemoteServerException;

	long getSubsystemID();

	void sendInfoToGlobalMonitoringTool(GlobalMonitoringToolInfo info);

	void lifeCheckTick();
}
