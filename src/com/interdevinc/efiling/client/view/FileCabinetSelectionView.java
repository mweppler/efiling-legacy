package com.interdevinc.efiling.client.view;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.processor.FileCabinetService;
import com.interdevinc.efiling.client.processor.FileCabinetServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class FileCabinetSelectionView {

    private VerticalPanel mainPanel;
    private FileCabinetServiceAsync fileCabinetAsync;
    private FileCabinetSelectionHandler fileCabinetSelectionHandler;
    private AuthenticatedUser authenticatedUser;
    private InformationDialogBox idb;
    private ArrayList<FileCabinet> fileCabinets;
    private FileCabinetView fileCabinetView;
    
    /**
     * CONSTRUCTOR: FILE CABINET SELECTION VIEW
     * @param au
     */
    public FileCabinetSelectionView(AuthenticatedUser au) {
	authenticatedUser = au;
	idb = new InformationDialogBox();
	idb.loadingDialogBox("File Cabinet Selection.");
	initializeComponents();
	doFileCabinetSelectionRpc();
    }
    
    /**
     * METHOD: SHOW VIEW
     * @return mainPanel
     * Returns the mainPanel 
     */
    public Widget showView() {
	return mainPanel;
    }

    /**
     * METHOD: DO FILE CABINET SELECTION RPC
     */
    private void doFileCabinetSelectionRpc() {
	//execute authentication procedure
	fileCabinetAsync.retrieveUsableFileCabinets(authenticatedUser, fileCabinetSelectionHandler);
    }

    /**
     * METHOD: INITIALIZE COMPONENTS
     */
    private void initializeComponents() {

	// Define the service to call
	fileCabinetAsync = (FileCabinetServiceAsync) GWT.create(FileCabinetService.class);

	// initialize the file cabinet handler
	fileCabinetSelectionHandler = new FileCabinetSelectionHandler();

	mainPanel = new VerticalPanel();

    }
    
    /**
     * METHOD: LOAD FILE CABINET VIEW
     * Loads the file cabinet view.
     */
    private void loadFileCabinetView(FileCabinet fileCabinet) {
	RootPanel.get("main-container").clear();
	fileCabinetView = new FileCabinetView(authenticatedUser, fileCabinet);
	RootPanel.get("main-container").add(fileCabinetView.showView());
    }
    
    /**
     * METHOD: SHOW FILE CABINETS
     * Builds a table of file cabinets available to the authenticated user.
     */
    private void showFileCabinets() {
	
	mainPanel.add(new Label("Welcome, " + authenticatedUser.getUsername() + ".\nPlease choose a file cabinet to begin."));
	
	FlexTable fileCabinetSelectionTable = new FlexTable();
	
	int row = 0; // First row in table.
	for (final FileCabinet fileCabinet: fileCabinets) {
	    HTML fileCabinetHtmlSelection = new HTML();
	    String htmlString = "<img src='" + GWT.getHostPageBaseURL() + "images/filecabinet.gif' width='32px' height='32px' align='center'>" + fileCabinet.getCabinetName();
	    fileCabinetHtmlSelection.setHTML(htmlString);
	    
	    fileCabinetHtmlSelection.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
		    History.newItem(fileCabinet.getCabinetName().replaceAll(" ", ""));
		    loadFileCabinetView(fileCabinet);
		}
		
	    });
	    
	    fileCabinetSelectionTable.setWidget(row, 0, fileCabinetHtmlSelection);
	    fileCabinetSelectionTable.getCellFormatter().setStyleName(row, 0, "link-element");
	    ++row;
	}

	mainPanel.add(fileCabinetSelectionTable);

    }
    
    /**
     * PRIVATE CLASS: FILE CABINET SELECTION HANDLER
     * @author mweppler
     *
     */
    private class FileCabinetSelectionHandler implements AsyncCallback<ArrayList<FileCabinet>> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.destroyTimer();
	    idb.messageDialogBox(0, "RPC Failure" , "File Cabinet Selection RPC Failure");
	}

	@Override
	public void onSuccess(ArrayList<FileCabinet> fc) {
	    fileCabinets = fc;
	    showFileCabinets();
	    idb.destroyTimer();
	}

    }

}
