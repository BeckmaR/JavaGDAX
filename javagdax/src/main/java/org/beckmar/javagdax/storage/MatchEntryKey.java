package org.beckmar.javagdax.storage;

public class MatchEntryKey {
	protected String productId;
	protected Integer tradeId;

	public MatchEntryKey(String productId, Integer tradeId) {
		super();
		this.productId = productId;
		this.tradeId = tradeId;
	}

	public MatchEntryKey(MatchEntry matchEntry) {
		this(matchEntry.getProductId(), matchEntry.getTradeId());
	}

	public String getProductId() {
		return productId;
	}

	public Integer getTradeId() {
		return tradeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((tradeId == null) ? 0 : tradeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MatchEntryKey other = (MatchEntryKey) obj;
		if (productId == null) {
			if (other.productId != null) {
				return false;
			}
		} else if (!productId.equals(other.productId)) {
			return false;
		}
		if (tradeId == null) {
			if (other.tradeId != null) {
				return false;
			}
		} else if (!tradeId.equals(other.tradeId)) {
			return false;
		}
		return true;
	}

}
