package com.android.potlach.cloud.client;

/**
 * Created by diyanfilipov on 10/30/14.
 */
public class PotlachServerRequestResult {
    private boolean loggedIn;
    private String accessToken;

    public PotlachServerRequestResult(){ }

    public PotlachServerRequestResult(boolean loggedIn, String accessToken){
        this.setLoggedIn(loggedIn);
        this.setAccessToken(accessToken);
    }


    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
