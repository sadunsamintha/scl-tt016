package com.sicpa.tt016.scl.remote.remoteservices;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

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
import com.sicpa.tt016.common.dto.CodingActivationSessionDTO;
import com.sicpa.tt016.common.dto.EncoderInfoDTO;
import com.sicpa.tt016.common.dto.EncoderInfoResultDTO;
import com.sicpa.tt016.common.dto.EncoderSclDTO;
import com.sicpa.tt016.common.dto.ExportSessionDTO;
import com.sicpa.tt016.common.dto.IEjectionDTO;
import com.sicpa.tt016.common.dto.MaintenanceSessionDTO;
import com.sicpa.tt016.common.dto.OfflineSessionDTO;
import com.sicpa.tt016.common.dto.SkuDTO;
import com.sicpa.tt016.common.model.CodeType;
import com.sicpa.tt016.common.security.CustomPrincipal;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;
import com.sicpa.tt016.master.scl.business.interfaces.ICodingActivationRemote;
import com.sicpa.tt016.master.scl.exceptions.InternalException;

public class TT016RemoteServices implements ITT016RemoteServices {

	private static final Logger logger = LoggerFactory.getLogger(TT016RemoteServices.class);
	private static final String CODING_SERVICE_NAME = "CodingActivationBean/remote";

	private ICodingActivationRemote codingActivation;

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
		codingActivation = (ICodingActivationRemote) locateService(CODING_SERVICE_NAME);
	}

	@Override
	public int getSubsystemId() {
		return subsystemId;
	}

	@Override
	@TimeoutLifeCheck
	public boolean isAlive() {
		logger.debug("remote server isalive called");
		return codingActivation.isAlive(subsystemId);
	}

	@Override
	@Timeout
	public List<SkuDTO> getSkuList() throws InternalException {
		logger.info("Requesting sku list");
		List<SkuDTO> skus = codingActivation.getSKU(subsystemId);
		Comparator<SkuDTO> c = (s1, s2) -> s1.getDescription().compareTo(s2.getDescription());
		Collections.sort(skus, c);
		return skus;
	}

	@Override
	@Timeout
	public IMoroccoAuthenticator getDecoder() {
		return codingActivation.getAuthenticator(subsystemId);
	}

	@Override
	@Timeout
	public EncoderInfoResultDTO sendEncoderInfo(List<EncoderInfoDTO> info) throws InternalException {
		logger.info("Sending encoders info");
		EncoderInfoResultDTO res = codingActivation.sendEncodersInfo(subsystemId, info);
		return res;
	}

	@Override
	@Timeout
	public List<EncoderSclDTO> getRemoteEncoders(int encoderQty, int codeTypeId) throws InternalException {
		logger.info("Requesting encoders");
		return codingActivation.getSclEncoders(encoderQty, new CodeType(codeTypeId), subsystemId);
	}

	@Override
	@Timeout
	public void sendOfflineProduction(OfflineSessionDTO data) throws InternalException {
		logger.info("Sending offline data");
		codingActivation.sendProductionQty(data, subsystemId);
	}

	@Override
	@Timeout
	public void sendMaintenanceProduction(MaintenanceSessionDTO data) throws InternalException {
		logger.info("Sending maintenance data");
		codingActivation.sendProductionQty(data, subsystemId);
	}

	@Override
	@Timeout
	public void sendExportProduction(ExportSessionDTO data) throws InternalException {
		logger.info("Sending export data");
		codingActivation.sendProductionQty(data, subsystemId);
	}

	@Override
	@Timeout
	public void sendDomesticProduction(CodingActivationSessionDTO activSession) throws InternalException {
		logger.info("Sending domestic data");
		codingActivation.sendProductionData(activSession, emptyList(), subsystemId);
	}

	@Override
	@Timeout
	public void sendRefeedProduction(CodingActivationSessionDTO activSession) throws InternalException {
		logger.info("Sending refeed data");
		codingActivation.sendProductionData(activSession, emptyList(), subsystemId);
	}

	@Override
	@Timeout
	public void sendEjectedProduction(IEjectionDTO ejected) throws InternalException {
		logger.info("Sending ejection data");
		CodingActivationSessionDTO emptySessionDto = new CodingActivationSessionDTO(emptyList());
		codingActivation.sendProductionData(emptySessionDto, asList(ejected), subsystemId);
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
}
