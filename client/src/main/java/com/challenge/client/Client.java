package com.challenge.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Service;

import com.challenge.client.utils.TelemetryUtils;

import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Client {

	@Value("${application.telemetry.test.uuid}")
	private String test;

	@Value("${application.telemetry.server.host}")
	private String host;

	@Value("${application.telemetry.server.port}")
	private Integer port;

	public static Integer INTERVAL;

	@Value("${application.telemetry.interval}")
	public void setInterval(Integer interval) {
		INTERVAL = interval;
	}

	private RSocketRequester rsocketRequester;
	private RSocketRequester.Builder rsocketRequesterBuilder;
	private RSocketStrategies strategies;

	@Autowired
	public Client(RSocketRequester.Builder builder, @Qualifier("rSocketStrategies") RSocketStrategies strategies) {
		this.rsocketRequesterBuilder = builder;
		this.strategies = strategies;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initClient() {

		String client = null;
		// (1)
		if ("uuid".equals(test)) {
			client = UUID.randomUUID().toString();
		} else {
			client = TelemetryUtils.getClientHostName();
		}
		log.info("Connecting using client ID:" + client);

		SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());

		this.rsocketRequester = rsocketRequesterBuilder.setupRoute("identification").setupData(client)
				.rsocketStrategies(strategies).rsocketConnector(connector -> connector.acceptor(responder))
				.connectTcp(host, port).block();

		this.rsocketRequester.rsocket().onClose().doOnError(error -> log.warn("Connection CLOSED"))
				.doFinally(consumer -> log.info("Client DISCONNECTED")).subscribe();

		while (true);
	}

}
