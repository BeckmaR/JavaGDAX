package org.beckmar.javagdax.storage;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.beckmar.javagdax.utils.CoinbaseUtils;
import org.json.JSONObject;

import com.google.inject.Inject;

public class MatchEntryParser {
	public static final String TRADE_ID = "trade_id";
	public static final String SIDE = "side";
	public static final String SIZE = "size";
	public static final String PRICE = "price";
	public static final String PRODUCT_ID = "product_id";
	public static final String TIME = "time";

	@Inject
	protected CoinbaseUtils coinbaseUtils;

	public MatchEntry parse(JSONObject data, String productId) {
		int trade_id = data.getInt(TRADE_ID);
		String side = data.getString(SIDE);
		BigDecimal size = new BigDecimal(data.getString(SIZE));
		BigDecimal price = new BigDecimal(data.getString(PRICE));
		OffsetDateTime dateTime = coinbaseUtils.parseTimeString(data.getString(TIME));

		return new MatchEntry(trade_id, side, size, price, productId, dateTime);
	}

	public MatchEntry parse(JSONObject data) {
		String product_id = data.getString(PRODUCT_ID);
		return parse(data, product_id);
	}
}
