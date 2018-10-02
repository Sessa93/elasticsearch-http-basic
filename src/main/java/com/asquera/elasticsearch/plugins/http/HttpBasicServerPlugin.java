package com.asquera.elasticsearch.plugins.http;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestHandler;

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

    @Override
    public UnaryOperator<RestHandler> getRestHandlerWrapper(final ThreadContext threadContext) {
        if (this.settings.getAsBoolean("http.basic.enabled", false)) {
            return (rh) -> basicFilter.wrap(rh);
        }
        return (rh) -> rh;
    }
}