# Custom Adaptive Script Function

This module provides a custom JavaScript function, `getCustomPropertiesFromContext`, for use in WSO2 Identity Server 
adaptive authentication scripts.

## Implementation

The function is exposed as an OSGi service when the bundle is activated. The `GetPropertyFromContextFunctionImpl` class 
implements the `GetPropertyFromContextFunction` interface and is registered as a service that the adaptive script engine 
can discover and use.

This function is designed to work in tandem with the `CustomAuthenticator`, which is responsible for collecting and 
storing runtime properties, while this function retrieves them for use within the adaptive script.

To ensure properties are available throughout a user's session, including Single Sign-On (SSO) attempts, 
the function first looks for the properties within the `AuthenticationContext`, 
which is primarily populated during the initial stages of the Identity Server's authentication flow. 
If the properties are not found there, it assumes this is an SSO login attempt and falls back to searching  
the user's session context, since it should be in cache or persisted in the database by now.
