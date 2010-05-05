package com.interdevinc.efiling.client.processor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.interdevinc.efiling.client.model.AuthenticatedUser;


public interface AuthenticateUser extends RemoteService {

    public AuthenticatedUser authenticateUser(String u, String p);

}