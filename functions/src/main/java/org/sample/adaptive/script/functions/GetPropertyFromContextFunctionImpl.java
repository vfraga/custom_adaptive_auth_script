package org.sample.adaptive.script.functions;

import org.sample.authenticator.CustomAuthenticator;
import org.sample.authenticator.CustomAuthenticatorProperties;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.context.SessionContext;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticationContextProperty;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkConstants;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GetPropertyFromContextFunctionImpl implements GetPropertyFromContextFunction {

    @Override
    public Map<String, String> getCustomPropertiesFromContext(final JsAuthenticationContext jsAuthenticationContext) {
        if (jsAuthenticationContext != null) {
            final AuthenticationContext javaAuthContext = jsAuthenticationContext.getWrapped();

            final Optional<CustomAuthenticatorProperties> customAuthenticatorProperties = getCustomAuthenticatorProperties(javaAuthContext);

            if (customAuthenticatorProperties.isPresent()) {
                return customAuthenticatorProperties.get().getCustomProperties();
            }
        }

        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private static Optional<CustomAuthenticatorProperties> getCustomAuthenticatorProperties(final AuthenticationContext authenticationContext) {
        if (authenticationContext == null) return Optional.empty();

        final List<AuthenticationContextProperty> existingProperties;

        if (authenticationContext.getProperty(FrameworkConstants.AUTHENTICATION_CONTEXT_PROPERTIES) instanceof List) {
            // If the properties are set in the authentication context, we use them directly
            existingProperties = (List<AuthenticationContextProperty>)
                    authenticationContext.getProperty(FrameworkConstants.AUTHENTICATION_CONTEXT_PROPERTIES);
        } else {
            // If the properties are not set in the authentication context, we check the session context
            final SessionContext sessionContext = FrameworkUtils.getSessionContextFromCache(authenticationContext.getSessionIdentifier());

            if (sessionContext != null && sessionContext.getProperty(FrameworkConstants.AUTHENTICATION_CONTEXT_PROPERTIES) instanceof List) {
                // If the properties are not set in the context, we check the session context
                existingProperties = (List<AuthenticationContextProperty>) sessionContext.getProperty(FrameworkConstants.AUTHENTICATION_CONTEXT_PROPERTIES);
            } else {
                // If neither context has properties, return empty
                return Optional.empty();
            }
        }

        final Optional<AuthenticationContextProperty> existingProperty = existingProperties.stream()
                .filter(prop -> CustomAuthenticator.Constants.CUSTOM_PROPERTIES.equals(prop.getPassThroughDataType()))
                .findFirst();

        if (existingProperty.isPresent()) {
            // If the property exists, we return it as a CustomAuthenticatorProperties instance
            final AuthenticationContextProperty prop = existingProperty.get();
            if (prop.getPassThroughData() instanceof CustomAuthenticatorProperties) {
                return Optional.of((CustomAuthenticatorProperties) prop.getPassThroughData());
            }
        }

        return Optional.empty();
    }
}
