package com.sicpa.standard.sasscl.devices.brs;

import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_COUNT_DIFFERENCE_HIGH;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_PLC_ALL_CAMERAS_CONNECTED;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_PLC_ALL_CAMERAS_DISCONNECTED;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_PLC_CAMERAS_DISCONNECTION;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_PLC_FRAUD_DETECTED;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_PLC_JAM_DETECTED;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.BRS.BRS_TOO_MANY_UNREAD;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.PlcListenerAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcVariableMap;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;
import com.sicpa.standard.sasscl.model.SkuCode;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.skucheck.acquisition.GroupAcquisitionType;
import com.sicpa.standard.sasscl.skucheck.acquisition.statistics.IAcquisitionStatistics;

public class Brs extends PlcListenerAdaptor {

	private static Logger logger = LoggerFactory.getLogger(Brs.class);

	public static final String UNREAD = "unread";
	public static final String INIT = "init";
	public static final String EMPTY = "empty";
	public static final String NTF_BRS_CAMERAS_CONNECTED = "NTF_BRS_CAMERAS_CONNECTED";
	public static final String NTF_BRS_SKU_INFO = "NTF_BRS_SKU_INFO";
	public static final String NTF_BRS_PRODUCT_COUNTER1 = "NTF_BRS_PRODUCT_COUNTER1";
	public static final String NTF_BRS_PRODUCT_COUNTER2 = "NTF_BRS_PRODUCT_COUNTER2";
	public static final String NTF_BRS_FRAUD1 = "NTF_BRS_FRAUD1";
	public static final String NTF_BRS_FRAUD2 = "NTF_BRS_FRAUD2";
	public static final String NTF_BRS_JAM_DETECTED1 = "NTF_BRS_JAM_DETECTED1";
	public static final String NTF_BRS_JAM_DETECTED2 = "NTF_BRS_JAM_DETECTED2";

	protected final BrsConfigBean config;
	protected final SkuCheckFacadeProvider skuCheckFacadeProvider;
	protected final PlcProvider plcProvider;
	protected final PlcIntegerVariableDescriptor brsPlcLineTypeVarDesc;
	protected final PlcIntegerVariableDescriptor brsPlcNotificationCntVarDesc;
	protected final IAcquisitionStatistics skuCheckStats;
	protected final IStatistics productionStats;
	protected final BrsStateListener brsStateListener;
	protected final BrsAggregateModel brsAggregateModel;
	protected final ITooManyUnreadHandler tooManyUnreadHandler;

	protected static final String BARCODE_PATTERN = "^((\\w+):(\\d+):([01]):(\\d+);).*";
	protected static final Pattern pattern = Pattern.compile(BARCODE_PATTERN);

	protected boolean previousCameraError;

