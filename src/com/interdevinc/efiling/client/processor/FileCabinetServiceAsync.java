package com.interdevinc.efiling.client.processor;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;

public interface FileCabinetServiceAsync {
    
    public void retrieveUsableFileCabinets(AuthenticatedUser au, AsyncCallback<ArrayList<FileCabinet>> callback);
    
    public void retrieveFileCabinetContents(FileCabinet fc, AsyncCallback<FileCabinet> callback);
    
    public void retrieveSearchComponents(AsyncCallback<SearchComponents> callback);
    
}