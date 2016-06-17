package com.sicpa.standard.sasscl.devices.bis.controller;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.sicpa.standard.sasscl.devices.bis.BisAdaptorException;
import com.sicpa.standard.sasscl.devices.bis.IBisController;
import com.sicpa.standard.sasscl.devices.bis.IBisControllerListener;
import com.sicpa.standard.sasscl.devices.bis.IScheduleWorker;
import com.sicpa.standard.sasscl.devices.bis.worker.ConnectionLifeCheckWorker;
import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.Command;
import com.sicpa.std.bis2.core.messages.RemoteMessages.Command.CommandType;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck.LifeCheckType;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkusMessage;
import com.sicpa.std.bis2.remote.netty.ProtobufPipelineFactory;

public class BisRemoteServer implements IBisController, IBisMessageHandlerListener {

	private static final Logger logger = LoggerFactory.getLogger(BisRemoteServer.class);

	private String ip;
	private int port;

	private int connectionLifeCheckIntervalMs;

	private Channel channel;
	private ChannelFuture connectFuture;
	private ClientBootstrap bootstrap;
	private LifeCheck lifeCheck;
	private IScheduleWorker connectionLifeCheckWorker;
	private Stack<Integer> lifeCheckTags = new Stack<>();

	private AtomicBoolean isSchedulerWorkerInit = new AtomicBoolean(false);

	private BisMessagesHandler bisMessagesHandler;

	private final List<IBisControllerListener> bisControllerListeners = new ArrayList<>();

	private final ExecutorService singleThreadedExecutorService = Executors.newSingleThreadExecutor();

	public BisRemoteServer() {

	}

	public void init() {
		connectionLifeCheckWorker = new ConnectionLifeCheckWorker(connectionLifeCheckIntervalMs);
		connectionLifeCheckWorker.addController(this);

		// setting up bootstrap
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		// Configure the pipeline factory.
		bisMessagesHandler = new BisMessagesHandler();
		bisMessagesHandler.addListener(this);
		bootstrap.setPipelineFactory(new ProtobufPipelineFactory(bisMessagesHandler));
		bootstrap.setOption("remoteAddress", new InetSocketAddress(ip, port));
		logger.debug("Scheduler tasks started.");
	}

	@Override
	public void connect() throws BisAdaptorException {

		try {
			if (channel != null) {
				if (channel.isConnected()) {
					channel.disconnect().awaitUninterruptibly();
				}

				if (channel.isOpen()) {
					channel.close();
				}
			}

			if (isSchedulerWorkerInit.compareAndSet(false, true)) {
				connectionLifeCheckWorker.create();
			}

			// Make a new connection.
			connectFuture = bootstrap.connect();

			// Wait until the connection is made successfully.
			channel = connectFuture.awaitUninterruptibly().getChannel();

			// reset the lifeCheckTags to 0 Tag
			lifeCheckTags.clear();

			// start performing connect
			connectionLifeCheckWorker.start();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BisAdaptorException("Fail to connect to remote server!", e);
		}
	}

	@Override
	public void disconnect() {
		channel.disconnect().awaitUninterruptibly();
		channel.close();
		bootstrap.releaseExternalResources();
		connectionLifeCheckWorker.dispose();
		isSchedulerWorkerInit.set(false);
	}

	@Override
	public void start() throws BisAdaptorException {
		// recognitionResultRequestWorker.start();
	}

	@Override
	public void stop() {
	}

	@Override
	public void sendSkuList(SkusMessage skus) {
		sendMessage(skus);

	}

	protected void sendMessage(GeneratedMessage message) {
		if ((channel != null) && (channel.isConnected())) {
			channel.write(message);
		}
	}

	@Override
	public void sendLifeCheck() {
		lifeCheck = LifeCheck.newBuilder().setType(LifeCheckType.REQUEST)
				.setTag((int) (System.currentTimeMillis() / 1000)).build();

		// keep pending life check request in lifeCheckTags
		lifeCheckTags.add(0, lifeCheck.getTag());

		sendMessage(lifeCheck);
	}

	@Override
	public void sendRecognitionResultRequest() {
		sendMessage(Command.newBuilder().setCommand(CommandType.RECOGNITION_REQUEST).build());

	}

	@Override
	public void sendAutoSave() {
		sendMessage(Command.newBuilder().setCommand(CommandType.AUTO_SAVE).build());
	}

	@Override
	public void sendUnknownSave() {
		sendMessage(Command.newBuilder().setCommand(CommandType.UNKNOWN_SAVE).build());
	}

	@Override
	public boolean isConnected() {

		if ((lifeCheckTags.size() < 3) && (channel.isConnected())) {
			return true;
		}

		return false;
	}

	@Override
	public void receiveLifeCheckResponce(LifeCheck lifeCheckResponse) {
		while (!lifeCheckTags.isEmpty()) {

			// remove life check request in lifeCheckTags if received response from server
			if (lifeCheckTags.peek().equals(lifeCheckResponse.getTag())) {
				lifeCheckTags.pop();
				return;
			}

			lifeCheckTags.pop();
		}
	}

	@Override
	public void onLifeCheckFailed() {

		// do connect again
		singleThreadedExecutorService.execute(new Runnable() {

			@Override
			public void run() {
				try {
					if (isConnected()) {
						return;
					}
					for (IBisControllerListener controllerListener : bisControllerListeners) {
						controllerListener.onLifeCheckFailed();
					}

					connect();
				} catch (BisAdaptorException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});

	}

	@Override
	public void onLifeCheckSucceed() {
		for (IBisControllerListener controllerListener : bisControllerListeners) {
			controllerListener.onConnection();
		}
	}

	@Override
	public void onConnected() {
		for (IBisControllerListener controllerListener : bisControllerListeners) {
			controllerListener.onConnection();
		}
	}

	@Override
	public void onDisconnected() {
		for (IBisControllerListener controllerListener : bisControllerListeners) {
			controllerListener.onDisconnection();
		}
	}

	@Override
	public void lifeCheckReceived(LifeCheck lifeCheckResponse) {
		this.receiveLifeCheckResponce(lifeCheckResponse);
		for (IBisControllerListener controllerListener : bisControllerListeners) {
			controllerListener.lifeCheckReceived(lifeCheckResponse);
		}
	}

	@Override
	public void alertReceived(Alert command) {
		for (IBisControllerListener controllerListener : bisControllerListeners) {
			controllerListener.alertReceived(command);
		}
	}

	@Override
	public void recognitionResultReceived(RecognitionResultMessage result) {
		for (IBisControllerListener controllerListener : bisControllerListeners) {
			controllerListener.recognitionResultReceived(result);
		}
	}

	@Override
	public void onOtherMessageReceived(Object otherMessage) {
		for (IBisControllerListener controllerListener : bisControllerListeners) {
			controllerListener.otherMessageReceived(otherMessage);
		}
	}

	@Override
	public void addListener(IBisControllerListener listener) {
		this.bisControllerListeners.add(listener);
	}

	@Override
	public void removeListener(IBisControllerListener listener) {
		this.bisControllerListeners.remove(listener);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setConnectionLifeCheckIntervalMs(int connectionLifeCheckIntervalMs) {
		this.connectionLifeCheckIntervalMs = connectionLifeCheckIntervalMs;
	}
}
