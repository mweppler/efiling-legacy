package com.interdevinc.efiling.client.processor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.interdevinc.efiling.client.model.AuthenticatedUser;


public interface AuthenticateUserAsync {

    public void authenticateUser(String u, String p, AsyncCallback<AuthenticatedUser> callback);

}
