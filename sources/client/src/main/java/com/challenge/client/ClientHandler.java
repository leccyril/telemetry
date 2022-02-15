package com.challenge.client;

import java.time.Duration;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

import com.challenge.client.data.TelemetryDto;
import com.challenge.client.utils.TelemetryUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class ClientHandler {
		
    @MessageMapping("telemetry")
    public Flux<TelemetryDto> statusUpdate(String status) {
        log.info("Start send telemetry");        
        return Flux.interval(Duration.ofSeconds(Client.INTERVAL)).map(index ->new TelemetryDto(TelemetryUtils.getProcessCpuLoad(),TelemetryUtils.getMemoryUsage(),TelemetryUtils.getActiveProcesses()))
        		.retry();   		
    }
    
    /**
     * Used to execute command sent by server (for now only stop agent)
     * @param command
     */
    @MessageMapping("close")
    public void message(String command){
    	log.info("new command coming : {}", command);
        if("stop".equals(command)) {
        	log.info("stopping agent requiested");
        	Runtime.getRuntime().exit(0);
        }else {
        	log.error("Command not implemented");
        }
    }
}