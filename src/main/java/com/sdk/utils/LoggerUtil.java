package com.sdk.utils;

import com.sdk.OpenSDk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class LoggerUtil {
	private static boolean isLog = true;
	private static Logger logger;
	static {
		if (logger == null) {
			logger = LoggerFactory.getLogger(OpenSDk.class);
			
		}
	}

	public static void setLogger(boolean isLog) {
		LoggerUtil.isLog = isLog;
	}
	public static void setLog(Logger logger) {
		LoggerUtil.logger = logger;
	}


	public static void debug(String msg) {
		if (isLog)
			logger.debug(new Date()+" "+msg);
	}

	public static void info(String msg) {
		if (isLog)
			logger.info(new Date()+" "+msg);
	}

	public static void warn(String msg) {
		if (isLog)
			logger.warn(msg);
	}

	public static void error(String msg) {
		if (isLog)
			logger.error(msg);
	}

	  
}
