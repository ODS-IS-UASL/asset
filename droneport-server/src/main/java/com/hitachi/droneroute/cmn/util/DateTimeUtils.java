package com.hitachi.droneroute.cmn.util;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtils {
	
	public static Timestamp getUtcCurrentTimestamp() {
		return Timestamp.valueOf(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());
	}

}
