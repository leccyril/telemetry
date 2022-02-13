package com.telemetry.server.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.telemetry.server.model.Telemetry;
import com.telemetry.server.model.TelemetryDto;
import com.telemetry.server.repository.TelemetryRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@Slf4j
@CrossOrigin("*")
@Controller
public class TelemetryController {

	@Autowired
	TelemetryRepository repository;

	private final Map<String, RSocketRequester> CLIENTS = new HashMap<>();

	@GetMapping("/clients/connected")
	public ResponseEntity<?> getClientsConnected() {
		log.debug("Get connected client list");
		return ResponseEntity.ok(CLIENTS.keySet());
	}

	@GetMapping("/clients")
	public ResponseEntity<?> getAllClients() {
		log.debug("get all data registered");
		return ResponseEntity.ok(repository.findAll());
	}

	@GetMapping("/clients/{id}")
	public ResponseEntity<?> getClientData(@PathVariable(value = "id") String id) {
		log.debug("get all data registered");
		return ResponseEntity.ok(repository.findById(id));
	}

	/**
	 * used to close manually agent
	 * 
	 * @param uuid
	 * @return
	 */
	@PostMapping("/clients/close/{id}")
	public ResponseEntity<?> closeClient(@PathVariable(value = "id") String id) {
		log.info("closing agent with id {}", id);
		// send command to client requested
		if (CLIENTS.get(id) != null) {
			Mono<Void> call = CLIENTS.get(id).route("close").data("stop").send();
			call.doOnSuccess(consumer -> {
				log.info("Client {} closed",id);
				CLIENTS.remove(id);
			}).subscribe();
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.notFound().build();

	}

	@PreDestroy
	void shutdown() {
		log.info("Detaching all remaining clients...");
		CLIENTS.values().stream().forEach(requester -> requester.rsocket().dispose());
		log.info("Shutting down.");
	}
	
	@MessageMapping("foo")
	void call(RSocketRequester requester, @Payload String data) {
		log.info(data);
	}
	

	@ConnectMapping("telemetry.identification")
	void connectShellClientAndAskForTelemetry(RSocketRequester requester, @Payload String client) {
		
		//we can allow or deny client to send telemetry datas
		requester.rsocket().onClose().doFirst(() -> {
			// Add all new clients to a client list
			log.info("Client: {} CONNECTED.", client);
			CLIENTS.put(client, requester);
		}).doOnError(error -> {
			// Warn when channels are closed by clients
			log.warn("Channel to client {} CLOSED", client);
		}).doFinally(consumer -> {
			// Remove disconnected clients from the client list
			CLIENTS.remove(client);
			log.info("Client {} DISCONNECTED", client);
		}).subscribe();

		// Once connection confirmed, ask to send telemetry update
		requester.route("telemetry").data("OPEN").retrieveFlux(TelemetryDto.class).doOnNext(s -> {
			log.debug("Client: {} inserting or updating data", client, s.getCpuUsage());
			// each time data incoming, update database
			repository.save(
					new Telemetry(client, s.getCpuUsage(), s.getMemoryUsed(), s.getProcesses(), LocalDateTime.now()));
		}).retry().subscribe();

		// manage channel close and disconnection
		Hooks.onErrorDropped(error -> {
			if ("Disposed".equals(error.getCause().getMessage())
					|| error.getMessage().contains("ClosedChannelException"))
				log.trace("client was stopped {}", client);
			else
				log.error("error while getting telemetry datas", error);
		});
	}

}
