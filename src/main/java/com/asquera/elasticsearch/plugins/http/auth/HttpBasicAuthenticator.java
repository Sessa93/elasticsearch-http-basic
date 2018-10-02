package com.asquera.elasticsearch.plugins.http.auth;

import com.sun.tools.javac.util.StringUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestRequest;
import java.util.Base64;

public class HttpBasicAuthenticator extends Authenticator {
    private AuthCredentials credentials;

    public HttpBasicAuthenticator(Settings settings, AuthCredentials credentials) {
        super(settings);
        this.credentials = credentials;
    }

    @Override
    public boolean authenticate(RestRequest request) {
        if (this.extractCredentials(request).equals(credentials)) {
            return true;
        }
        return false;
    }

    private AuthCredentials extractCredentials(final RestRequest request) {
        final boolean forceLogin = request.paramAsBoolean("force_login", false);

        if(forceLogin) {
            return null;
        }

        final String authorizationHeader = request.header("Authorization");
        return HTTPHelper.extractCredentials(authorizationHeader);
    }

}
