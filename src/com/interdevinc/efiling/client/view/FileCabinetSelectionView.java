package com.interdevinc.efiling.client.view;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
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
    private FileCabinetSelectionHandler fileCabinetHandler;
    private AuthenticatedUser authenticatedUser;
    private InformationDialogBox idb;
    private ArrayList<FileCabinet> fileCabinets;

    public FileCabinetSelectionView(AuthenticatedUser au) {
	authenticatedUser = au;
	idb = new InformationDialogBox();
	idb.loadingDialogBox("File Cabinet Selection.");
	initializeComponents();
	doFileCabinetSelectionRpc();
    }

    public Widget showView() {
	return mainPanel;
    }

    private void doFileCabinetSelectionRpc() {
	//execute authentication procedure
	fileCabinetAsync.retrieveUsableFileCabinets(authenticatedUser, fileCabinetHandler);
    }

    private void initializeComponents() {

	// Define the service to call
	fileCabinetAsync = (FileCabinetServiceAsync) GWT.create(FileCabinetService.class);

	// initialize the file cabinet handler
	fileCabinetHandler = new FileCabinetSelectionHandler();

	mainPanel = new VerticalPanel();

    }

    private void showFileCabinets() {
	FlexTable fileCabinetSelectionTable = new FlexTable();
	
	int row = 0; // First row in table.
	for (FileCabinet fileCabinet: fileCabinets) {
	    Image fileCabinetImage = new Image();
	    fileCabinetImage.setUrl("/images/filecabinet.gif");
	    fileCabinetImage.setSize("32px", "32px");

	    fileCabinetSelectionTable.setWidget(row, 0, fileCabinetImage);
	    fileCabinetSelectionTable.setWidget(row, 1, new Hyperlink(fileCabinet.getCabinetName(), fileCabinet.getCabinetName()));
	    ++row;
	}

	mainPanel.add(fileCabinetSelectionTable);

    }

    private class FileCabinetSelectionHandler implements AsyncCallback<ArrayList<FileCabinet>> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.destroyTimer();
	    idb.errorMessageDialogBox("RPC Failure" , "File Cabinet Selection RPC Failure");
	}

	@Override
	public void onSuccess(ArrayList<FileCabinet> fc) {
	    fileCabinets = fc;
	    showFileCabinets();
	    idb.destroyTimer();
	}

    }

}
