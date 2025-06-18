package org.sample.authenticator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *  Wrapper class for custom properties used in authenticators.
 *  Its main purpose is to allow a safe casting from an {@link Object} instance
 *  into the expected Map key/value class types.
 **/
public final class CustomAuthenticatorProperties implements Serializable {
    private static final long serialVersionUID = 12345678900002221L;

    private final HashMap<String, String> customProperties;

    public CustomAuthenticatorProperties() {
        this(null);
    }

    public CustomAuthenticatorProperties(final Map<String, String> customProperties) {
        this.customProperties = customProperties != null ? new HashMap<>(customProperties) : new HashMap<>();
    }

    public void setCustomProperty(final String key, final String value) {
        customProperties.put(key, value);
    }

    public String getCustomProperty(final String key) {
        return customProperties.get(key);
    }

    public HashMap<String, String> getCustomProperties() {
        return new HashMap<>(customProperties);
    }

    public void setCustomProperties(final Map<String, String> properties) {
        if (properties != null) {
            customProperties.putAll(properties);
        }
    }
}
