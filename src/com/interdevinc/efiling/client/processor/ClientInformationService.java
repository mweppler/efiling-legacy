package com.interdevinc.efiling.client.processor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.interdevinc.efiling.client.model.FileCabinet;

@RemoteServiceRelativePath("clientinformation")
public interface ClientInformationService extends RemoteService {
    
    public String addClientInformation(FileCabinet fc, String clientFirstName, String clientLastName, String clientAccountNumber, String clientRepNumber);
    
    public String deleteClientInformation(FileCabinet fc, String clientID);
    
    public String editClientInformation(FileCabinet fc, String clientFirstName, String clientLastName, String clientAccountNumber, String clientRepNumber, String clientID);

}
