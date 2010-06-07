package com.interdevinc.efiling.client.view;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;

public class EditClientInfo {

    private AuthenticatedUser authenticatedUser;
    private FileCabinet fileCabinet;
    private SearchComponents searchComponents;
    
    // Panels
    private VerticalPanel mainPanel;
    
    public EditClientInfo(AuthenticatedUser au, FileCabinet fc, VerticalPanel mp, SearchComponents sc) {
	authenticatedUser = au;
	fileCabinet = fc;
	mainPanel = mp;
	searchComponents = sc;
	
	
    }
    
}
