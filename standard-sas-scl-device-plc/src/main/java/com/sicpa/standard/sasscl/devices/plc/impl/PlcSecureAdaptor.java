package com.sicpa.standard.sasscl.devices.plc.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.security.SecurityModel;
import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.controller.PlcException;
import com.sicpa.standard.plc.controller.actions.PlcAction;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.common.storage.UserIdStorage;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.plc.IPlcListener;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.event.LoginAttemptEvent;
import com.sicpa.standard.sasscl.event.PlcLoginEvent;
import com.sicpa.standard.sasscl.event.PrinterProfileEvent;
import com.sicpa.standard.sasscl.security.UserId;

public class PlcSecureAdaptor extends PlcAdaptor {
	
	private static final Logger logger = LoggerFactory.getLogger(PlcSecureAdaptor.class);
	
	private Map<String, String> plcSecureRequestMap = new HashMap<String, String>();
	
	private UserIdStorage userIdStorage;
	
	private SecurityModel securityModel;
	
	private boolean fileReloadSent;
	
	/** Stores the user coming from GUI login attempts, null for the finger print case*/
	protected UserId currentUserId;

	public PlcSecureAdaptor(IPlcController<?> controller) {
		super(controller);
		setName("PLC Secure");
		EventBusService.register(this);
	}

	public enum SecurePlcRequest {
		
		OPEN_ELECTRICAL_CABINET			,
		OPEN_PRINTER_CABINET			,
		OPEN_DEPORTED_PRINTER_CABINET	,
		REBOOT_SECURE_SYSTEM			,
		LSM_TAKE_PICTURE				,
		SECURITY_ALARM					,
		RELOAD_USER_DATA_FILE			,
		SET_USER_ID						,
		AUTHENTICATE_USER				;
	}
	
	public void executeRequest(SecurePlcRequest request, boolean value) throws PlcException {
		
		String varName = plcSecureRequestMap.get(request.name());
		
		controller.createRequest(PlcAction.request(varName, value)).execute();
	}
	
	public void executeRequest(SecurePlcRequest request, int value) throws PlcException {
		
		String varName = plcSecureRequestMap.get(request.name());
		
		controller.createRequest(PlcAction.request(varName, value)).execute();
	}
	
	@Override
	protected void onPlcConnected() {
		sendAllParameters();
		addPlcListener(new IPlcListener() {
			@Override
			public void onPlcEvent(PlcEvent event) {
				handleEvent(event);
			}

			@Override
			public List<String> getListeningVariables() {
				List<String> result = new ArrayList<String>();
				result.addAll(PlcSecureVariables.NTF_USER_AUTHENTICATION.getLineVariableNames());
				logger.debug("getListeningVariables");
				return result;
			}
		});

		createNotifications();

		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		
		sendReloadUserDataFile();

//		checkUserDbVersion();
	}

