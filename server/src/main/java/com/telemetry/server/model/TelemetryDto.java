package com.telemetry.server.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelemetryDto {
	
	private String cpuUsage;
	private String memoryUsed;
	private List<String> processes;

}
