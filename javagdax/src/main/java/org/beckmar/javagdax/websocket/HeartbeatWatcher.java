package org.beckmar.javagdax.websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.google.inject.Singleton;

@Singleton
public class HeartbeatWatcher {
	protected Map<String, Integer> lastTradeIds = new HashMap<>();
	protected Map<String, Boolean> messageReceived = new HashMap<>();
	protected Set<HeartbeatListener> listeners = new HashSet<>();

	public void registerListener(HeartbeatListener listener) {
		listeners.add(listener);
	}

	public void removeListener(HeartbeatListener listener) {
		listeners.remove(listener);
	}

	public void consume(JSONObject message) {
		String productId = message.getString("product_id");
		messageReceived.put(productId, true);
		int id = message.getInt("last_trade_id");
		sendHeartbeat(productId);
		if (lastTradeIds.get(productId) == null) {
			lastTradeIds.put(productId, -1);
		}
		if (lastTradeIds.get(productId) != id) {
			lastTradeIds.put(productId, id);
			notifyChanged(productId, id);
		}
	}

	protected void notifyChanged(String productId, int tradeId) {
		System.out.println("Last trade id [" + productId + "]: " + tradeId);
		for (HeartbeatListener heartbeatListener : listeners) {
			heartbeatListener.lastTradeIdChanged(productId, tradeId);
		}
	}

	protected void sendHeartbeat(String productId) {
		System.out.println("Heartbeat received [" + productId + "]");
		for (HeartbeatListener heartbeatListener : listeners) {
			heartbeatListener.heartbeat();
		}
	}

	public Integer getLastTradeId(String productId) {
		Integer id = lastTradeIds.get(productId);
		return id == null ? -1 : id;
	}
}
