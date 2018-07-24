package org.beckmar.javagdax.storage;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;

public class MatchEntry {
	protected int tradeId;
	protected String side;
	protected BigDecimal size;
	protected BigDecimal price;
	protected String productId;
	protected OffsetDateTime time;

	public MatchEntry(int trade_id, String side, BigDecimal size, BigDecimal price, String product_id,
			OffsetDateTime time) {
		tradeId = trade_id;
		this.side = side;
		this.size = size;
		this.price = price;
		productId = product_id;
		this.time = time;
	}

	@Override
	public String toString() {
		return "MATCH " + tradeId + " [" + productId + "]: " + side + "-order fulfilled, " + size + "@" + price + " "
				+ time;
	}

	public int getTradeId() {
		return tradeId;
	}

	public String getSide() {
		return side;
	}

	public BigDecimal getSize() {
		return size;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getProductId() {
		return productId;
	}

	public OffsetDateTime getTime() {
		return time;
	}

	public Duration getAge() {
		Instant now = Instant.now();
		Instant myTime = time.toInstant();
		return Duration.between(myTime, now);
	}
}
