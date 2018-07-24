package org.beckmar.javagdax.websocket;

import org.beckmar.javagdax.storage.MatchEntry;
import org.beckmar.javagdax.storage.MatchEntryParser;
import org.beckmar.javagdax.storage.MatchEntryStorage;
import org.json.JSONObject;

import com.google.inject.Inject;

public class MatchConsumer {
	@Inject
	protected MatchEntryParser parser;

	@Inject
	protected MatchEntryStorage storage;

	public void consume(JSONObject message) {
		MatchEntry entry = parser.parse(message);
		storage.put(entry);
		System.out.println(entry.toString());
	}
}
