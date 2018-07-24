package org.beckmar.javagdax.storage.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.beckmar.javagdax.storage.db.MatchEntrySQLAdapter;

import com.google.inject.Inject;

public class MatchEntrySQLAdapterDummy extends MatchEntrySQLAdapter {
	@Inject
	public MatchEntrySQLAdapterDummy(Connection connection) throws SQLException {
		super(connection);
	}

	@Override
	protected String table() {
		return "trades_test";
	}
}
