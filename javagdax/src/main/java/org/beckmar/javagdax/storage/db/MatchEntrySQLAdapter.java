package org.beckmar.javagdax.storage.db;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beckmar.javagdax.storage.MatchEntry;

import com.google.inject.Inject;

public class MatchEntrySQLAdapter {
	public static final String TRADE_ID = "trade_id";
	public static final String SELL = "sell";
	public static final String PRICE_UNSCALED = "price_unscaled";
	public static final String PRICE_SCALE = "price_scale";
	public static final String SIZE_UNSCALED = "size_unscaled";
	public static final String SIZE_SCALE = "size_scale";
	public static final String PRODUCT_ID = "product_id";
	public static final String TIME = "time";

	protected Connection connection;

	protected PreparedStatement get;
	protected PreparedStatement put;

	protected Map<String, Integer> productIdCache;

	@Inject
	public MatchEntrySQLAdapter(Connection connection) throws SQLException {
		System.out.println("SQLAdapter created (" + table() + ")");
		this.connection = connection;

		String getQuery = "SELECT "
				+ "t.trade_id, t.sell, t.price_unscaled, t.price_scale, t.size_unscaled, t.size_scale, UNIX_TIMESTAMP(t.time) * 1000 as time, p.name as product_id "
				+ "FROM " + table() + " t " + "INNER JOIN product_id p on t.product_id = p.id "
				+ "WHERE trade_id = ? AND product_id = ?";
		String putQuery = "INSERT INTO " + table()
				+ " (trade_id, sell, price_unscaled, price_scale, size_unscaled, size_scale, time, product_id)"
				+ " VALUES (?, ?, ?, ?, ?, ?, FROM_UNIXTIME(? * 0.001), ?)";

		get = connection.prepareStatement(getQuery);
		put = connection.prepareStatement(putQuery);

		productIdCache = new HashMap<>();
	}

	@Override
	protected void finalize() throws Throwable {
		get.close();
		put.close();
		super.finalize();
	}

	protected String table() {
		return "trades";
	}

	public synchronized int getMaxTradeId(String productId) throws SQLException {
		String query = "SELECT MAX(trade_id) from " + table() + " where product_id = " + getProductId(productId);
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query);) {
			if (rs.next()) {
				return rs.getInt(1);
			}
			return -1;
		}
	}

	public synchronized int getMaxMissingTradeId(String productName) throws SQLException {
		int productId = getProductId(productName);
		//@formatter:off
		String query  =
				"select t.trade_id -1 as start\n" +
						"from " + table() + " as t\n" +
						"left outer join " + table() + " as r on t.trade_id - 1 = r.trade_id\n" +
						"and r.product_id = " + productId +
						" where r.trade_id is null\n" +
						" and t.product_id = " + productId	+
						" order by start DESC";
		//@formatter:on
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			if (rs.next()) {
				int id = rs.getInt(1);
				return id;
			}
			return -1;
		}
	}

	public synchronized MatchEntry get(int tradeId, String productId) throws SQLException {
		get.setInt(1, tradeId);
		get.setInt(2, getProductId(productId));

		try (ResultSet rs = get.executeQuery()) {
			if (rs.next()) {
				int trade_id = rs.getInt(TRADE_ID);
				boolean sell = rs.getBoolean(SELL);
				BigInteger priceUnscaled = BigInteger.valueOf(rs.getLong(PRICE_UNSCALED));
				int priceScale = rs.getInt(PRICE_SCALE);
				BigInteger sizeUnscaled = BigInteger.valueOf(rs.getLong(SIZE_UNSCALED));
				int sizeScale = rs.getInt(SIZE_SCALE);
				String productIdRes = rs.getString(PRODUCT_ID);
				long timestamp = rs.getLong(TIME);

				BigDecimal price = new BigDecimal(priceUnscaled, priceScale);
				BigDecimal size = new BigDecimal(sizeUnscaled, sizeScale);

				OffsetDateTime dateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);

				return new MatchEntry(trade_id, sell ? "sell" : "buy", size, price, productIdRes, dateTime);
			}
			return null;
		}
	}

	public synchronized boolean exists(MatchEntry entry) throws SQLException {
		String query = "SELECT EXISTS(SELECT 1 FROM " + table() + " WHERE trade_id = " + entry.getTradeId() + ""
				+ " AND product_id = " + getProductId(entry.getProductId()) + ")";
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			if (rs.next()) {
				return rs.getBoolean(1);
			}
		}

		return false;
	}

	public synchronized void put(MatchEntry entry) throws SQLException {
		setPutValues(entry);

		try {
			put.executeUpdate();
		} catch (SQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
			System.err.println("Entry does exist in db: " + entry.getTradeId());
		}
	}

	protected synchronized void setPutValues(MatchEntry entry) throws SQLException {
		int priceScale = entry.getPrice().scale();
		BigInteger priceUnscaled = entry.getPrice().unscaledValue();
		int sizeScale = entry.getSize().scale();
		BigInteger sizeUnscaled = entry.getSize().unscaledValue();
		boolean sell = entry.getSide().equals("sell");

		put.setInt(1, entry.getTradeId());
		put.setBoolean(2, sell);
		put.setLong(3, priceUnscaled.longValue());
		put.setInt(4, priceScale);
		put.setLong(5, sizeUnscaled.longValue());
		put.setInt(6, sizeScale);
		put.setLong(7, (entry.getTime().toInstant().toEpochMilli()));
		put.setInt(8, getProductId(entry.getProductId()));
	}

	public synchronized void put(List<MatchEntry> entries) throws SQLException {
		connection.setAutoCommit(false);
		for (MatchEntry matchEntry : entries) {
			setPutValues(matchEntry);
			put.addBatch();
		}
		put.executeBatch();
		connection.commit();
		connection.setAutoCommit(true);
	}

	public synchronized List<String> getProductIds() throws SQLException {
		List<String> result = new ArrayList<>();
		String query = "SELECT name from product_id";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				result.add(rs.getString(1));
			}

		}

		return result;
	}

	protected synchronized int getProductId(String name) throws SQLException {
		Integer productId = productIdCache.get(name);
		if (productId != null) {
			return productId;
		}
		String query = "SELECT id from product_id where name = '" + name + "'";
		String put = "INSERT INTO product_id (name) VALUES('" + name + "')";
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			if (rs.next()) {
				productIdCache.put(name, rs.getInt(1));
				return rs.getInt(1);
			} else {
				stmt.executeUpdate(put);
				return getProductId(name);
			}
		}

	}

	protected String time(Instant time) {
		return "FROM_UNIXTIME(" + String.valueOf(time.toEpochMilli()) + " * 0.001)";
	}
}
