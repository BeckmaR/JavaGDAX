package org.beckmar.javagdax.storage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.beckmar.javagdax.rest.TradesRestService;
import org.beckmar.javagdax.storage.db.MatchEntrySQLAdapter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MatchEntryStorageManager {
	@Inject
	protected MatchEntryStorage storage;
	@Inject
	protected TradesRestService restService;
	@Inject
	protected MatchEntrySQLAdapter db;

	protected int oldTradeId = -1;

	public void findHoles(String productId) throws SQLException, IOException {
		int maxMissingTradeId = db.getMaxMissingTradeId(productId);
		if (maxMissingTradeId == oldTradeId) {
			return;
		}
		MatchEntry entry = db.get(maxMissingTradeId + 1, productId);
		if (entry.getAge().toDays() > 180) {
			oldTradeId = maxMissingTradeId;
			return;
		}
		loadId(productId, maxMissingTradeId);
	}

	protected void loadId(String productId, int id) throws IOException {
		System.out.println("Getting id " + id + " from REST [" + productId + "]");
		List<MatchEntry> trades = restService.getTrades(productId, id + 1);
		storage.put(trades);
	}
}
