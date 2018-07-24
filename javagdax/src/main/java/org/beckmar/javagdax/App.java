package org.beckmar.javagdax;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.beckmar.javagdax.storage.MatchEntryStorageManagerRunner;
import org.beckmar.javagdax.storage.db.MatchEntrySQLAdapter;
import org.beckmar.javagdax.websocket.CoinbaseWebSocketClient;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Hello world!
 *
 */
public class App {
	protected static Injector createInjector() {
		return Guice.createInjector(new CoinbaseModule());
	}

	public static void main(String[] args) throws URISyntaxException {
		Injector injector = createInjector();
		MatchEntrySQLAdapter db = injector.getInstance(MatchEntrySQLAdapter.class);
		List<MatchEntryStorageManagerRunner> runners = new ArrayList<>();
		try {
			List<String> productIds = db.getProductIds();
			for (String productId : productIds) {
				MatchEntryStorageManagerRunner instance = injector.getInstance(MatchEntryStorageManagerRunner.class);
				instance.setProductId(productId);
				runners.add(instance);
				instance.start();
				System.out.println("Started loader for " + productId);
			}
		} catch (SQLException e) {
			System.err.println("Could not load product ids!");
			e.printStackTrace();
			System.exit(-1);
		}
		CoinbaseWebSocketClient wc = injector.getInstance(CoinbaseWebSocketClient.class);
		wc.connect();
		System.out.println("WebSocket started.");
	}
}
