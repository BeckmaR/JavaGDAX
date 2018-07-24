package org.beckmar.javagdax.utils;

import static org.junit.Assert.assertEquals;

import java.time.Month;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;

import org.beckmar.javagdax.AbstractTest;
import org.junit.Test;

import com.google.inject.Inject;

public class CoinbaseUtilsTest extends AbstractTest {
	@Inject
	protected CoinbaseUtils coinBaseUtils;
	protected String timeString;

	@Test
	public void testTimeParse() {
		timeString = "2018-07-21T12:45:35.190000Z";

		OffsetDateTime time = coinBaseUtils.parseTimeString(timeString);

		assertEquals(2018, time.getYear());
		assertEquals(7, time.getMonthValue());
		assertEquals(21, time.getDayOfMonth());
		assertEquals(Month.JULY, time.getMonth());
		assertEquals(12, time.getHour());
		assertEquals(45, time.getMinute());
		assertEquals(35, time.getSecond());
		assertEquals(190, time.getLong(ChronoField.MILLI_OF_SECOND));
	}
}
