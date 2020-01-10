package com.sicpa.tt016.scl.business.coding.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.sasscl.business.coding.CodeReceivedFailedException;
import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.business.coding.impl.Coding;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.CodeType;
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
public class TT016Coding extends Coding {
	
	private static final Logger logger = LoggerFactory.getLogger(TT016Coding.class);

	public TT016Coding() {
		super();
	}

	public TT016Coding(final IStorage storage) {
		super(storage);
	}
	
	protected void generateCodesPair(IEncoder encoder, final List<Pair<String, String>> codes, final long numberCodes)
			throws CryptographyException {
		// Gets the codes from the current encoder and adds them to the
		// container to be send to the printer.
		codes.addAll(encoder.getEncryptedCodesPair(numberCodes - codes.size(), productionParameters));
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.GET_CODE_FROM_ENCODER, encoder.getId() + ""));
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
				List<Pair<String, String>> codes = new ArrayList<>();

				encoder = retreiveEnoughCodesPairToPrint(encoder, codes, number);

				sendCodePairToPrinter(codes, target, encoder, codeType);
			}
		}
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
	 *            the list where to ADD code in pair
	 * @param numberCodes
	 *            the number of codes needed
	 * @return the last used encoder
	 */
	private IEncoder retreiveEnoughCodesPairToPrint(IEncoder encoder, final List<Pair<String, String>> codes, final long numberCodes) {
		try {
			encoderPreprocessing(encoder);
			generateCodesPair(encoder, codes, numberCodes);

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
			return handleNotEnoughCodesPairInEncoder(encoder, codes, numberCodes);
		} else {
			return encoder;
		}
	}
	
	private void sendCodePairToPrinter(List<Pair<String, String>> codes, ICodeReceiver target, IEncoder encoder, CodeType codeType) {
		try {
			// After we have all the request codes, we send them to the printer
			// and save the state of the current encoder
			synchronized (this.codeReceivers) {
				for (ICodeReceiver codeReceiver : this.codeReceivers) {
					codeReceiver.provideCodePair(codes, target);
				}
			}
			target.provideCodePair(codes, target);
			if (encoder != null) {
				storage.saveCurrentEncoder(encoder);
			}
		} catch (CodeReceivedFailedException e) {
			logger.error("", e);
			EventBusService.post(new MessageEvent(MessageEventKey.Coding.FAILED_TO_PROVIDE_CODES));
		}
	}
	
	private IEncoder handleNotEnoughCodesPairInEncoder(IEncoder encoder, final List<Pair<String, String>> codes, final long numberCodes) {
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
			return retreiveEnoughCodesPairToPrint(newencoder, codes, numberCodes);
		}
	}
}