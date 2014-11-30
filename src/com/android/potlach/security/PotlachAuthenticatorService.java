package com.android.potlach.security;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by diyanfilipov on 10/30/14.
 */
public class PotlachAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        PotlachAuthenticator authenticator = new PotlachAuthenticator(this);
        return authenticator.getIBinder();
    }
}
