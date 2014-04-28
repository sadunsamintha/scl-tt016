package com.sicpa.standard.sasscl.sicpadata.reader;

/**
 * 
 * 
 * Interface for decoded code result. It can be implemented to return different type of results based on project
 * requirements
 * 
 * @author YYang
 * 
 */
public interface IDecodedResult {

	/**
	 * 
	 * Is code authenticated
	 * 
	 * @return
	 */
	boolean isAuthenticated();

	/**
	 * 
	 * set if the code is authenticated
	 * 
	 * @param authenticated
	 */
	void setAuthenticated(boolean authenticated);

}
