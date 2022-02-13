package com.challenge.client;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.challenge.client.utils.TelemetryUtils;

import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Hooks;
import reactor.util.retry.Retry;
import reactor.util.retry.RetrySpec;

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

	@PostConstruct
	public void initClient() {
		String client = null;
		if ("uuid".equals(test)) {
			client = UUID.randomUUID().toString();
		} else {
			client = TelemetryUtils.getClientHostName();
		}
		log.info("Connecting using client ID:" + client);

		SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());

		//retry specification
		RetrySpec retryBackoffSpec = Retry.indefinitely()
				.doBeforeRetry(retrySignal -> log.warn("Reconnecting... {}", retrySignal));

		this.rsocketRequester = rsocketRequesterBuilder.setupRoute("telemetry.identification").setupData(client)
				.rsocketStrategies(strategies)
				.rsocketConnector(connector -> connector.acceptor(responder).reconnect(retryBackoffSpec))
				.transport(TcpClientTransport.create(host, port));

		this.rsocketRequester.rsocketClient().source().flatMap(RSocket::onClose)
				.doOnError(error -> log.warn("Connection CLOSED")).doFinally(consumer -> {
					this.rsocketRequester.dispose();
					log.warn("DISCONNECTED");
					this.initClient();
				}).subscribe();

		// manage channel close and disconnection
		Hooks.onErrorDropped(error -> {
			if (!"Disposed".equals(error.getCause().getMessage())
					|| !error.getMessage().contains("CancellationException"))

				log.error("error while getting telemetry datas", error);
		});

	}

	@Scheduled(fixedDelay = 5000)
	public void dummyMethod() {
		// do nothing just to keep running deamon

	}

}
