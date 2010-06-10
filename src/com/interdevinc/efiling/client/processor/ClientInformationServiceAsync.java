package com.interdevinc.efiling.client.processor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;

public interface ClientInformationServiceAsync {
    
    public void addClientInformation(AuthenticatedUser au, FileCabinet fc, String clientFirstName, String clientLastName, String clientAccountNumber, String clientRepNumber, AsyncCallback<String> callback);

    public void deleteClientInformation(AuthenticatedUser au, FileCabinet fc, String clientID, AsyncCallback<String> callback);
    
    public void editClientInformation(AuthenticatedUser au, FileCabinet fc, String clientFirstName, String clientLastName, String clientAccountNumber, String clientRepNumber, String clientID, AsyncCallback<String> callback);
    
}
