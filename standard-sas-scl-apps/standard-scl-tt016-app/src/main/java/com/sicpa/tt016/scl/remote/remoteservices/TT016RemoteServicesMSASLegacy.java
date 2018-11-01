package com.sicpa.tt016.scl.remote.remoteservices;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.timeout.Timeout;
import com.sicpa.standard.client.common.timeout.TimeoutLifeCheck;
import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.tt016.master.sas.business.interfaces.IActivationRemote;
import com.sicpa.tt016.common.dto.ActivationSessionDTO;
import com.sicpa.tt016.common.dto.ExportSessionDTO;
import com.sicpa.tt016.common.dto.IEjectionDTO;
import com.sicpa.tt016.common.dto.MaintenanceSessionDTO;
import com.sicpa.tt016.common.dto.OfflineSessionDTO;
import com.sicpa.tt016.common.dto.SkuDTO;
import com.sicpa.tt016.common.security.CustomPrincipal;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;
import com.sicpa.tt016.master.scl.business.interfaces.IBisUserManagerRemote;
import com.sicpa.tt016.master.scl.exceptions.InternalException;

public class TT016RemoteServicesMSASLegacy implements ITT016RemoteServicesMSASLegacy {

	private static final Logger logger = LoggerFactory.getLogger(TT016RemoteServicesMSASLegacy.class);
	private static final String ACTIVATION_SERVICE_NAME = "ActivationBean/remote";


	private IActivationRemote activationRemote;
	private IBisUserManagerRemote bisUserManagerRemote;

	private String decoderPassword;
	private String userMachine;
	private String passwordMachine;
	private int subsystemId;
	private String url;
	private Context context;

	@Override
	@Timeout
	public void login() throws Exception {
		logger.info("Connecting to remote server using:" + userMachine);
		loadInitialContext();
		loadRemoteService();
	}

	private void loadRemoteService() throws NamingException {
			activationRemote = (IActivationRemote) locateService(ACTIVATION_SERVICE_NAME);
	}

	@Override
	public int getSubsystemId() {
		return subsystemId;
	}

	@Override
	@TimeoutLifeCheck
	public boolean isAlive() {
		logger.debug("remote server isAlive called");
			return activationRemote.isAlive(subsystemId);
	}
	

	@Override
	@Timeout
	public List<SkuDTO> getSkuList() throws InternalException {
		logger.info("Requesting sku list");
		List<SkuDTO> skus = new ArrayList<SkuDTO>();
		try {
			skus = activationRemote.getSKU(subsystemId);
		} catch (Exception e) {
			logger.error("could not retrive the list of sku ");
		}
		Comparator<SkuDTO> c = (s1, s2) -> s1.getDescription().compareTo(s2.getDescription());
		Collections.sort(skus, c);
		return skus;
	}
	

	@Override
	@Timeout
	public IMoroccoAuthenticator getDecoder() {
		IMoroccoAuthenticator decoder = activationRemote.getAuthenticator(subsystemId);
		if (decoder != null) {
			try {
				decoder.load(decoderPassword);
			} catch (CryptoException e) {
				logger.error("Problem while loading the password");
			}
		}
		return decoder;
	}
	

	@Override
	@Timeout
	public void sendOfflineProduction(OfflineSessionDTO data) throws InternalException {
		logger.info("Sending offline data");
			try {
				activationRemote.sendProductionQty(data, subsystemId);
			} catch (Exception e) {
				logger.error("Problem while sending Offline data to master SAS");
			}
	}

	@Override
	@Timeout
	public void sendMaintenanceProduction(MaintenanceSessionDTO data) throws InternalException {
		logger.info("Sending maintenance data");
		try {
			activationRemote.sendProductionQty(data, subsystemId);
		} catch (Exception e) {
			logger.error("Problem while sending Maintenance data to master SAS");
		}
	}

	@Override
	@Timeout
	public void sendExportProduction(ExportSessionDTO data) throws InternalException {
		logger.info("Sending export data");
		try {
			activationRemote.sendProductionQty(data, subsystemId);
		} catch (Exception e) {
			logger.error("Problem while sending Export data to master SAS");
		}
	}

	@Override
	@Timeout
	public void sendDomesticProduction(ActivationSessionDTO activSession) throws InternalException {
		logger.info("Sending domestic data");
		try {
			activationRemote.sendProductionData( activSession, emptyList(), subsystemId);
		} catch (com.sicpa.tt016.master.sas.exceptions.InternalException e) {
			logger.error("Problem while sending Domestic production data to master SAS");
		}
	}

	
	@Override
	@Timeout
	public void sendEjectedProduction(IEjectionDTO ejected) throws InternalException {
		logger.info("Sending ejection data");
		ActivationSessionDTO emptySessionDto = new ActivationSessionDTO(emptyList());
		try {
			activationRemote.sendProductionData(emptySessionDto, asList(ejected), subsystemId);
		} catch (Exception e) {
			logger.error("Problem while sending Ejection production data to master SAS");
		}
	}
	
	
	@Override
	@Timeout
	public void sendRefeedProduction(ExportSessionDTO data) throws InternalException {
		logger.info("Sending refeed data");
		try {
			activationRemote.sendProductionQty(data, subsystemId);
		} catch (Exception e) {
			logger.error("Problem while sending Refeed data to master SAS");
		}
	}

	
	@Override
	@Timeout
	public String getBisTrainerPassword(String user) {
		return bisUserManagerRemote.getPassword(subsystemId, user);
	}

	private Object locateService(String name) throws NamingException {
		return context.lookup(name);
	}

	public void loadInitialContext() throws NamingException {
		Properties properties = new Properties();
		properties.put("java.naming.security.principal", new CustomPrincipal(userMachine, subsystemId));
		properties.put("java.naming.security.credentials", passwordMachine);
		properties.put("java.naming.factory.initial", "org.jboss.security.jndi.JndiLoginInitialContextFactory");
		properties.put(" java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
		properties.put("jnp.multi-threaded", "false");
		properties.put("java.naming.provider.url", url);
		context = new InitialContext(properties);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPasswordMachine(String passwordMachine) {
		this.passwordMachine = passwordMachine;
	}

	public void setUserMachine(String userMachine) {
		this.userMachine = userMachine;
	}

	public void setSubsystemId(int subsystemId) {
		this.subsystemId = subsystemId;
	}

	public void setDecoderPassword(String decoderPassword) {
		this.decoderPassword = decoderPassword;
	}


	

	
	
}
