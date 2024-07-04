package fr.actia.teledist.evol;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class GitHubApi extends DefaultApi20 {

    protected GitHubApi() {
    }

    private static class InstanceHolder {
        private static final GitHubApi INSTANCE = new GitHubApi();
    }

    public static GitHubApi instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://github.com/login/oauth/access_token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://github.com/login/oauth/authorize";
    }
}