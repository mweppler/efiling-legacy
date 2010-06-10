package com.interdevinc.efiling.client.processor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;

@RemoteServiceRelativePath("clientinformation")
public interface ClientInformationService extends RemoteService {
    
    public String addClientInformation(AuthenticatedUser au, FileCabinet fc, String clientFirstName, String clientLastName, String clientAccountNumber, String clientRepNumber);
    
    public String deleteClientInformation(AuthenticatedUser au, FileCabinet fc, String clientID);
    
    public String editClientInformation(AuthenticatedUser au, FileCabinet fc, String clientFirstName, String clientLastName, String clientAccountNumber, String clientRepNumber, String clientID);

}
