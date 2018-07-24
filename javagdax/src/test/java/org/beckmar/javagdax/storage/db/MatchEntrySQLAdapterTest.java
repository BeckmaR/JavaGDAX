package org.beckmar.javagdax.storage.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.beckmar.javagdax.AbstractTest;
import org.beckmar.javagdax.storage.MatchEntry;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;

public class MatchEntrySQLAdapterTest extends AbstractTest {
	public static final String BTCEUR = "BTC-EUR";
	public static final String ETHEUR = "ETH-EUR";
	@Inject
	protected MatchEntrySQLAdapter adapter;

	@Inject
	Connection connection;

	@Before
	public void cleanTable() throws SQLException {
		String query = "DELETE FROM trades_test";
		connection.createStatement().executeUpdate(query);
	}

	@Test
	public void testDatabase() throws SQLException {
		MatchEntry entry = getBTCTestEntry();
		adapter.put(entry);
		MatchEntry got = adapter.get(42, BTCEUR);
		assertNotNull(got);

		assertEntriesEqual(entry, got);
	}

	@Test
	public void testDoubleInsertionTimeStamp() throws SQLException {
		final MatchEntry entry = getBTCTestEntry();
		adapter.put(entry);
		MatchEntry got = adapter.get(42, BTCEUR);
		assertNotNull(got);

		assertEntriesEqual(entry, got);
		adapter.put(entry);
		MatchEntry got2 = adapter.get(42, BTCEUR);
		assertNotNull(got2);

		assertEntriesEqual(entry, got2);
	}

	@Test
	public void testSameTradeIdDifferentProduct() throws SQLException {
		final MatchEntry BTC = getBTCTestEntry();
		final MatchEntry ETH = getETHTestEntry();

		adapter.put(BTC);
		adapter.put(ETH);

		MatchEntry matchEntry1 = adapter.get(42, BTCEUR);
		MatchEntry matchEntry2 = adapter.get(41, ETHEUR);

		assertEntriesEqual(matchEntry1, BTC);
		assertEntriesEqual(matchEntry2, ETH);

		assertEquals(null, adapter.get(42, ETHEUR));
		assertEquals(null, adapter.get(41, BTCEUR));
	}

	@Test
	public void testDoubleInsert() throws SQLException {
		MatchEntry entry = getBTCTestEntry();
		adapter.put(entry);
		adapter.put(entry);
	}

	@Test
	public void testExists() throws SQLException {
		MatchEntry entry = getBTCTestEntry();
		assertFalse(adapter.exists(entry));
		adapter.put(entry);
		assertTrue(adapter.exists(entry));
	}

	@Test
	public void testMaxId() throws SQLException {
		MatchEntry entry = getBTCTestEntry();
		adapter.put(entry);
		int maxTradeId = adapter.getMaxTradeId(BTCEUR);

		assertEquals(entry.getTradeId(), maxTradeId);
	}

	@Test
	public void testMaxMissingId() throws SQLException {
		MatchEntry entry = getBTCTestEntry();
		adapter.put(entry);
		int maxMissingTradeId = adapter.getMaxMissingTradeId(ETHEUR);
		assertEquals(-1, maxMissingTradeId);
		adapter.put(getETHTestEntry());
		assertEquals(41, adapter.getMaxMissingTradeId(BTCEUR));
		assertEquals(40, adapter.getMaxMissingTradeId(ETHEUR));
	}

	@Test
	public void testPutMany() throws SQLException {
		List<MatchEntry> entries = new ArrayList<>();
		entries.add(getBTCTestEntry());
		entries.add(getETHTestEntry());
		adapter.put(entries);
		assertTrue(adapter.exists(getBTCTestEntry()));
		assertTrue(adapter.exists(getETHTestEntry()));

	}

	protected void assertEntriesEqual(MatchEntry expected, MatchEntry got) {
		assertNotNull(got);

		assertEquals(expected.getTradeId(), got.getTradeId());
		assertEquals(expected.getSide(), got.getSide());
		assertEquals(expected.getSize(), got.getSize());
		assertEquals(expected.getPrice(), got.getPrice());
		assertEquals(expected.getTime(), got.getTime());
		assertEquals(expected.getProductId(), got.getProductId());
	}
}
