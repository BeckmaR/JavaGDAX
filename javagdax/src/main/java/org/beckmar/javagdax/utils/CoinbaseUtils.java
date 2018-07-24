package org.beckmar.javagdax.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CoinbaseUtils {
	public OffsetDateTime parseTimeString(String time) {
		return OffsetDateTime.ofInstant(Instant.parse(time), ZoneOffset.UTC);
	}
}
