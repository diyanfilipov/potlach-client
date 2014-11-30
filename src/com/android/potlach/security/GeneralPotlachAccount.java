package com.android.potlach.security;

/**
 * Created by diyanfilipov on 10/30/14.
 */
public class GeneralPotlachAccount {
    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "com.diyanfilipov.potlach";
    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "Potlach";
    /**
     * Auth token types
     */
    public static final String AUTH_TOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTH_TOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Potlach account";
    public static final String AUTH_TOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTH_TOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Potlach account";

    public static final ServerAuthenticate sServerAuthenticate = new PotlachServerAuthenticate();
}
