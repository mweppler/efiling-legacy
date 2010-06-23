package com.interdevinc.efiling.client.processor;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.interdevinc.efiling.client.model.ActivityLetter;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.ScannedDocument;

@RemoteServiceRelativePath("activityletter")
public interface ActivityLetterService extends RemoteService {

    public String addNewActivityLetter(AuthenticatedUser au, String acctNum, String dateSent);
    
    public ArrayList<ActivityLetter> retrieveClientsWithNullUpdates(AuthenticatedUser au);

    public ArrayList<ScannedDocument> retrieveScannedActivityLetterForClient(AuthenticatedUser au, String acctNum);
    
    public ArrayList<ActivityLetter> retrieveThrityDayNotReceivedStatus(AuthenticatedUser au);
    
    public ActivityLetter retrieveTwelveMonthStatus(AuthenticatedUser au, String acctNum);
    
    public String updateActivityLetter(AuthenticatedUser au, ActivityLetter al);
    
}
