package com.android.potlach.cloud.client;

import retrofit.RestAdapter;
import retrofit.client.ApacheClient;
import retrofit.client.Client;

/**
 * Created by diyanfilipov on 10/28/14.
 */
public final class ClientUtils {
    public static final String TEST_URL = "https://10.0.2.2:8443";
    public static final String CLIENT_ID = "mobile";
    private static final Client client = new ApacheClient(UnsafeHttpsClient.createUnsafeClient());

    public static Client getClient(){
        return new ApacheClient(UnsafeHttpsClient.createUnsafeClient());
    }

    public static PotlachSvcApi createReadPotlachApi(PotlachErrorHandler errorHandler){
        return new RestAdapter.Builder()
                .setClient(ClientUtils.getClient())
                .setEndpoint(ClientUtils.TEST_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(errorHandler)
                .build()
                .create(PotlachSvcApi.class);
    }

    public static PotlachSvcApi createWritePolachApi(String username, String password, String clientId){
        return new SecuredRestBuilder()
                .setClient(ClientUtils.getClient())
                .setEndpoint(ClientUtils.TEST_URL)
                .setLoginEndpoint(ClientUtils.TEST_URL + PotlachSvcApi.TOKEN_PATH)
                        // .setLogLevel(LogLevel.FULL)
                .setUsername(username).setPassword(password).setClientId(clientId)
                .build().create(PotlachSvcApi.class);
    }
}
