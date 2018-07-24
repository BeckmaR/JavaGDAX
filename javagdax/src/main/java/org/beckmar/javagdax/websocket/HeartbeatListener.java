package org.beckmar.javagdax.websocket;

public interface HeartbeatListener {
	public void heartbeat();

	public void lastTradeIdChanged(String productId, int tradeId);
}
