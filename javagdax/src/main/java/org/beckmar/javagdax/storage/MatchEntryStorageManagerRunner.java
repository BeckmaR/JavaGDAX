package org.beckmar.javagdax.storage;

import java.io.IOException;
import java.sql.SQLException;

import com.google.inject.Inject;

public class MatchEntryStorageManagerRunner extends Thread {
	@Inject
	protected MatchEntryStorageManager manager;

	protected String productId;

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public void run() {
		if (productId == null) {
			throw new IllegalStateException("Product ID must not be null!");
		}
		try {
			while (true) {
				manager.findHoles(productId);
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
