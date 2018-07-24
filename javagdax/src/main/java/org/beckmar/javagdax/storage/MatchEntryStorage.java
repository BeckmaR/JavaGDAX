package org.beckmar.javagdax.storage;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.beckmar.javagdax.storage.db.MatchEntrySQLAdapter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MatchEntryStorage {
	@Inject
	protected MatchEntrySQLAdapter db;

	protected LoadingCache<MatchEntryKey, MatchEntry> cache;

	public MatchEntryStorage() {
		cache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterAccess(10, TimeUnit.MINUTES)
				.build(new CacheLoader<MatchEntryKey, MatchEntry>() {
					@Override
					public MatchEntry load(MatchEntryKey key) throws SQLException {
						return db.get(key.getTradeId(), key.getProductId());
					}
				});
	}

	public MatchEntry get(int tradeId, String productId) {
		try {
			return cache.get(new MatchEntryKey(productId, tradeId));
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void put(MatchEntry entry) {
		if (entry == null) {
			return;
		}
		cache.put(new MatchEntryKey(entry), entry);
		try {
			db.put(entry);
		} catch (SQLException e) {
			System.err.println("Failed to put entry into db:");
			System.err.println(entry);
			e.printStackTrace();
		}
	}

	public void put(List<MatchEntry> entries) {
		for (MatchEntry matchEntry : entries) {
			cache.put(new MatchEntryKey(matchEntry), matchEntry);
		}
		try {
			db.put(entries);
		} catch (SQLException e) {
			System.err.println("Failed to put entries into db");
			e.printStackTrace();
		}
	}

}
