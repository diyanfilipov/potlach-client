package com.android.potlach.security;

/**
 * Created by diyanfilipov on 10/30/14.
 */
public interface ServerAuthenticate {
    String userSignUp(final String name, final String email, final String pass, String authType) throws Exception;
    String userSignIn(final String user, final String pass, String authType) throws Exception;
}
