package com.interdevinc.efiling.client.processor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.interdevinc.efiling.client.model.FileCabinet;

public interface ClientInformationServiceAsync {
    
    public void addClientInformation(FileCabinet fc, String clientFirstName, String clientLastName, String clientAccountNumber, String clientRepNumber, AsyncCallback<String> callback);

    public void deleteClientInformation(FileCabinet fc, String clientID, AsyncCallback<String> callback);
    
    public void editClientInformation(FileCabinet fc, String clientFirstName, String clientLastName, String clientAccountNumber, String clientRepNumber, String clientID, AsyncCallback<String> callback);
    
}
