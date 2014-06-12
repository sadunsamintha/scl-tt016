package com.sicpa.standard.sasscl.business.coding.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.sasscl.business.coding.CodeReceivedFailedException;
import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.business.coding.ICoding;
import com.sicpa.standard.sasscl.business.coding.RequestCodesEvent;
import com.sicpa.standard.sasscl.business.coding.validator.EncoderValidatorResult;
import com.sicpa.standard.sasscl.business.coding.validator.IEncoderValidator;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.common.storage.QuarantineReason;
import com.sicpa.standard.sasscl.config.GlobalConfig;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

/**
 * <p>
 * Provide codes to the printer when the application is running in SCL mode.
 * </p>
 * <p>
 * It implements the <code>IPrinterListener</code> interface so it can be notified whenever the printer requests for
 * codes.
 * </p>
 * 
 */
public class Coding implements ICoding {

	private static final Logger logger = LoggerFactory.getLogger(Coding.class);

	protected GlobalConfig globalConfig;

	protected IStorage storage;

	protected final List<ICodeReceiver> codeReceivers = new ArrayList<ICodeReceiver>();

	protected ProductionParameters productionParameters;

	protected final List<IEncoderValidator> encoderValidators = new ArrayList<IEncoderValidator>();

	// static lock so if there is more than one coding, it will still lock
	protected static final Object askCodeLock = new Object();

	public Coding() {

	}

