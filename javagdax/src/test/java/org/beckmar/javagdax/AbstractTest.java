package org.beckmar.javagdax;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.beckmar.javagdax.storage.MatchEntry;
import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class AbstractTest {
	protected Injector injector;

	@Before
	public void injectorSetUp() {
		injector = Guice.createInjector(new TestModule());
		injector.injectMembers(this);
	}

	protected MatchEntry getBTCTestEntry() {
		return new MatchEntry(42, "sell", new BigDecimal("0.42"), new BigDecimal("13000.34"), "BTC-EUR",
				OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
	}

	protected MatchEntry getETHTestEntry() {
		return new MatchEntry(41, "buy", new BigDecimal("0.41"), new BigDecimal("13000.33"), "ETH-EUR",
				OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
	}
}
