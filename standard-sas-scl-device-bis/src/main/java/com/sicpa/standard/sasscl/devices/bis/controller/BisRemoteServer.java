package com.sicpa.standard.sasscl.devices.bis.controller;

import static java.util.concurrent.Executors.newCachedThreadPool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.devices.bis.BisAdaptorException;
import com.sicpa.standard.sasscl.devices.bis.IBisController;
import com.sicpa.standard.sasscl.devices.bis.IBisControllerListener;
import com.sicpa.standard.sasscl.devices.bis.worker.BisLifeCheckWorker;
import com.sicpa.std.bis2.core.messages.RemoteMessages;
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

	private BisMessagesHandler bisMessagesHandler;
	private BisLifeCheckWorker connectionLifeCheckWorker;

	private Channel channel;
	private ChannelFuture connectFuture;
	private ClientBootstrap bootstrap;

	private final List<IBisControllerListener> bisControllerListeners = new ArrayList<>();
	private Stack<Integer> lifeCheckTags = new Stack<>();

	private String ip;
	private int port;
	private int connectionLifeCheckIntervalSec = 5;

	public BisRemoteServer() {
	}

	public void init() {
		connectionLifeCheckWorker = new BisLifeCheckWorker(connectionLifeCheckIntervalSec, this);

		// setting up bootstrap
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(newCachedThreadPool(), newCachedThreadPool()));

		// Configure the pipeline factory.
		bisMessagesHandler = new BisMessagesHandler();
		bisMessagesHandler.addListener(this);
		bootstrap.setPipelineFactory(new ProtobufPipelineFactory(bisMessagesHandler));
		bootstrap.setOption("remoteAddress", new InetSocketAddress(ip, port));
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
		connectionLifeCheckWorker.stop();
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
		LifeCheck lifeCheck = LifeCheck.newBuilder().setType(LifeCheckType.REQUEST)
				.setTag((int) (System.currentTimeMillis() / 1000)).build();

		// keep pending life check request in lifeCheckTags
		lifeCheckTags.add(0, lifeCheck.getTag());

		sendMessage(lifeCheck);
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
		logger.debug("on lifecheck failed");

		onDisconnected();

        TaskExecutor.execute(() -> {
			try {
				connect();
			} catch (BisAdaptorException e) {
				logger.error(e.getMessage(), e);
			}

		}, "bis lifecheck");
	}

	@Override
	public void onLifeCheckSucceed() {
		onConnected();
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
		receiveLifeCheckResponce(lifeCheckResponse);
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
		logger.info("" + otherMessage);
	}

	@Override
	public void sendCredential(String user, String password) {
		RemoteMessages.SetUserPassword message = RemoteMessages.SetUserPassword.newBuilder().setUsername(user)
				.setPassword(password).build();
		sendMessage(message);
	}

	@Override
	public void sendDomesticMode() {
		sendMessage(Command.newBuilder().setCommand(CommandType.PRODUCTION_DOMESTIC).build());
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

	public void setConnectionLifeCheckIntervalSec(int connectionLifeCheckIntervalSec) {
		this.connectionLifeCheckIntervalSec = connectionLifeCheckIntervalSec;
	}
}
