package org.mule.modules.instagram;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.util.List;

import org.mule.api.annotations.lifecycle.Start;
import org.mule.modules.instagram.config.ConnectorConfig;

@Connector(name = "instagram", schemaVersion = "2.4", description = "Instagram Integration", friendlyName = "Instagram", minMuleVersion = "3.6")
public class InstagramConnector {
    @NotNull
    @ConnectionStrategy
    ConnectorConfig config;

    // private MuleCookBookClient client;
    private Instagram4j instagram;

    @Start
    public void initialize() {
        // client = new MuleCookBookClient(config.getAddress());
    }

    @Processor
    public InstagramSearchUsernameResult searchProfileByUserName(String userName)
            throws ClientProtocolException, IOException {
        InstagramSearchUsernameResult userResult = instagram.sendRequest(new InstagramSearchUsernameRequest(userName));
        return userResult;
    }

    @Processor
    public List<InstagramUserSummary> getFollowersByUserName(String userName)
            throws ClientProtocolException, IOException {
        InstagramSearchUsernameResult userResult = instagram.sendRequest(new InstagramSearchUsernameRequest(userName));
        InstagramGetUserFollowersResult githubFollowers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
        return  githubFollowers.getUsers();
    }

    @Processor
    public List<InstagramFeedItem> getFeedForHashtagByUserName(String userName) 
        throws ClientProtocolException, IOException {
            InstagramFeedResult tagFeed = instagram.sendRequest(new InstagramTagFeedRequest(userName));
            return tagFeed.getItems();
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
        this.instagram = config.getInstagram();
    }

}