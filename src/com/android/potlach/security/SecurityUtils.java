package com.android.potlach.security;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.potlach.ui.DisplayableMessageAxtivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diyanfilipov on 11/1/14.
 */
public final class SecurityUtils {
    private static final String TAG = SecurityUtils.class.getSimpleName();

    public static void checkAuthentication(
            final AccountManager mAccountManager,
            final AuthenticationAction action,
            final Activity activity,
            final DisplayableMessageAxtivity displayableMessageAxtivity) {
        final Account availableAccounts[] = mAccountManager.getAccountsByType(GeneralPotlachAccount.ACCOUNT_TYPE);
        if (availableAccounts.length == 0) {
            final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(
                    GeneralPotlachAccount.ACCOUNT_TYPE,
                    GeneralPotlachAccount.AUTH_TOKEN_TYPE_FULL_ACCESS, null, null, activity, new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            try {
                                Bundle bnd = future.getResult();
                                Log.d(TAG, "AddNewAccount Bundle is " + bnd);
                                displayableMessageAxtivity.showMessage("Account was created");
                                action.execute(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                                displayableMessageAxtivity.showMessage(e.getMessage());
                            }
                        }
                    }, null);
        } else if(availableAccounts.length == 1) {
//            getTokenForAccountCreateIfNeeded(
//                    mAccountManager,
//                    action,
//                    activity,
//                    displayableMessageAxtivity);
            getExistingAccountAuthToken(availableAccounts[0],
                    mAccountManager,
                    action,
                    activity,
                    displayableMessageAxtivity);
        }
//        else{
//
//            String name[] = new String[availableAccounts.length];
//
//            for (int i = 0; i < availableAccounts.length; i++) {
//                name[i] = availableAccounts[i].name;
//            }
//            // Account picker
//            AlertDialog mAlertDialog = new AlertDialog.Builder(activity).setTitle("Pick Account")
//                    .setAdapter(
//                            new ArrayAdapter<String>(activity.getBaseContext(),
//                                    android.R.layout.simple_list_item_1, name),
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
////                                    getExistingAccountAuthToken(availableAccounts[which],
////                                            mAccountManager,
////                                            action,
////                                            activity);
//                                    getTokenForAccountCreateIfNeeded(
//                                            mAccountManager,
//                                            action,
//                                            activity);
//                                }
//                            }).create();
//            mAlertDialog.show();
//        }
    }

    /**
     * Get an auth token for the account.
     * If not exist - add it and then return its auth token.
     * If one exist - return its auth token.
     * If more than one exists - show a picker and return the select account's auth token.
     */
    public static void getTokenForAccountCreateIfNeeded(AccountManager mAccountManager,
                                                        final AuthenticationAction action,
                                                        final Activity activity,
                                                        final DisplayableMessageAxtivity displayableMessageAxtivity) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(
                GeneralPotlachAccount.ACCOUNT_TYPE,
                GeneralPotlachAccount.AUTH_TOKEN_TYPE_FULL_ACCESS, null, activity, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            //activity.showMessage(((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));

                            if (!TextUtils.isEmpty(authtoken)) {
                                action.execute(true);
                            } else {
                                action.execute(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            displayableMessageAxtivity.showMessage(e.getMessage());
                        }
                    }
                }
                , null);
    }

    public static void getExistingAccountAuthToken(Account account,
                                                   AccountManager mAccountManager,
                                                   final AuthenticationAction action,
                                                   final Activity activity,
                                                   final DisplayableMessageAxtivity displayableMessageAxtivity) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account,
                GeneralPotlachAccount.AUTH_TOKEN_TYPE_FULL_ACCESS, null, activity, null, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                    showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");


                    if (!TextUtils.isEmpty(authtoken)) {
                        action.execute(true);
                    } else {
                        action.execute(false);
                    }


                    Log.d(TAG, "AuthToken is " + authtoken);
                    Log.d(TAG, "GetTokenForAccount Bundle is " + bnd);
                } catch (Exception e) {
                    e.printStackTrace();
                    displayableMessageAxtivity.showMessage(e.getMessage());
                }
            }
        }).start();
    }

    public static void invalidateAuthentication(final AccountManager mAccountManager,
                                                final Account account,
                                                final AuthenticationAction action,
                                                final Activity activity,
                                                final DisplayableMessageAxtivity displayableMessageAxtivity){
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account,
                GeneralPotlachAccount.AUTH_TOKEN_TYPE_FULL_ACCESS, null, activity, null,null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.invalidateAuthToken(account.type, authtoken);
                    displayableMessageAxtivity.showMessage(account.name + " signed out successfully.");

                    action.execute(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    displayableMessageAxtivity.showMessage(e.getMessage());
                }
            }
        }).start();
    }

    public static List<String> getAccountInfo(Activity activity){
        AccountManager accountManager = AccountManager.get(activity);
        final Account availableAccounts[] = accountManager.getAccountsByType(GeneralPotlachAccount.ACCOUNT_TYPE);
        if(availableAccounts.length == 1) {
            Account account = availableAccounts[0];
            List<String> info = new ArrayList<String>();
            info.add(account.name);
            info.add(accountManager.getPassword(account));
            return info;
        }
        return null;
    }
}
