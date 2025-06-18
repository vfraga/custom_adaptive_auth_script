# Custom Properties Authenticator

This module contains a custom authenticator designed to capture and persist runtime properties during the authentication flow.

## Problem

A challenge in adaptive authentication flows is the transient nature of runtime data. By default, the Identity Server exposes 
only certain attributes of the `AuthenticationContext` in the adaptive script, and it doesn't allow fetching properties from it
in the adaptive script. 
Moreover, any custom properties an authenticator sets directly on the `AuthenticationContext` object are only available during the initial login attempt. 
They are not persisted across subsequent Single Sign-On (SSO) sessions, which means that any decision-making logic in 
an adaptive script that would rely on these properties would fail during SSO.

Similarly, runtime claims added to the `AuthenticationContext` are also not persisted. Each SSO attempt effectively 
resets these claims, making them unreliable for session-spanning logic.

## Solution

To overcome this limitation, this authenticator uses a more robust method for persisting properties that is also used by the
(default SAML authenticator)[https://github.com/wso2-extensions/identity-outbound-auth-samlsso/blob/v5.9.8/components/org.wso2.carbon.identity.application.authenticator.samlsso/src/main/java/org/wso2/carbon/identity/application/authenticator/samlsso/SAMLSSOAuthenticator.java#L402-L417]. 
Instead of using any `String` key on the `AuthenticationContext` (e.g. `CUSTOM_PROPERTIES`), 
it stores a `List` of `AuthenticationContextProperty` objects under the `AUTHENTICATION_CONTEXT_PROPERTY` key.

The authentication framework then (recognises this specific key and persists this list into the user's session context)[https://github.com/wso2/carbon-identity-framework/blob/v7.0.78/components/authentication-framework/org.wso2.carbon.identity.application.authentication.framework/src/main/java/org/wso2/carbon/identity/application/authentication/framework/handler/request/impl/DefaultAuthenticationRequestHandler.java#L503-L598].  
By doing so, the custom properties become available not only during the initial login but also throughout the entire SSO session.

This approach requires the corresponding JavaScript function (in the function library module) to be aware of this 
mechanism. The function should be implemented to first check the `AuthenticationContext` for the properties and, 
if not found, fall back to checking the session context. 
