package com.sicpa.standard.sasscl.business.production;


public interface IProduction {

	/**
	 * save locally the in memory production data
	 */
	void saveProductionData();

	/**
	 * This operation retrieves all the packagedProduction data that has not been sent to the remote server (if any),
	 * and sends it to the remote server.
	 */
	void sendAllProductionData();

	/**
	 * prepare the saved production data to be send to the remote server
	 */
	void packageProduction();

	/**
	 * 
	 * ask to cancel the current production sending task
	 */
	void cancelSending();

	/**
	 * call when the application is exiting to send all production data
	 */
	void onExitSendAllProductionData();
}
