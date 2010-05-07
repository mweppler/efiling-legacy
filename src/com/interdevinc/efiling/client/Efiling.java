package com.interdevinc.efiling.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.interdevinc.efiling.client.model.AccessControl;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.view.FileCabinetSelectionView;
import com.interdevinc.efiling.client.view.LoginManager;

public class Efiling implements EntryPoint, ClickHandler, ValueChangeHandler<String> {
 
    private LoginManager loginManager;
    private FileCabinetSelectionView fileCabinetSelectionView;
    private AuthenticatedUser authenticatedUser;
    
    public void onModuleLoad() {
	initializeHistoryState();
    }
    
    /**
     * METHOD: ON HISTORY CHANGE
     * @param token - String value of the view.
     */
    public void onHistoryChange(String token){
	if (token.equals("LoginManager")) {
	    loadLoginManagerView();
	} else if (token.equals("UserAuthenticated")) {
	    loadUserDetails();
	} else if (token.equals("FileCabinetSelectionView")) {
	    loadFileCabinetSelectionView();
	}
    }
    
    /**
     * METHOD: ON VALUE CHANGE
     * @param event - 
     */
    public void onValueChange(ValueChangeEvent<String> event) {
	onHistoryChange(event.getValue());
    }
    
    /**
     * METHOD:	INIT HISTORY STATE	 */
    private void initializeHistoryState(){
	// Add the History Handler
	History.addValueChangeHandler(this);

	// Test if any tokens have been passed at startup
	String token = History.getToken();

	if(token.length() == 0){
	    onHistoryChange("LoginManager");
	} else {
	    onHistoryChange(token);
	}
    }
    
    /**
     * METHOD: LOAD FILE CABINET SELECTION VIEW
     */
    private void loadFileCabinetSelectionView() {
	RootPanel.get("main-container").clear();
	fileCabinetSelectionView = new FileCabinetSelectionView(authenticatedUser);
	RootPanel.get("main-container").add(fileCabinetSelectionView.showView());
    }
    
    /**
     * METHOD: LOAD LOGIN MANAGER VIEW
     */
    private void loadLoginManagerView() {
	RootPanel.get("main-container").clear();
	loginManager = new LoginManager();
	RootPanel.get("main-container").add(loginManager.showView());
    }

    /**
     * METHOD: LOAD USER DETAILS
     */
    private void loadUserDetails() {
	RootPanel.get("main-container").clear();
	authenticatedUser = loginManager.getAuthenticatedUser();
//	StringBuilder details = new StringBuilder("UserID: " + authenticatedUser.getUserID() + "\nUsername: " + authenticatedUser.getUsername() + "\nEmail: " + authenticatedUser.getEmailAddress());
//	for (AccessControl ac : authenticatedUser.getAccessControl()) {
//	    details = details.append("\nRole: " + ac.getRoleName() + "\tResource: " + ac.getResourceName());
//	}
//	Window.alert(details.toString());
	History.newItem("FileCabinetSelectionView");
    }
    
    @Override
    public void onClick(ClickEvent event) {
	// TODO Auto-generated method stub
	
    }
    
}
