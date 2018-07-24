package org.beckmar.javagdax;

import org.beckmar.javagdax.storage.db.MatchEntrySQLAdapter;
import org.beckmar.javagdax.storage.db.MatchEntrySQLAdapterDummy;

public class TestModule extends CoinbaseModule {
	@Override
	protected void configure() {
		super.configure();
		bind(MatchEntrySQLAdapter.class).to(MatchEntrySQLAdapterDummy.class);
	}
}
