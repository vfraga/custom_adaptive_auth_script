package org.sample.adaptive.script.functions;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;

import java.util.Map;

@FunctionalInterface
public interface GetPropertyFromContextFunction {
    Map<String, String> getCustomPropertiesFromContext(final JsAuthenticationContext context);
}
