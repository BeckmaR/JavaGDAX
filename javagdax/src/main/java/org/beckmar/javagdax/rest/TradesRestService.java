package org.beckmar.javagdax.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.beckmar.javagdax.storage.MatchEntry;
import org.beckmar.javagdax.storage.MatchEntryParser;
import org.json.JSONArray;

import com.google.inject.Inject;

public class TradesRestService extends AbstractGdaxRestService {
	@Inject
	MatchEntryParser parser;

	protected String getEndpoint(String productId) {
		return "products/" + productId + "/trades";
	}

	protected String getEndpoint(String productId, int after) {
		return getEndpoint(productId) + "?after=" + after;
	}

	public List<MatchEntry> getTrades(String productId) throws IOException {
		return getTrades(productId, 0);
	}

	public List<MatchEntry> getTrades(String productId, int after) throws IOException {
		String endpoint = after <= 0 ? getEndpoint(productId) : getEndpoint(productId, after);
		List<MatchEntry> result = new ArrayList<>(100);
		JSONArray array = getRESTArray(endpoint);
		int i = 0;
		while (!array.isNull(i)) {
			result.add(parser.parse(array.getJSONObject(i), productId));
			i++;
		}
		return result;
	}
}
