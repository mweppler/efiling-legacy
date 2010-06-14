package com.interdevinc.efiling.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.Client;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.client.processor.ActivityLetterService;
import com.interdevinc.efiling.client.processor.ActivityLetterServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class ActivityLetterView implements ChangeHandler, ClickHandler {

    // Models
    private AuthenticatedUser authenticatedUser;
    private FileCabinet fileCabinet;
    private SearchComponents searchComponents;
    private InformationDialogBox idb;
    private ActivityLetterServiceAsync activityLetterAsync;

    // Panels
    private DecoratedTabPanel mainTabPanel;
    private VerticalPanel mainPanel;
    private VerticalPanel newActivityLetterPanel;
    private VerticalPanel searchActivityLetterPanel;
    private VerticalPanel updateActivityLetterPanel;
    
    // New Activity Letter Components
    private ListBox clientListbox;
    private DateBox letterSentDatebox;
    private final DateTimeFormat databaseTimeFormat = DateTimeFormat.getFormat("yyyyMMdd");
    private final DateTimeFormat dateFieldFormat = DateTimeFormat.getFormat("MMM dd yyyy");
    private Button submitNewActivityLetterButton;
    private NewActivityLetterHandler newActivityLetterHandler;
    
    // Search Activity Letter Components
    
    // Update Activity Letter Components
    
    public ActivityLetterView(AuthenticatedUser au, FileCabinet fc, VerticalPanel mp, SearchComponents sc) {
	authenticatedUser = au;
	fileCabinet = fc;
	mainPanel = mp;
	searchComponents = sc;
	idb = new InformationDialogBox();
	
	initializeInitialComponents();
	assembleInitialComponents();
	
    }
    

    @Override
    public void onChange(ChangeEvent event) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void onClick(ClickEvent event) {
	// TODO Auto-generated method stub
	if (event.getSource().equals(submitNewActivityLetterButton)) {
	    idb.messageDialogBox(1, "Submit New Activity Letter", "Account#: " + searchComponents.getClientList().get(clientListbox.getSelectedIndex() - 1).getAccountNumber() + " | Date Sent: " + databaseTimeFormat.format(letterSentDatebox.getValue()));
	}
	
    }
    
    private void assembleInitialComponents() {

	// New Activity Letter
	mainTabPanel.add(newActivityLetterPanel, "Add New Activity Letter");
	clientListbox.addItem("Select a Client");
	for (Client clientInfo : searchComponents.getClientList()) {
	    clientListbox.addItem(clientInfo.getClientFullInfo());
	}
	letterSentDatebox.setFormat(new DateBox.DefaultFormat(dateFieldFormat));
	submitNewActivityLetterButton.addClickHandler(this);
	newActivityLetterPanel.add(clientListbox);
	newActivityLetterPanel.add(letterSentDatebox);
	newActivityLetterPanel.add(submitNewActivityLetterButton);
	
	// Search Activity Letter
	mainTabPanel.add(updateActivityLetterPanel, "Update Activity Letter");
	
	// Update Activity Letter
	mainTabPanel.add(searchActivityLetterPanel, "Search Activity Letter");
	
	mainPanel.add(mainTabPanel);
	
    }
    
    private void initializeInitialComponents() {
	
	mainTabPanel = new DecoratedTabPanel();
	
	// New Activity Letter
	newActivityLetterPanel = new VerticalPanel();
	clientListbox = new ListBox();
	letterSentDatebox = new DateBox();
	submitNewActivityLetterButton = new Button("Submit");
	
	// Search Activity Letter
	searchActivityLetterPanel = new VerticalPanel();
	
	// Update Activity Letter
	updateActivityLetterPanel = new VerticalPanel();
	
	
    }
    
    /**
     * METHOD: INITIALIZE RPC WORKERS
     */
    private void initializeRpcWorkers() {

	// Define the service to call  
	activityLetterAsync = (ActivityLetterServiceAsync) GWT.create(ActivityLetterService.class); 

	// Initialize the RPC Classes.
	newActivityLetterHandler = new NewActivityLetterHandler();

    }
    
    /**
     * PRIVATE CLASS: NEW ACTIVITY LETTER HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class NewActivityLetterHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	}

	@Override
	public void onSuccess(String resultMessage) {

	}

    }
    
}
