package com.sicpa.standard.sasscl.devices.bis.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck.LifeCheckType;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;

/**
 * Autor: MCarteaux Date: 11.10.12
 * <p/>
 * Copyright (c) 2011 SICPA Security Solutions, all rights reserved.
 */
public class BisMessagesHandler extends SimpleChannelUpstreamHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BisMessagesHandler.class);

	// private IBisRemoteListener listener;
	protected final List<IBisMessageHandlerListener> listeners = new ArrayList<IBisMessageHandlerListener>();

	public BisMessagesHandler() {
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		LOGGER.info("New client connected closed");
		if (listeners != null) {
			for (IBisMessageHandlerListener listener : listeners) {
				listener.onConnected();
			}
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Object message = e.getMessage();
		LOGGER.debug("Message received - " + message.getClass().getSimpleName());

		if (message instanceof LifeCheck) {
			LifeCheck response = (LifeCheck) message;
			if (LifeCheckType.RESPONSE.equals(response.getType())) {
				for (IBisMessageHandlerListener listener : listeners) {
					listener.lifeCheckReceived(response);
				}
				return;
			}
		}
		if (message instanceof Alert) {
			for (IBisMessageHandlerListener listener : listeners) {
				listener.alertReceived((Alert) message);
			}
			return;
		}

		if (message instanceof RecognitionResultMessage) {
			for (IBisMessageHandlerListener listener : listeners) {
				listener.recognitionResultReceived((RecognitionResultMessage) message);
			}
			return;
		}

		// other messages - send it back to listeners too
		for (IBisMessageHandlerListener listener : listeners) {
			listener.onOtherMessageReceived(message);
		}

		LOGGER.debug(String.format("Unrecognised object class %s from %s", message.getClass().getSimpleName(), e
				.getChannel().toString()));
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		LOGGER.info("Channel closed, {}", e.getState().toString());
		if (listeners != null) {
			for (IBisMessageHandlerListener listener : listeners) {
				listener.onDisconnected();
			}
		}
		e.getChannel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event) {
		try {
			event.getChannel().close();
		} catch (Exception e) {
			LOGGER.error("Unexpected exception from downstream.", e.getCause());
		}
	}

	public void addListener(IBisMessageHandlerListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(IBisMessageHandlerListener listener) {
		this.listeners.remove(listener);
	}

}
