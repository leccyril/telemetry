package com.challenge.client.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelemetryUtils {

	private TelemetryUtils() {
		throw new AssertionError("Could no instanciate thos class");

	}


	public static String getProcessCpuLoad() {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"),
					"ProcessCpuLoad");
			return (Double) attribute * 100 + "%";
		} catch (Exception e) {
			log.error("error while retrieving CPU usage", e);
			return "NaN";
		}

	}

	public static String getMemoryUsage() {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"),
					"TotalPhysicalMemorySize");
			Object attribute2 = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"),
					"FreePhysicalMemorySize");
			Long usedMemory = (Long.parseLong(attribute.toString()) - Long.parseLong(attribute2.toString())) / 1024;
			return usedMemory + "MB";
		} catch (Exception e) {
			log.error("error while retrieving memory usage", e);
			return "NaN";
		}

	}

	public static List<String> getActiveProcesses() {
		List<String> processes = new ArrayList<>();
		ProcessHandle.allProcesses().forEach(process -> {
			if(!process.info().command().isEmpty()) {
				processes.add(process.pid()+" "+text(process.info().command()));
			}
		
		});
		return processes;
	}

	private static String text(Optional<?> optional) {
		return optional.map(Object::toString).orElse("");
	}

	public static String getClientHostName() {
		String hostname = "Unknown";

		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (UnknownHostException ex) {
			hostname= UUID.randomUUID().toString();
			System.out.println("Hostname can not be resolved");
		}
		return hostname;
	}

}
