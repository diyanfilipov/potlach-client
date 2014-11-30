package com.android.potlach.security;

import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachServerRequestResult;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.cloud.client.SecuredRestBuilder;

import retrofit.RestAdapter;

/**
 * Created by diyanfilipov on 10/30/14.
 */
public class PotlachServerAuthenticate implements ServerAuthenticate {
    public static final String TAG = PotlachServerAuthenticate.class.getSimpleName();


    @Override
    public String userSignUp(String name, String email, String pass, String authType) throws Exception {
        PotlachErrorHandler errorHandler = new PotlachErrorHandler();

        try {
            ClientUtils.createReadPotlachApi(errorHandler).register(name, pass);
        } catch (Exception e) { //probably the user already exists
            Log.d(TAG, "Error occured while signing in: " + e.getMessage());
            throw new Exception("User already exists!");
        }
        return null;
    }
    @Override
    public String userSignIn(String user, String pass, String authType) throws Exception {
        Log.d(TAG, "userSignIn");

        PotlachServerRequestResult requestResult = new PotlachServerRequestResult();


        PotlachSvcApi potlachSvcApi = new SecuredRestBuilder()
                .setClient(ClientUtils.getClient())
                .setEndpoint(ClientUtils.TEST_URL)
                .setLoginEndpoint(ClientUtils.TEST_URL + PotlachSvcApi.TOKEN_PATH)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setUsername(user)
                .setPassword(pass)
                .setClientId(ClientUtils.CLIENT_ID)
                .setOAuthRequestResult(requestResult)
                .build()
                .create(PotlachSvcApi.class);

        try {
            potlachSvcApi.signIn();

            if(requestResult.isLoggedIn()){
                return requestResult.getAccessToken();
            }
        }catch (Exception e){
            Log.d(TAG, "Error occured while signing in: " + e.getMessage());
            throw e;
        }

        return null;
    }
}
