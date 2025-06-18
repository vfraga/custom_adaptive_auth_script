package org.sample.adaptive.script.functions.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;
import org.wso2.carbon.user.core.service.RealmService;

public final class ServiceHolder {
    private static final ServiceHolder instance = new ServiceHolder();
    private static final Log log = LogFactory.getLog(ServiceHolder.class);

    private JsFunctionRegistry jsFunctionRegistry;
    private RealmService realmService;

    private ServiceHolder() {
    }

    public static ServiceHolder getInstance() {
        return instance;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    public void setJsFunctionRegistry(final JsFunctionRegistry jsFunctionRegistry) {
        this.jsFunctionRegistry = jsFunctionRegistry;
    }

    public JsFunctionRegistry getJsFunctionRegistry() {
        return jsFunctionRegistry;
    }
}
