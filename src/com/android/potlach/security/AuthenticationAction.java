package com.android.potlach.security;

import android.accounts.AccountManagerFuture;
import android.os.Bundle;

/**
 * Created by diyanfilipov on 11/1/14.
 */
public interface AuthenticationAction {
    void execute(boolean valid);
}
