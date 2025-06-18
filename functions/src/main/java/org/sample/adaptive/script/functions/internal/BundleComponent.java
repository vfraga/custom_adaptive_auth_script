package org.sample.adaptive.script.functions.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.sample.adaptive.script.functions.GetPropertyFromContextFunctionImpl;
import org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticationService;
import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;
import org.wso2.carbon.identity.core.util.IdentityCoreInitializedEvent;
import org.wso2.carbon.user.core.service.RealmService;

@Component(
        name = "custom.adaptive.script.functions.bundle",
        immediate = true)
public class BundleComponent {
    private static final Log log = LogFactory.getLog(BundleComponent.class);

    private static void registerService(final String functionName, final Object functionInstance) {
        try {
            final JsFunctionRegistry jsFunctionRegistry = ServiceHolder.getInstance().getJsFunctionRegistry();

            if (jsFunctionRegistry != null) {
                jsFunctionRegistry.register(
                        JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER,
                        functionName,
                        functionInstance
                );
                log.info(String.format(
                        "Successfully registered function '%s' with an '%s' instance.",
                        functionName,
                        functionInstance.getClass().getCanonicalName()
                ));
            } else {
                log.error("JsFunctionRegistry is not available. Cannot register custom function.");
            }
        } catch (Exception e) {
            log.error(String.format(
                    "Error registering function '%s' with an '%s' instance. Service registration failed.",
                    functionName,
                    functionInstance.getClass().getCanonicalName()
            ), e);
        }
    }

    @Activate
    protected void activate(final ComponentContext context) {
        registerService("getCustomPropertiesFromContext", new GetPropertyFromContextFunctionImpl());

        log.info("Custom bundle activated.");
    }

    @Deactivate
    protected void deactivate(final ComponentContext ignored) {
        log.info("Custom bundle deactivated.");
    }

    @Reference(
            name = "RealmService",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(final RealmService realmService) {
        log.debug("Setting the Realm Service.");
        ServiceHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(final RealmService realmService) {
        log.debug("Unsetting the Realm Service.");
        ServiceHolder.getInstance().setRealmService(null);
    }

    @Reference(
            name = "identity.application.authentication.framework",
            service = ApplicationAuthenticationService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetApplicationAuthenticationService"
    )
    protected void setApplicationAuthenticationService(final ApplicationAuthenticationService ignored) {
        // do nothing: waiting for ApplicationAuthenticationService to initialise
    }

    protected void unsetApplicationAuthenticationService(final ApplicationAuthenticationService ignored) {
        // do nothing: method declaration for the unbind action for setApplicationAuthenticationService
    }

    @Reference(
            name = "identity.core.init.event.service",
            service = IdentityCoreInitializedEvent.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetIdentityCoreInitializedEventService"
    )
    protected void setIdentityCoreInitializedEventService(final IdentityCoreInitializedEvent ignored) {
        // do nothing: waiting for IdentityCoreInitializedEvent to initialise
    }

    protected void unsetIdentityCoreInitializedEventService(final IdentityCoreInitializedEvent ignored) {
        // do nothing: method declaration for the unbind action for setIdentityCoreInitializedEventService
    }

    @Reference(
            service = JsFunctionRegistry.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetJsFunctionRegistry"
    )
    public void setJsFunctionRegistry(final JsFunctionRegistry jsFunctionRegistry) {
        log.debug("Setting the JsFunctionRegistry.");
        ServiceHolder.getInstance().setJsFunctionRegistry(jsFunctionRegistry);
    }

    public void unsetJsFunctionRegistry(final JsFunctionRegistry ignored) {
        log.debug("Unsetting the JsFunctionRegistry.");
        ServiceHolder.getInstance().setJsFunctionRegistry(null);
    }
}