	public Brs(final BrsConfigBean config, final SkuCheckFacadeProvider skuCheckFacadeProvider,
			final PlcProvider plcProvider, final PlcIntegerVariableDescriptor brsPlcLineTypeVarDesc,
			final PlcIntegerVariableDescriptor brsPlcNotificationCntVarDesc,
			final IAcquisitionStatistics skuCheckStats, IStatistics productionStats,
			final BrsStateListener brsStateListener, final BrsAggregateModel brsAggregateModel,
			final ITooManyUnreadHandler tooManyUnreadHandler) {

		this.tooManyUnreadHandler = tooManyUnreadHandler;
		this.config = config;
		this.skuCheckFacadeProvider = skuCheckFacadeProvider;
		this.plcProvider=plcProvider;
		registerPlcChanged(plcProvider);
		this.plcProvider.addChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				plcProvider.get().addPlcListener(Brs.this);
				brsStateListener.setPlcAdaptor(plcProvider.get());
			}
		});
		this.brsPlcLineTypeVarDesc = brsPlcLineTypeVarDesc;
		this.brsPlcNotificationCntVarDesc = brsPlcNotificationCntVarDesc;
		this.skuCheckStats = skuCheckStats;
		this.productionStats = productionStats;
		this.brsStateListener = brsStateListener;
		this.brsAggregateModel = brsAggregateModel;

		notificationCounter = -1; // initialize, must be different from 0 or 1,
									// that is set by the PLC.

	}

	@Override
	public void onPlcEvent(PlcEvent event) {

		logger.debug("[BRS] PlcEvent, PLC variable notification: {}, value {}", event.getVarName(), event.getValue()
				.toString().replaceAll("[^\\p{Print}]", "?"));

		if (PlcVariableMap.get(NTF_BRS_CAMERAS_CONNECTED).equals(event.getVarName())) {
			onCamerasConnected(event);
			return;
		}

		if (PlcVariableMap.get(NTF_BRS_SKU_INFO).equals(event.getVarName())) {
			onSkuInfo(event);
			return;
		}

		if (PlcVariableMap.get(NTF_BRS_FRAUD1).equals(event.getVarName())) {
			if ((Boolean) event.getValue()) {
				logger.info("[BRS] Notification: Fraud detected at camera set 1.");
				EventBusService.post(new MessageEvent(this, BRS_PLC_FRAUD_DETECTED));
			}
			return;
		}

		if (PlcVariableMap.get(NTF_BRS_FRAUD2).equals(event.getVarName())) {
			if ((Boolean) event.getValue()) {
				logger.info("[BRS] Notification: Fraud detected at camera set 2.");
				EventBusService.post(new MessageEvent(this, BRS_PLC_FRAUD_DETECTED));
			}
			return;
		}

		if (PlcVariableMap.get(NTF_BRS_JAM_DETECTED1).equals(event.getVarName())) {
			if ((Boolean) event.getValue()) {
				logger.info("[BRS] Notification: Jam detected at camera set 1.");
				EventBusService.post(new MessageEvent(this, BRS_PLC_JAM_DETECTED));
			}
			return;
		}

		if (PlcVariableMap.get(NTF_BRS_JAM_DETECTED2).equals(event.getVarName())) {
			if ((Boolean) event.getValue()) {
				logger.info("[BRS] Notification: Jam detected at camera set 2.");
				EventBusService.post(new MessageEvent(this, BRS_PLC_JAM_DETECTED));
			}
			return;
		}

		logger.debug("[BRS] Event Unknown, variable notification: {}", event.getVarName());
	}

	protected int notificationCounter;

	protected ExecutorService singleThreadedExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			Thread result = new Thread(r, "BRS local thread");
			result.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

				public void uncaughtException(Thread t, Throwable e) {

					logger.error("[BRS]Problem in BRS execution.", e);
				}
			});
			return result;
		}
	});

	void setExecutorService(ExecutorService testingExecutorService) {
		this.singleThreadedExecutor = testingExecutorService;
	}

	/**
	 * Expected event message string is composed of 4 fields separated by colon ':' and terminated by semicolon ';'. The
	 * first field is the barcode. The second is the number of scanned products with that barcode since the previous
	 * notification. Third Field is an integer to indicate Offline production (0) or Normal production Online (1).
	 * Finally the fourth field is an additional field to verify that a notification has changed and discard duplicated
	 * notificationis (see below.)
	 * 
	 * <p>
	 * This is the format of the string that it is expected:
	 * <p>
	 * <code>barcode:count:offline/online:changeField;</code>
	 * <ul>
	 * <li>
	 * <p>
	 * Barcode is a string.</li>
	 * <li>
	 * <p>
	 * Count is an integer.</li>
	 * <li>
	 * <p>
	 * Offline/online is an integer either 0 or 1.
	 * <li>
	 * <p>
	 * ChangeField is an integer.</li>
	 * </ul>
	 * 
	 * Everything after the semicolon ';' will be ignored. If it not formatted as described, the notification will be
	 * ignored.
	 * 
	 * 
	 * <p>
	 * This pattern <code>"^((\\w+):(\\d+):([01]):(\\d+);).*"</code> will match different groups. For example with this
	 * string: <code>'2938237:23:1:217;XXXXXXX'</code>:
	 * 
	 * <ul>
	 * <li>
	 * <p>
	 * Group 0, whole string: <code>'2938237:23:1;XXXXXXX'</code></li>
	 * <li>
	 * <p>
	 * Group 1, the target information string: <code>'2938237:23:1:217;'</code></li>
	 * <li>
	 * <p>
	 * Group 2, the barcode string: <code>'2938237'</code></li>
	 * <li>
	 * <p>
	 * Group 3, the count string: <code>'23'</code></li>
	 * <li>
	 * <p>
	 * Group 4, the Offline/online production field: <code>'1'</code></li>
	 * <li>
	 * <p>
	 * Group 5, the changeField string: <code>'217'</code></li>
	 * </ul>
	 * 
	 * <p>
	 * Description of the protocol for the PLC notifications regarding BRS.
	 * 
	 * 
	 * <p>
	 * The PLC sends notifications when a certain threshold of scanned products is reached. It sends one notification
	 * per barcode. Each notification contains the barcode, the number of barcode reads, the offline indicator and the
	 * change field as described before. For example if the barcode count threshold set in the PLC is 20, this
	 * notifications will be received:
	 * <ul>
	 * <li>
	 * <p>
	 * <code>2093482:16:1:217</code></li>
	 * <li>
	 * <p>
	 * <code>4293038:1:1:218</code></li>
	 * <li>
	 * <p>
	 * <code>UNREAD:3:1:219</code></li>
	 * <li>
	 * <p>
	 * <code>...</code></li>
	 * </ul>
	 * 
	 * Meaning that he scanned 20 products resulting: 16 products where scanned with barcode <code>2093482</code>, 1
	 * product with barcode <code>4293038</code> and 3 products were not able to be scanned.
	 * 
	 * <p>
	 * Barcode field value <code>UNREAD</code>:
	 * <p>
	 * When the camera could not read a barcode, it notifies the same way as a normal barcode but with the string
	 * 'UNREAD' in the barcode field. These unread are processed in a different way than a normal barcode.
	 * 
	 * <p>
	 * Barcode field value <code>INIT</code>:
	 * <p>
	 * When a signal is sent to the PLC to switch off the BRS system, the BRS notifies the last value read even if the
	 * threshold of count to send the notification was not still reached. After that, it sets the value of the variable
	 * to <code>'INIT:0:1:changeField'</code>.
	 * 
	 * <p>
	 * When we connect to the PLC, the variable that contains this string is registered and we are immediately notified
	 * of the content value. That value should be processed because it can contain a barcode count if the application
	 * did not closed correctly (sent the BRS switch off signal to the PLC.)
	 * 
	 * <p>
	 * If the switch down of the BRS system was done, it will contain a string like this:
	 * <p>
	 * <code>INIT:0:1:changeField</code>
	 * <p>
	 * Where the barcode field is the string 'INIT'.
	 * <p>
	 * This kind of notification should be ignored, it is just used with the purpose of differentiate when the
	 * application terminated correctly and switch off the BRS system.
	 * 
	 * <p>
	 * The third field <code>changeField:</code>
	 * <p>
	 * Sometimes the 'DLL' or PLC software (bug) sends a notification even if the variable did not changed, so we use
	 * this field to verify that it is a different notification.
	 * <p>
	 * Example:
	 * <ul>
	 * <li>
	 * <p>
	 * <code>2093482:18:217</code></li>
	 * <li>
	 * <p>
	 * <code>2093482:18:217</code>, will be ignored</li>
	 * <li>
	 * <p>
	 * <code>UNREAD:2:218</code></li>
	 * <li>
	 * <p>
	 * <code>2093482:20:219</code></li>
	 * <li>
	 * <p>
	 * <code>2093482:15:220</code></li>
	 * <li>
	 * <p>
	 * <code>UNREAD:5:221</code></li>
	 * <li>
	 * <p>
	 * <code>UNREAD:5:221</code>, will be ignored</li>
	 * </ul>
	 * 
	 * It might happen that due to the underlying threads processing the notifications, these are received and processed
	 * in different order. In that case it is possible to process duplicated notifications:
	 * <p>
	 * Example:
	 * <ul>
	 * <li>
	 * <p>
	 * <code>2093482:18:217</code></li>
	 * <li>
	 * <p>
	 * <code>2093482:18:217</code>, will be ignored</li>
	 * <li>
	 * <p>
	 * <code>UNREAD:2:218</code></li>
	 * <li>
	 * <p>
	 * <code>2093482:18:217</code>, will be processed!!!</li>
	 * <li>
	 * <p>
	 * <code>2093482:20:219</code></li>
	 * <li>
	 * <p>
	 * <code>2093482:15:220</code></li>
	 * <li>
	 * <p>
	 * <code>UNREAD:5:221</code></li>
	 * <li>
	 * <p>
	 * <code>UNREAD:5:221</code>, will be ignored</li>
	 * </ul>
	 * *
	 * 
	 * 
	 * @param event
	 *            contains the PlcEvent whose value converted to String is the message we have to pattern match to get
	 *            the different fields, barcode, count and changeField.
	 */
	protected void onSkuInfo(final PlcEvent event) {
		singleThreadedExecutor.execute(new Runnable() {
			@Override
			public void run() {
				doOnSKUInfo(event);
			}
		});
	}

	protected void doOnSKUInfo(PlcEvent event) {

		Matcher m = pattern.matcher(event.getValue().toString());

		if (!m.matches()) {
			logger.warn("[BRS] Received unformatted info string: {}.",
					event.getValue().toString().replaceAll("[^\\p{Print}]", "?"));
			return;
		}

		logger.debug("[BRS] Received info string: {}.", m.group(1));

		int count = Integer.parseInt(m.group(3));

		int newNotificationCounter = Integer.parseInt(m.group(5));

		// filter notification strings that have the same differentiation field
		// (set by the plc).
		// two consecutive strings received "235433453:100:1:217;",
		// "235433453:100:1:217;" must be filtered (due to error in DLL or PLC
		// notifications mechanism).
		// Two consecutive strings with different 3rd field must be processed:
		// "235433453:100:217;", "235433453:100:218;"
		if (newNotificationCounter == notificationCounter) {
			logger.debug("[BRS] Received consecutive duplicated message? same change field, ignoring message: {}.",
					m.group(1));
			return;
		}
		notificationCounter = newNotificationCounter;

		String barcode = m.group(2);

		if (INIT.equals(barcode.toLowerCase())) {
			logger.debug("[BRS] Init barcode received: {}.", m.group(1));
			synchronized (brsAggregateModel) {
				int aggregateThreshold = brsAggregateModel.getTotalcount();
				checkAggregatedSkus(aggregateThreshold);
			}
			return;
		}

		if (count <= 0) {
			logger.info("[BRS] message with count 0 ignored: {}", m.group(1));
			return;
		}

		boolean offline = Integer.parseInt(m.group(4)) == 0;

		if (offline) {
			logger.info("[BRS] {} OFFLINE products, barcode {} detected : {}.",
					new Object[] { count, barcode, m.group(1) });
			return;
		}

		logger.info("[BRS] Sent barcode info. to skucheck system, ONLINE: {}.", m.group(1));

		// When there are more that 1 independent master camera, collect the codes till the threshold
		// has been reached before determining the correct number of unread codes.
		// The threshold is the number of expected replies from the master cameras.
		// Example: 2 master cameras, and the notification counter is 5, the threshold will be 2*5=10
		if (masterCount() > 1) {
			synchronized (brsAggregateModel) {
				int aggregateThreshold = masterCount() * notificationCounter();

				brsAggregateModel.add(barcode, count);

				if (brsAggregateModel.getTotalcount() >= aggregateThreshold) {
					checkAggregatedSkus(aggregateThreshold);
				}
			}
			return;
		}

		boolean unread = UNREAD.equals(barcode.toLowerCase());
		if (unread) {
			addUnread(count);
		} else {
			addSku(barcode, count);
		}
		checkAll();
	}

	protected void checkAll() {
		skuCheckFacadeProvider.get().querySkus();
		checkAndSignalDifferenceStatisticsWarning();
		checkTooManyUnread();
	}

	protected void checkAggregatedSkus(int aggregateThreshold) {

		if (aggregateThreshold == 0) {
			return;
		}

		int unread = 0;
		for (Entry<String, Integer> entry : brsAggregateModel.getCodes().entrySet()) {
			if (UNREAD.equals(entry.getKey().toLowerCase())) {
				unread += entry.getValue();
			} else {
				addSku(entry.getKey(), entry.getValue());
			}
		}
		brsAggregateModel.clear();
		if (isMasterOnSameLine()) {
			unread = unread - (aggregateThreshold / masterCount());
		}
		addUnread(unread);

		checkAll();
	}

	protected void addSku(String code, int count) {
		for (int i = 0; i < count; i++) {
			skuCheckFacadeProvider.get().addSku(new SkuCode(code));
		}
		tooManyUnreadHandler.addRead(count);
	}

	protected void addUnread(int count) {
		for (int i = 0; i < count; i++) {
			skuCheckFacadeProvider.get().addUnread();
		}
		tooManyUnreadHandler.addUnread(count);
	}

	/**
	 * Check the difference between the BRS product count and the Production count. If the difference is higher than the
	 * established threshold then log it and propagate a message.
	 * 
	 * @return
	 */
	protected void checkAndSignalDifferenceStatisticsWarning() {
		int totalAcquisitions = 0;

		for (GroupAcquisitionType type : GroupAcquisitionType.values()) {
			totalAcquisitions += (skuCheckStats.getStatistics().containsKey(type) ? skuCheckStats.getStatistics().get(
					type) : 0);
		}

		// Initialization of previous production cannot be done in constructor
		// so it must be checked here.
		if (previousProductionCount == 0) {
			previousProductionCount = productionStats.getValues().get(StatisticsKey.TOTAL);
		}

		int totalProduction = productionStats.getValues().get(StatisticsKey.TOTAL);
		int currentProduction = totalProduction - previousProductionCount;
		int difference = Math.abs(totalAcquisitions - currentProduction);

		int brsDifferenceCountThreshold = config.getDifferenceCountThreshold();
		// get propertie "brs.count.diff.threshold";

		logger.debug(
				"[BRS] COUNTS: totalProduction: {}; previousProduction: {}; currentProd: {}; Acquisitions: {}; Difference prod-acq: {}; Threshold: {}",
				new Object[] { totalProduction, previousProductionCount, currentProduction, totalAcquisitions,
						difference, brsDifferenceCountThreshold });

		if (difference > brsDifferenceCountThreshold) {
			logger.info(
					"[BRS] BRS/Production count difference too high. brs_acquistions={}; production={}; difference={}; threshold={};",
					new Object[] { totalAcquisitions, currentProduction, difference, brsDifferenceCountThreshold });
			EventBusService.post(new MessageEvent(this, BRS_COUNT_DIFFERENCE_HIGH));
		}

	}

	protected void checkTooManyUnread() {
		if (tooManyUnreadHandler.isThresholdReached()) {
			EventBusService.post(new MessageEvent(this, BRS_TOO_MANY_UNREAD));
		}
	}

	protected void onCamerasConnected(PlcEvent event) {

		int value = (Short) event.getValue();

		if (isOnCamerasConnectedEventValueNegative(value)) {
			logger.error("[BRS] Plc On Cameras connected value is negative {}", value);
			return;
		}

		if (areAllCamerasConnected(value, LineType.cameraCount(lineType()))) {
			MessageEvent evt = new MessageEvent(plcProvider, BRS_PLC_ALL_CAMERAS_CONNECTED);
			EventBusService.post(evt);
			logger.info(evt.toString());

			if (previousCameraError) {
				// update cameras disconnection message
				String cameraDisconnected = toDisconnectedCameras(value);
				if (!cameraDisconnected.isEmpty()) {
					MessageEvent evtmsg = new MessageEvent(plcProvider, BRS_PLC_CAMERAS_DISCONNECTION,
							cameraDisconnected);
					EventBusService.post(evtmsg);
					logger.warn(evtmsg.toString());
					previousCameraError = false;
				}
			}

			return;
		}

		// Some camera error happened, signal it.
		previousCameraError = true;

		if (areAllCamerasDisconnected(value)) {
			MessageEvent evt = new MessageEvent(plcProvider, BRS_PLC_ALL_CAMERAS_DISCONNECTED);
			EventBusService.post(evt);
			logger.warn(evt.toString());
		}

		// Some cameras are disconnected, print which ones.
		// Notify message with the disconnected cameras.
		MessageEvent evt = new MessageEvent(plcProvider, BRS_PLC_CAMERAS_DISCONNECTION, toDisconnectedCameras(value));
		EventBusService.post(evt);
		logger.warn(evt.toString());

	}

	protected boolean isOnCamerasConnectedEventValueNegative(int value) {

		if (value < 0) {
			return true;
		}
		return false;
	}

	protected boolean areAllCamerasConnected(int value, int cameraCount) {
		// noinspection UnnecessaryUnboxing
		if (((int) Math.pow(2, cameraCount) - 1) == value) {
			return true;
		}
		return false;
	}

	protected boolean areAllCamerasDisconnected(int value) {

		if (0 == value) {
			return true;
		}
		return false;
	}

	protected String toDisconnectedCameras(int value) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < LineType.cameraCount(lineType()); i++) {
			if (((int) Math.pow(2, i) & value) == 0) {
				sb.append(i + 1).append(',');
			}
		}
		return sb.length() == 0 ? StringUtils.EMPTY : sb.deleteCharAt(sb.length() - 1).toString();
	}

	protected int lineType() {
		return (Integer) brsPlcLineTypeVarDesc.getValue();
	}

	protected int notificationCounter() {
		return (Integer) brsPlcNotificationCntVarDesc.getValue();
	}

	protected int masterCount() {
		return LineType.masterCount(lineType());
	}

	protected boolean isMasterOnSameLine() {
		return LineType.isMasterOnSameLine(lineType());
	}

	/**
	 * Keep the previous production (start production) counter to compare with current.
	 */
	protected int previousProductionCount;

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState() == ApplicationFlowState.STT_STARTED) {

			// TODO: reseting the statistics does not change the GUI
			// skuCheckStats.reset();
			// logger.debug("[BRS] STATE_STARTED");

			// Need to record the current production before it starts increasing
			// again to compare this count
			// with the count when an BRS event arrives.
			// See method checkAndSignalDifferenceStatisticsWarning()
			previousProductionCount = productionStats.getValues().get(StatisticsKey.TOTAL);

		}
		brsStateListener.processStateChanged(evt);
	}

	public void registerPlcChanged(final PlcProvider plcProvider) {
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				plcProvider.get().addPlcListener(Brs.this);
				for (IPlcVariable<?> notif : notifications) {
					plcProvider.get().registerNotification(notif);
				}
			}
		});
	}

	protected final Collection<IPlcVariable<?>> notifications = new ArrayList<IPlcVariable<?>>();

	public void setNotifications(Collection<IPlcVariable<?>> notifications) {
		this.notifications.addAll(notifications);
		for (IPlcVariable<?> notif : notifications) {
			listeningPlcVariableNames.add(notif.getVariableName());
		}
	}

}
