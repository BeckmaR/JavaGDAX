package org.beckmar.javagdax;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class CoinbaseModule extends AbstractModule {
	@Override
	protected void configure() {
		super.configure();
	}

	@Provides
	protected Connection sqlConnection() {
		String URL = MySQL.getString("Database.URL"); //$NON-NLS-1$
		String user = MySQL.getString("Database.user"); //$NON-NLS-1$
		String pw = MySQL.getString("Database.pw"); //$NON-NLS-1$
		try {
			return DriverManager.getConnection(URL, user, pw);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Provides
	protected HttpClient httpClient() {
		return HttpClients.createDefault();
	}
}
