package org.sample.authenticator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticatorFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.LogoutFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticationContextProperty;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkConstants;
import org.wso2.carbon.identity.application.authenticator.basicauth.BasicAuthenticator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Custom Authenticator that extends BasicAuthenticator.
 * This class demonstrates how to set custom properties in the AuthenticationContext
 * during various stages of the authentication flow.
 */
public class CustomAuthenticator extends BasicAuthenticator {
    private static final Log log = LogFactory.getLog(CustomAuthenticator.class);

    private static final String AUTHENTICATOR_NAME = "CustomAuthenticator";
    private static final String AUTHENTICATOR_FRIENDLY_NAME = "Custom Authenticator";

    private static AuthenticationContextProperty createContextProperty(final String key, final String value) {
        // Create a CustomAuthenticatorProperties object to hold the custom properties
        final CustomAuthenticatorProperties customProperties = new CustomAuthenticatorProperties();
        customProperties.setCustomProperty(key, value);

        final AuthenticationContextProperty customProperty = new AuthenticationContextProperty();
        customProperty.setIdPName("LOCAL");  // LOCAL since BasicAuthenticator is a local authenticator
        customProperty.setPassThroughDataType(Constants.CUSTOM_PROPERTIES);
        customProperty.setPassThroughData(customProperties);
        return customProperty;
    }

    private static void setPropertyInContextProperties(final AuthenticationContext context, final String key, final String value) {
        if (context == null) return;

        if (context.getProperty(FrameworkConstants.AUTHENTICATION_CONTEXT_PROPERTIES) == null ||
                !(context.getProperty(FrameworkConstants.AUTHENTICATION_CONTEXT_PROPERTIES) instanceof List)) {
            // Initialize the context properties if not already set
            context.setProperty(FrameworkConstants.AUTHENTICATION_CONTEXT_PROPERTIES, new ArrayList<AuthenticationContextProperty>());
        }

        // This should always be a List<AuthenticationContextProperty>. Suppressing unchecked cast warning.
        @SuppressWarnings("unchecked")
        final List<AuthenticationContextProperty> existingProperties =
                (List<AuthenticationContextProperty>) context.getProperty(FrameworkConstants.AUTHENTICATION_CONTEXT_PROPERTIES);

        final Optional<AuthenticationContextProperty> existingProperty = existingProperties.stream()
                .filter(prop -> Constants.CUSTOM_PROPERTIES.equals(prop.getPassThroughDataType()))
                .findFirst();

        // If the property already exists, update it; otherwise, create a new one
        if (existingProperty.isPresent()) {
            ((CustomAuthenticatorProperties) existingProperty.get().getPassThroughData())
                    .setCustomProperty(key, value);
        } else {
            existingProperties.add(createContextProperty(key, value));
        }
    }

    @Override
    public AuthenticatorFlowStatus process(final HttpServletRequest request,
                                           final HttpServletResponse response,
                                           final AuthenticationContext context)
            throws AuthenticationFailedException, LogoutFailedException {
        // called when the user is redirected to the authenticator (e.g. gets to login page, submits credentials)
        setPropertyInContextProperties(context, "PROCESS_KEY", "VALUE");
        return super.process(request, response, context);
    }

    @Override
    protected AuthenticatorFlowStatus executeAutoLoginFlow(final HttpServletRequest request,
                                                           final HttpServletResponse response,
                                                           final AuthenticationContext context,
                                                           final Cookie autoLoginCookie)
            throws AuthenticationFailedException {
        // called when the user has the auto-login cookie set
        setPropertyInContextProperties(context, "EXECUTE_ATLF_KEY", "VALUE");
        return super.executeAutoLoginFlow(request, response, context, autoLoginCookie);
    }

    @Override
    protected void initiateAuthenticationRequest(final HttpServletRequest request,
                                                 final HttpServletResponse response,
                                                 final AuthenticationContext context)
            throws AuthenticationFailedException {
        // called when the user is initiates the authentication request (i.e. gets to login page)
        setPropertyInContextProperties(context, "INITIATE_AUTHREQ_KEY", "VALUE");
        super.initiateAuthenticationRequest(request, response, context);
    }

    @Override
    protected void processAuthenticationResponse(final HttpServletRequest request,
                                                 final HttpServletResponse response,
                                                 final AuthenticationContext context)
            throws AuthenticationFailedException {
        // called when the user responds to the authentication request (i.e. submits credentials)
        setPropertyInContextProperties(context, "PROCESS_AUTHRES_KEY", "VALUE");
        super.processAuthenticationResponse(request, response, context);
    }

    @Override
    public String getName() {
        return AUTHENTICATOR_NAME;
    }

    @Override
    public String getFriendlyName() {
        return AUTHENTICATOR_FRIENDLY_NAME;
    }

    public static final class Constants {
        public static final String CUSTOM_PROPERTIES = "CustomAuthenticator:CUSTOM_PROPERTIES";

        private Constants() {
            // Prevent instantiation
        }
    }
}
