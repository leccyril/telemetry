package com.telemetry.server.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document("telemetry")
public class Telemetry {
	
	@Id
	private String uuid;
	
	private String cpuUsage;
	private String memoryUsage;
	private List<String> processes;
	private LocalDateTime lastUpdate=LocalDateTime.now();

}
