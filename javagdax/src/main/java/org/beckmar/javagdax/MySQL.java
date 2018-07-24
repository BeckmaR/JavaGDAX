package org.beckmar.javagdax;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MySQL {
	private static final String BUNDLE_NAME = "org.beckmar.javagdax.MySQL"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private MySQL() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
