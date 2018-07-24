package org.beckmar.javagdax.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

public class CoinbaseWebSocketClient extends org.java_websocket.client.WebSocketClient {

	public static final String COINBASE_WEBSOCKET_URI = "wss://ws-feed.pro.coinbase.com";

	@Inject
	protected MessageDispatcher dispatcher;

	public CoinbaseWebSocketClient() throws URISyntaxException {
		super(new URI(COINBASE_WEBSOCKET_URI));
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		JSONObject subscribe = new JSONObject();
		JSONArray ids = new JSONArray();
		ids.put("BTC-EUR");
		ids.put("ETH-EUR");
		JSONArray channels = new JSONArray();
		channels.put("matches");
		channels.put("heartbeat");
		subscribe.put("type", "subscribe");
		subscribe.put("product_ids", ids);
		subscribe.put("channels", channels);

		send(subscribe.toString());
		System.out.println("opened connection");
	}

	@Override
	public void onMessage(String message) {
		dispatcher.dispatchMessage(message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println(
				"Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}

}