	private void sendReloadUserDataFile() {
		if (fileReloadSent)
			return;
		try {
			executeRequest(SecurePlcRequest.RELOAD_USER_DATA_FILE, true);
			fileReloadSent = true;
		} catch (PlcException e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * For the moment no need to send the notification to reload all parameters
	 */
	@Override
	protected void sendReloadPlcParametersRequest() throws PlcAdaptorException {
//		executeRequest(PlcRequest.RELOAD_PLC_PARAM);
	}
	
	@Override
	public void reloadPlcParameter(final IPlcVariable<?> var) throws PlcAdaptorException {

		if (var == null) {
			return;
		}

		logger.debug("Reloading PLC parameter : variable name - {} , value - {}", var.getVariableName(), var.getValue());

		try {
			write(var);
//			sendReloadPlcParametersRequest();
		} catch (Exception e) {
			throw new PlcAdaptorException(e);
		}
	}
	
	@Override
	protected void handleEvent(final PlcEvent event) {
		
		logger.debug("Event {} {}", event.getVarName(), event.getValue());
		
		PlcSecureVariables variable = PlcSecureVariables.getKey(event.getVarName());
		
		if (variable == null) return;
		
		switch (variable) {
		case NTF_USER_AUTHENTICATION:
			if ((Boolean)event.getValue() == true)
				notifyAuthentication();
			break;
		}
	}
	
	/**
	 * Sends the PLC requests due to the login attempt event from the GUI
	 * Checks if the password is correct before (... shouldn't be done here, it's due to the way the SecurityService is implemented)
	 * @param event
	 */
	@Subscribe
	public void onLoginAttempt(LoginAttemptEvent event) {

		currentUserId = getUserId(event.getLogin());
		try {
			if (currentUserId != null && getAppUser(currentUserId).getPassword().equals(event.getPassword())) {
				
				executeRequest(SecurePlcRequest.SET_USER_ID, currentUserId.getUserID());
				executeRequest(SecurePlcRequest.AUTHENTICATE_USER, true);
				return;
			}
			logger.error("Invalid login/password");
		} catch (PlcException e) {
			logger.error("Unable to authenticate User ID={}", currentUserId.getUserID(), e);
		} catch (PlcAdaptorException e) {
			logger.error(e.getMessage());
		}
		EventBusService.post(new PlcLoginEvent(null, null));
	}
	

	
	private void notifyAuthentication() {

		PlcLoginEvent event = null;
		try {
			UserId userId = checkPlcUser();
			
			int printerProfileId = getPrinterProfileId(userId.getUserLevelAccess());
			EventBusService.post(new PrinterProfileEvent(printerProfileId));

			User appUser = getAppUser(userId);
			if (appUser != null) {
				event = new PlcLoginEvent(userId, appUser);
			}

		} catch (PlcAdaptorException e) {
			logger.error(e.getMessage());
		}
		if (event == null) {
			event = new PlcLoginEvent(null, null);
		}

		EventBusService.post(event);
	}

	/**
	 * For the no-fingerprint case checks the user returned by the PLC against the one from the last login attempt, 
	 * otherwise retrieves the UserId associated
	 * Returns the UserId object associated 
	 * @return
	 * @throws PlcAdaptorException
	 */
	protected UserId checkPlcUser() throws PlcAdaptorException {
		
		int intuserId;
		int printerAccess;
		int javaAccess;
		
		try {
			intuserId = read(PlcVariable.createInt32Var(PlcSecureVariables.NTF_USER_ID_AUTHENTICATED.getVariableName()));
			OperatorLogger.log("Plc User id received: {}", intuserId);

			javaAccess = read(PlcVariable.createInt32Var(PlcSecureVariables.NTF_USER_JAVA_ACCESS.getVariableName()));
			printerAccess = read(PlcVariable.createInt32Var(PlcSecureVariables.NTF_USER_PRINTER_ACCESS.getVariableName()));
			
		} catch (PlcAdaptorException e) {
			throw new PlcAdaptorException("Authentication notification received but unable to read user id", e);
		}
		
		UserId userId;
		if (currentUserId != null) {
			if(intuserId != currentUserId.getUserID() 
					|| javaAccess != getAppLevelAccess(currentUserId.getUserLevelAccess())
					|| printerAccess != getPrinterProfileId(currentUserId.getUserLevelAccess())) {
				
				throw new PlcAdaptorException("Different user id received from the PLC: id=" + intuserId);
			}
			
			userId = currentUserId;
			
		} else {
			userId = userIdStorage.getUserIdRegistry().getUserId(intuserId);			
		}
		
		if (userId == null)
			throw new PlcAdaptorException("User not found");
		
		return userId;
	}

//	/**
//	 * Checks the user db file version on the PLC and if outdated uploads it with the local file
//	 */
//	private void checkUserDbVersion() {
//
//		String version;
//		String errMsg = "Unable to retrieve PLC User-DB version";
//		try {
//			version = read(PlcVariable.createStringVar(PlcSecureVariables.NTF_USER_DATABASE_VERSION.getVariableName(), 10));
//		} catch (PlcAdaptorException e) {
//			logger.error(errMsg, e);
//			return;
//		}
//		if (version == null){
//			logger.error(errMsg);
//			return;
//		}
//		uploadUserIdFile(version);
//	}
//	
//	private void uploadUserIdFile(String version) {
//		
//		UserIdRegistry userReg;
//		try {
//			userReg = userIdStorage.load();
//		} catch (Exception e) {
//			logger.error("Unable to retrieve user registry file", e);
//			return;
//		}
//		
//		if (!checkVersion(userReg.getVersion(), version)) {
//			logger.info("User registry file already up to date on the PLC");
//			return;
//		}
//		
//		uploadFile();
//	}
//
//	private void uploadFile() {
//		String ipAddress = getIpAddress();
//		
//		FTPClient client = new FTPClient();
//		FileInputStream fis = null;
//
//		try {
//		    client.connect(ipAddress);
//		    client.login("plcadmin", "pass");
//		    
//		    String filename = userIdStorage.getFileName();
//			fis = new FileInputStream(filename);
//
//		    client.storeFile("C:\\User Data\\UserDatabase.csv", fis);
//		    client.logout();
//		} catch (IOException e) {
//			logger.error("Unable to upload user registry file", e);
//		} finally {
//		    try {
//		        if (fis != null) {
//		            fis.close();
//		        }
//		        client.disconnect();
//		    } catch (IOException e) {
//		        logger.error(e.getMessage());
//		    }
//		}
//	}
//	
//	/**
//	 * Extracts the ipAddress from the PLCModel adress
//	 * @return
//	 */
//	private String getIpAddress() {
//		String ipAddress;
//		try {
//			String s1 = model.getIp().split("@")[1];
//			ipAddress = s1.split("/")[0];
//			if (StringUtils.split(ipAddress, '.').length != 4) {
//				return null;
//			}
//		} catch (Exception e) {
//			logger.error("Couldn't retrieve ipAddress from {}", model.getIp());
//			return null;
//		}		
//		return ipAddress;
//	}
//	
//	/**
//	 * True if the local version is higher than plc or the PLC has a wrong version
//	 * False otherwise
//	 * @param localVersion
//	 * @param version
//	 * @return
//	 */
//	private boolean checkVersion(String[] localVersion, String version) {
//		String[] plcVersion = version.split(".");
//		
//		if (localVersion == null || localVersion.length <= 1)
//			return false;
//		
//		if (plcVersion == null || plcVersion.length < localVersion.length)
//			return true;
//		
//		//first field is the Version tag
//		for (int i = 1; i < localVersion.length; i++) {
//			
//			switch (checkDigit(localVersion[i], plcVersion[i])) {
//			case 1:				
//				return true;
//			case -1:
//				return false;
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * Safe compare between 2 String digits
//	 * @param localVersion
//	 * @param plcVersion
//	 * @return
//	 */
//	private int checkDigit(String localVersion, String plcVersion) {
//		
//		Integer local; 
//		try {
//			local = Integer.valueOf(localVersion);
//		} catch (Exception e) {
//			return -1;
//		}
//		Integer plc;
//		try {
//			plc = Integer.valueOf(plcVersion);
//		} catch (Exception e) {
//			return 1;
//		}
//		
//		if (local == null) return -1;
//		if (plc ==null) return 1;
//		
//		return local.compareTo(plc);
//	}
	
	private User getAppUser(UserId userId) throws PlcAdaptorException {
		
		int appLevelAccess = getAppLevelAccess(userId.getUserLevelAccess());
		
		for (User user : securityModel.getUsers()) {
			if (securityModel.getDefaultUser().getLogin().equals(user.getLogin()))
					continue;
			Integer valueOf;
			try {
				valueOf = Integer.valueOf(user.getLogin());
			} catch (Exception e) {
				throw new PlcAdaptorException("Invalid application user profile id (should be a digit) : " + user.getLogin());
			}
			if (valueOf == appLevelAccess) {
				return user;
			}
		}
		return null;
	}
	
	private int getAppLevelAccess(String userLevelAccess) throws PlcAdaptorException {
		
		if (userLevelAccess == null || userLevelAccess.length() != 3) {
			throw new PlcAdaptorException("Invalid level access received from the PLC : " + userLevelAccess);
		}
		return Integer.valueOf(userLevelAccess.substring(2));
	}
	
	private int getPrinterProfileId(String userLevelAccess) throws PlcAdaptorException {
		
		if (userLevelAccess == null || userLevelAccess.length() != 3) {
			throw new PlcAdaptorException("Invalid level access received from the PLC : " + userLevelAccess);
		}
		return Integer.valueOf(userLevelAccess.substring(1,2));
	}

	protected UserId getUserId(String login) {
		for (UserId userId : userIdStorage.getUserIdRegistry().getUserIdList()) {
			if (userId.getLogin().equals(login)) {
				return userId;
			}
		}
		return null;
	}

	public Map<String, String> getPlcSecureRequestMap() {
		return plcSecureRequestMap;
	}

	public void setPlcSecureRequestMap(Map<String, String> securePlcRequestActionMap) {
		this.plcSecureRequestMap = securePlcRequestActionMap;
	}

	public UserIdStorage getUserIdStorage() {
		return userIdStorage;
	}

	public void setUserIdStorage(UserIdStorage userIdStorage) {
		this.userIdStorage = userIdStorage;
	}

	public SecurityModel getSecurityModel() {
		return securityModel;
	}

	public void setSecurityModel(SecurityModel securityModel) {
		this.securityModel = securityModel;
	}
	
}
