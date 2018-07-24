package org.beckmar.javagdax.storage;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.beckmar.javagdax.AbstractTest;
import org.beckmar.javagdax.utils.CoinbaseUtils;
import org.json.JSONObject;
import org.junit.Test;

import com.google.inject.Inject;

public class MatchEntryParserTest extends AbstractTest {
	@Inject
	protected MatchEntryParser parser;
	@Inject
	protected CoinbaseUtils utils;

	@Test
	public void testParserBuy() {
		String data = "{\"type\":\"match\",\"trade_id\":15281236,\"maker_order_id\":\"b1cf95eb-d0d6-44b7-b412-0155a3bf562d\",\"taker_order_id\":\"546eb70c-eae1-449a-a67b-e22829caa106\",\"side\":\"buy\",\"size\":\"0.00100000\",\"price\":\"6319.18000000\",\"product_id\":\"BTC-EUR\",\"sequence\":4028218707,\"time\":\"2018-07-21T22:04:18.881000Z\"}";
		MatchEntry entry = parser.parse(new JSONObject(data));

		assertEquals(15281236, entry.getTradeId());
		assertEquals(new BigDecimal("6319.18000000"), entry.getPrice());
		assertEquals("buy", entry.getSide());
		assertEquals(new BigDecimal("0.00100000"), entry.getSize());
		assertEquals("BTC-EUR", entry.getProductId());
		assertEquals(utils.parseTimeString("2018-07-21T22:04:18.881000Z"), entry.getTime());
	}
}
