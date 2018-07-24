package org.beckmar.javagdax.websocket;

import org.json.JSONObject;

import com.google.inject.Inject;

public class MessageDispatcher {
	public static final String MATCH = "match";
	public static final String HEARTBEAT = "heartbeat";
	public static final String TYPE = "type";

	@Inject
	protected MatchConsumer matchConsumer;

	@Inject
	protected HeartbeatWatcher heartbeatWatcher;

	public void dispatchMessage(String message) {
		JSONObject data = new JSONObject(message);
		switch (data.getString(TYPE)) {
		case MATCH: {
			matchConsumer.consume(data);
			break;
		}
		case HEARTBEAT: {
			heartbeatWatcher.consume(data);
			break;
		}

		default:
			System.out.println(message);
		}
	}
}
