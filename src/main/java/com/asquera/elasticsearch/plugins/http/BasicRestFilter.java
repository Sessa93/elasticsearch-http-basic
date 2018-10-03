package com.asquera.elasticsearch.plugins.http;

import com.asquera.elasticsearch.plugins.http.auth.AuthCredentials;
import com.asquera.elasticsearch.plugins.http.auth.HttpBasicAuthenticator;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;
import org.elasticsearch.transport.TransportException;

public class BasicRestFilter {
    private final Settings settings;
    private final HttpBasicAuthenticator httpBasicAuthenticator;

    public BasicRestFilter(final Settings settings) {
        super();
        this.settings = settings;
        this.httpBasicAuthenticator = new HttpBasicAuthenticator(this.settings, new AuthCredentials(settings.get("http.basic.username", "pippo"), settings.get("http.basic.password", "pippo").getBytes()));
    }

    public RestHandler wrap(RestHandler original) {
        return (request, channel, client) -> {
            if (!checkAndAuthenticateRequest(request, channel, client)) {
                original.handleRequest(request, channel, client);
            }
        };
    }

    private boolean checkAndAuthenticateRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception {
        ElasticsearchException forbiddenException = new TransportException("Forbidden");
        try {
            if (this.httpBasicAuthenticator.authenticate(request)) {
                return false;
            }
            channel.sendResponse(new BytesRestResponse(channel, RestStatus.FORBIDDEN, forbiddenException));
        } catch (Exception e) {
            channel.sendResponse(new BytesRestResponse(channel, RestStatus.FORBIDDEN, forbiddenException));
            return true;
        }
        return true;
    }
}
