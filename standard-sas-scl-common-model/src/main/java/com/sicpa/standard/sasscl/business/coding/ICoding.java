package com.sicpa.standard.sasscl.business.coding;


/**
 * Provide code when asked
 * 
 * @author CDeAlmeida
 * 
 */
public interface ICoding {

	/**
	 * 
	 * @param codeReceiver
	 *            where the code will be sent
	 */
	void addCodeReceiver(ICodeReceiver codeReceiver);

	/**
	 * it will provide codes to the <code>ICodeReceiver</code>
	 * 
	 * @param number
	 *            the number of code to send to the <code>ICodeReceiver</code>
	 */
	void askCodes(int number,ICodeReceiver target);
}
