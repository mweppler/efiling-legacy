package com.interdevinc.efiling.client.processor;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.ScannedDocument;
import com.interdevinc.efiling.client.model.SearchComponents;

public interface FileCabinetServiceAsync {
    
    public void retrieveUsableFileCabinets(AuthenticatedUser au, AsyncCallback<ArrayList<FileCabinet>> callback);
    
    public void retrieveFileCabinetContents(FileCabinet fc, AsyncCallback<FileCabinet> callback);
    
    public void retrieveSearchComponents(FileCabinet fc, AsyncCallback<SearchComponents> callback);
    
    public void retrieveSearchResults(FileCabinet fc, String n, String d, AsyncCallback<ArrayList<ScannedDocument>> callback);
    
    public void addDocumentType(FileCabinet fc, String documentTypeName, String documentTypeAbbr, AsyncCallback<String> callback);
    
    public void deleteDocumentType(FileCabinet fc, String documentTypeAbbr, AsyncCallback<String> callback);
    
    public void editDocumentType(FileCabinet fc, String documentTypeName, String documentTypeAbbr, String documentTypeAbbrOld, AsyncCallback<String> callback);
    
}