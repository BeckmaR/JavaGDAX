package org.beckmar.javagdax.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.inject.Inject;

public class AbstractGdaxRestService {
	public static final String COINBASE_REST_API = "https://api.pro.coinbase.com/";

	protected CloseableHttpResponse currentResponse;

	@Inject
	protected HttpClient client;

	protected JSONArray getRESTArray(String endPoint) throws IOException {
		JSONArray jsonArray = getJSONArray(getTokener(doRequest(endPoint)));
		currentResponse.close();
		return jsonArray;
	}

	protected JSONObject getRESTObject(String endPoint) throws IOException {
		JSONObject jsonObject = getJSONObject(getTokener(doRequest(endPoint)));
		currentResponse.close();
		return jsonObject;
	}

	protected InputStream doRequest(String endPoint) throws IOException {
		HttpGet get = new HttpGet(getURI(endPoint));
		CloseableHttpResponse res = null;
		try {
			currentResponse = (CloseableHttpResponse) client.execute(get);
			HttpEntity entity = currentResponse.getEntity();
			if (entity != null) {
				InputStream content = entity.getContent();
				return content;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	protected JSONTokener getTokener(InputStream stream) throws IOException {
		JSONTokener jsonTokener = new JSONTokener(stream);
		return jsonTokener;
	}

	protected JSONArray getJSONArray(JSONTokener tokener) {
		return new JSONArray(tokener);
	}

	protected JSONObject getJSONObject(JSONTokener tokener) {
		return new JSONObject(tokener);
	}

	protected URI getURI(String endpoint) {
		URI uri = null;
		try {
			uri = new URI(COINBASE_REST_API + endpoint);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

}
