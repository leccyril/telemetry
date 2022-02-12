package com.challenge.client.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TelemetryDto {
	
	private String cpuUsage;
	private String memoryUsed;
	private List<String> processes;

}
