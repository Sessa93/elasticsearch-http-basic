package com.asquera.elasticsearch.plugins.http;

import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * @author Andrea Sessa (andrea.sessa@cleafy.com)
 */
public class HttpBasicServerPlugin extends Plugin implements ActionPlugin {

    private boolean enabledByDefault = true;
    private final Settings settings;
    BasicRestFilter basicFilter;

    @Inject
    public HttpBasicServerPlugin(Settings settings) {
        this.settings = settings;
        this.basicFilter = new BasicRestFilter(this.settings);
    }

    public String name() {
        return "http-basic-server-plugin";
    }
    public String description() {
        return "HTTP Basic Server Plugin";
    }

    public Collection<Class<? extends Module>> modules() {
        Collection<Class<? extends Module>> modules = new ArrayList<>();
        if (settings.getAsBoolean("http.basic.enabled", enabledByDefault)) {
            //modules.add(HttpBasicServerModule.class);
        }
        return modules;
    }

    public Collection<Class<? extends LifecycleComponent>> services() {
        Collection<Class<? extends LifecycleComponent>> services = new ArrayList<>();
        if (settings.getAsBoolean("http.basic.enabled", enabledByDefault)) {
            //services.add(HttpBasicServer.class);
        }
        return services;
    }

    public Settings additionalSettings() {
        if (settings.getAsBoolean("http.basic.enabled", enabledByDefault)) {
            return Settings.builder().
                    put("http.enabled", false).
                    build();
        } else {
            return Settings.Builder.EMPTY_SETTINGS;
        }
    }

    @Override
    public UnaryOperator<RestHandler> getRestHandlerWrapper(final ThreadContext threadContext) {
        return (rh) -> basicFilter.wrap(rh);
    }
}