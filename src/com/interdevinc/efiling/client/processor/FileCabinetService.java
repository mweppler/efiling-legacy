package com.interdevinc.efiling.client.processor;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;

@RemoteServiceRelativePath("filecabinet")
public interface FileCabinetService extends RemoteService {
    
    public ArrayList<FileCabinet> retrieveUsableFileCabinets(AuthenticatedUser au);
    
    public FileCabinet retrieveFileCabinetContents(FileCabinet fc);
    
    public SearchComponents retrieveSearchComponents(FileCabinet fc);
    
}