	public Coding(final GlobalConfig globalConfig, final IStorage storage) {
		this.globalConfig = globalConfig;
		this.storage = storage;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Subscribe
	public void requestCodes(final RequestCodesEvent evt) {
		logger.debug("Request codes to print: {} for codeType {}", (int) evt.getNumberCodes(), 
				productionParameters.getSku().getCodeType().getId());
		if(productionParameters.getSku().getCodeType().getId() >= CodeType.ExtendedCodeId)
			askExtendedCodes((int) evt.getNumberCodes(), evt.getTarget());
		else
			askCodes((int) evt.getNumberCodes(), evt.getTarget());
	}

	/**
	 * fill <code>codes</code> with <code>numberCodes</code> of codes.<br>
	 * fill <code>restorableEncoders</code> with all the <code>IEncoder</code> used<br>
	 * if an exception occurred
	 * <code>restoreEncoders(restorableEncoders);<code> will be called to reset the used <code>IEncoder</code>
	 * 
	 * @param encoder
	 *            the current encoder
	 * @param codes
	 *            the list where to ADD code
	 * @param numberCodes
	 *            the number of codes needed
	 * @return the last used encoder
	 */
	protected IEncoder retreiveEnoughtCodeToPrint(IEncoder encoder, final List<String> codes, final long numberCodes) {

		try {

			encoderPreprocessing(encoder);
			generateCodes(encoder, codes, numberCodes);

		} catch (EncoderEmptyException e) {
			logger.info("encoder is empty:" + encoder.getId());
		} catch (CryptographyException e) {
			logger.error("", e);
			EventBusService.post(new MessageEvent(this, MessageEventKey.Coding.ERROR_GETTING_CODES_FROM_ENCODER, e
					.getMessage()));
			return null;
		}

		// if not enough codes in current encoder
		if (numberCodes > codes.size()) {
			return handleNotEnoughCodesInEncoder(encoder, codes, numberCodes);
		} else {
			return encoder;
		}
	}
	protected IEncoder retreiveEnoughtExtendedCodeToPrint(IEncoder encoder, final List<ExtendedCode> codes, final long numberCodes) {

		try {

			encoderPreprocessing(encoder);
			generateExtendedCodes(encoder, codes, numberCodes);

		} catch (EncoderEmptyException e) {
			logger.info("encoder is empty:" + encoder.getId());
		} catch (CryptographyException e) {
			logger.error("", e);
			EventBusService.post(new MessageEvent(this, MessageEventKey.Coding.ERROR_GETTING_EXTENDED_CODES_FROM_ENCODER, e
					.getMessage()));
			return null;
		}

		// if not enough codes in current encoder
		if (numberCodes > codes.size()) {
			return handleNotEnoughExtendedCodesInEncoder(encoder, codes, numberCodes);
		} else {
			return encoder;
		}
	}

	protected void generateCodes(IEncoder encoder, final List<String> codes, final long numberCodes)
			throws CryptographyException {
		// Gets the codes from the current encoder and adds them to the
		// container to be send to the printer.
		codes.addAll(encoder.getEncryptedCodes(numberCodes - codes.size()));
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.GET_CODE_FROM_ENCODER, encoder.getId()
				+ ""));
	}

	protected void generateExtendedCodes(IEncoder encoder, final List<ExtendedCode> codes, final long numberCodes)
			throws CryptographyException {
		// Gets the codes from the current encoder and adds them to the
		// container to be send to the printer.
		codes.addAll(encoder.getExtendedCodes(numberCodes - codes.size()));
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.GET_EXTENDED_CODE_FROM_ENCODER, encoder.getId()
				+ ""));
	}

	/**
	 * called before generating codes with this encoder, empty, for customisation purpose
	 */
	protected void encoderPreprocessing(IEncoder encoder) {

	}

	protected IEncoder handleNotEnoughCodesInEncoder(IEncoder encoder, final List<String> codes, final long numberCodes) {
		// save the fact that we reached the end of the encoder
		// when calling getEncoderToUse(true); the next encoder will be used and the current one moved as finished
		storage.saveCurrentEncoder(encoder);
		IEncoder newencoder = getEncoderToUse(true);
		if (newencoder == null) {
			if (codes.isEmpty()) {
				EventBusService.post(new MessageEvent(MessageEventKey.Coding.ERROR_NO_ENCODERS_IN_STORAGE));
			}
			return null;
		} else {
			return retreiveEnoughtCodeToPrint(newencoder, codes, numberCodes);
		}
	}

	protected IEncoder handleNotEnoughExtendedCodesInEncoder(IEncoder encoder, final List<ExtendedCode> codes, final long numberCodes) {
		// save the fact that we reached the end of the encoder
		// when calling getEncoderToUse(true); the next encoder will be used and the current one moved as finished
		storage.saveCurrentEncoder(encoder);
		IEncoder newencoder = getEncoderToUse(true);
		if (newencoder == null) {
			if (codes.isEmpty()) {
				EventBusService.post(new MessageEvent(MessageEventKey.Coding.ERROR_NO_ENCODERS_IN_STORAGE));
			}
			return null;
		} else {
			return retreiveEnoughtExtendedCodeToPrint(newencoder, codes, numberCodes);
		}
	}

	@Override
	public void addCodeReceiver(final ICodeReceiver codeReceiver) {
		synchronized (codeReceivers) {
			this.codeReceivers.add(codeReceiver);
		}
	}

	protected IEncoder getEncoderToUse(final boolean useNext) {
		IEncoder encoder = null;

		CodeType codeType = productionParameters.getSku().getCodeType();

		if (useNext) {
			logger.info("Asking storage for next encoder for code type {}", codeType);
			encoder = storage.useNextEncoder(codeType);
			// Storage contains no current encoder.
			if (encoder == null) {
				return null;
			}
		} else {
			encoder = storage.getCurrentEncoder(codeType);
			if (encoder == null) {
				return getEncoderToUse(true);
			}
		}
		if (!checkForInvalidEncoder(encoder)) {
			return getEncoderToUse(true);
		}

		if (encoder.getClass().toString().toUpperCase().contains("SIMULATOR")) {
			EventBusService.post(new MessageEvent(MessageEventKey.Simulator.ENCODER));
		}

		return encoder;
	}

	protected boolean checkForInvalidEncoder(IEncoder encoder) {
		EncoderValidationResult result = isEncoderValid(encoder);
		if (!result.isValid()) {// discard current encoder
			String prefixes = QuarantineReason.INVALID_ENCODER.getFilePrefix();
			for (String prefix : result.quarantinePrefixes) {
				prefixes += "_" + prefix;
			}
			QuarantineReason reason = new QuarantineReason(QuarantineReason.INVALID_ENCODER.getSubFolder(), prefixes);

			storage.saveToQuarantine(encoder, "encoder-" + encoder.getId(), reason);

			EventBusService.post(new MessageEvent(this, MessageEventKey.Coding.INVALID_ENCODER, encoder.getId()));
			return false;
		}
		return true;
	}

	@Override
	public void askCodes(final int number, ICodeReceiver target) {
		synchronized (askCodeLock) {

			IEncoder encoder;
			SKU sku = productionParameters.getSku();
			if (sku != null) {
				CodeType codeType = sku.getCodeType();

				encoder = getEncoderToUse(false);

				// Storage ran out of encoders.
				if (encoder == null) {
					EventBusService.post(new MessageEvent(MessageEventKey.Coding.ERROR_NO_ENCODERS_IN_STORAGE));
					return;
				}

				// codes to be sent to the printer
				List<String> codes = new ArrayList<String>();

				encoder = retreiveEnoughtCodeToPrint(encoder, codes, number);

				sendCodeToPrinter(codes, target, encoder, codeType);
			}
		}
	}

	@Override
	public void askExtendedCodes(final int number, ICodeReceiver target) {
		synchronized (askCodeLock) {

			IEncoder encoder;
			SKU sku = productionParameters.getSku();
			if (sku != null) {
				CodeType codeType = sku.getCodeType();

				encoder = getEncoderToUse(false);

				// Storage ran out of encoders.
				if (encoder == null) {
					EventBusService.post(new MessageEvent(MessageEventKey.Coding.ERROR_NO_ENCODERS_IN_STORAGE));
					return;
				}

				// codes to be sent to the printer
				List<ExtendedCode> codes = new ArrayList<ExtendedCode>();

				encoder = retreiveEnoughtExtendedCodeToPrint(encoder, codes, number);

				sendExtendedCodeToPrinter(codes, target, encoder, codeType);
			}
		}
	}
	
	protected void sendCodeToPrinter(List<String> codes, ICodeReceiver target, IEncoder encoder, CodeType codeType) {
		try {
			// After we have all the request codes, we send them to the printer
			// and save the state of the current encoder
			synchronized (this.codeReceivers) {
				for (ICodeReceiver codeReceiver : this.codeReceivers) {
					codeReceiver.provideCode(codes, target);
				}
			}
			target.provideCode(codes, target);
			if (encoder != null) {
				storage.saveCurrentEncoder(encoder);
			}
		} catch (CodeReceivedFailedException e) {
			logger.error("", e);
			EventBusService.post(new MessageEvent(MessageEventKey.Coding.FAILED_TO_PROVIDE_CODES));
		}
	}

	protected void sendExtendedCodeToPrinter(List<ExtendedCode> codes, ICodeReceiver target, IEncoder encoder, CodeType codeType) {
		try {
			// After we have all the request codes, we send them to the printer
			// and save the state of the current encoder
			synchronized (this.codeReceivers) {
				for (ICodeReceiver codeReceiver : this.codeReceivers) {
					codeReceiver.provideExtendedCode(codes, target);
				}
			}
			target.provideExtendedCode(codes, target);
			if (encoder != null) {
				storage.saveCurrentEncoder(encoder);
			}
		} catch (CodeReceivedFailedException e) {
			logger.error("", e);
			EventBusService.post(new MessageEvent(MessageEventKey.Coding.FAILED_TO_PROVIDE_EXTENDED_CODES));
		}
	}

	protected static class EncoderValidationResult {
		List<String> quarantinePrefixes = new ArrayList<String>();

		public void addPrefix(String prefix) {
			quarantinePrefixes.add(prefix);
		}

		protected boolean isValid() {
			return quarantinePrefixes.isEmpty();
		}
	}

	protected EncoderValidationResult isEncoderValid(IEncoder encoder) {
		EncoderValidationResult validationResult = new EncoderValidationResult();
		synchronized (encoderValidators) {
			// execute all the validator to have log for each validator
			for (IEncoderValidator encoderValidator : encoderValidators) {
				EncoderValidatorResult result = encoderValidator.isEncoderValid(encoder);
				if (!result.isValid()) {
					validationResult.addPrefix(result.getQuarantineFilePrefix());
				}
			}
		}
		return validationResult;
	}

	public void addEncoderValidator(IEncoderValidator encoderValidator) {
		synchronized (encoderValidators) {
			encoderValidators.add(encoderValidator);
		}
	}

	public void removeEncoderValidator(IEncoderValidator encoderValidator) {
		synchronized (encoderValidators) {
			encoderValidators.remove(encoderValidator);
		}
	}
}