package com.interdevinc.efiling.client.processor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.interdevinc.efiling.client.model.AuthenticatedUser;

@RemoteServiceRelativePath("authentication")
public interface AuthenticationService extends RemoteService {

    public AuthenticatedUser authenticateUser(String u, String p);

}