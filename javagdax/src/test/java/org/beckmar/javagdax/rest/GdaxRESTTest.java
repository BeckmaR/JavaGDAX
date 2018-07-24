package org.beckmar.javagdax.rest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.beckmar.javagdax.AbstractTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.google.inject.Inject;

public class GdaxRESTTest extends AbstractTest {
	@Inject
	AbstractGdaxRestService service;

	@Test
	public void testGetProducts() throws IOException {
		JSONArray restObject = service.getRESTArray("products");
	}

	@Test
	public void testProductsContainBTC() throws IOException {
		JSONArray arr = service.getRESTArray("products");
		int i = 0;
		boolean found = false;
		while (!arr.isNull(i++)) {
			JSONObject o = arr.getJSONObject(i);
			o.getString("id");
			if (o.getString("id").equals("BTC-EUR")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
}
