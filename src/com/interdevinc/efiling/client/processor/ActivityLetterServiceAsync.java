package com.interdevinc.efiling.client.processor;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.interdevinc.efiling.client.model.ActivityLetter;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.ScannedDocument;

public interface ActivityLetterServiceAsync {

    public void addNewActivityLetter(AuthenticatedUser au, String acctNum, String dateSent, AsyncCallback<String> callback);
    
    public void retrieveClientsWithNullUpdates(AuthenticatedUser au, AsyncCallback<ArrayList<ActivityLetter>> callback);

    public void retrieveScannedActivityLetterForClient(AuthenticatedUser au, String acctNum, AsyncCallback<ArrayList<ScannedDocument>> callback);
    
    public void updateActivityLetter(AuthenticatedUser au, ActivityLetter al, AsyncCallback<String> callback);
    
}
