package com.interdevinc.efiling.client.view;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.Broker;
import com.interdevinc.efiling.client.model.Client;
import com.interdevinc.efiling.client.model.DocumentType;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.ScannedDocument;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.client.processor.FileCabinetService;
import com.interdevinc.efiling.client.processor.FileCabinetServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

@SuppressWarnings("unused")
public class SearchDocuments implements ChangeHandler {

	// Panels
	private VerticalPanel searchDocumentsPanel;
	private FlexTable searchComponentsTable;
	private FlexTable searchResultsTable;
	private InformationDialogBox idb;

	private ListBox cabinetTypeInfoList;
	private ListBox documentTypeList;
	private MultiWordSuggestOracle searchOracle;
	private SuggestBox quickSearchSuggestbox;
	private TextBox quickSearchTextbox;

	private AuthenticatedUser authenticatedUser;
	private FileCabinet fileCabinet;
	private SearchComponents searchComponents;
	private ArrayList<ScannedDocument> scannedDocuments;

	private FileCabinetServiceAsync fileCabinetAsync; 
	private DisassociateDocumentHandler disassociateDocumentHandler;
	private SearchResultsHandler searchResultsHandler;

	/**
	 * CONSTRUCTOR: SEARCH DOCUMENTS
	 * @param au the authenticatedUser to set
	 * @param fc the FileCabinet to set
	 * @param sdp the VerticalPanel to set
	 * @param sc the SearchComponents to set
	 */
	public SearchDocuments(AuthenticatedUser au, FileCabinet fc, VerticalPanel sdp, SearchComponents sc) {
		authenticatedUser = au;
		fileCabinet = fc;
		searchDocumentsPanel = sdp;
		searchComponents = sc;

		initializeComponents();
		assembleComponents();

	}

	/**
	 * METHOD: ON CHANGE
	 * Handles any changes.
	 */
	@Override
	public void onChange(ChangeEvent event) {

		if (event.getSource().equals(cabinetTypeInfoList) && cabinetTypeInfoList.getSelectedIndex() > 0) {
			String number = null;
			String documentType = null;
			if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
				number = searchComponents.getBrokerList().get(cabinetTypeInfoList.getSelectedIndex() - 1).getRepNumber();
			} else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
				number = searchComponents.getClientList().get(cabinetTypeInfoList.getSelectedIndex() - 1).getAccountNumber();
			}

			if (documentTypeList.getSelectedIndex() > 0) {
				documentType = searchComponents.getDocumentTypeList().get(documentTypeList.getSelectedIndex() - 1).getDocumentTypeAbbr();
			}

			submitSearchQuery(number, documentType);

		}

		if (event.getSource().equals(documentTypeList) && documentTypeList.getSelectedIndex() > 0) {
			String number = null;
			String documentType = null;

			documentType = searchComponents.getDocumentTypeList().get(documentTypeList.getSelectedIndex() - 1).getDocumentTypeAbbr();

			if (cabinetTypeInfoList.getSelectedIndex() > 0) {
				if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
					number = searchComponents.getBrokerList().get(cabinetTypeInfoList.getSelectedIndex() - 1).getRepNumber();
				} else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
					number = searchComponents.getClientList().get(cabinetTypeInfoList.getSelectedIndex() - 1).getAccountNumber();
				}
			}

			submitSearchQuery(number, documentType);
		}

	}

	/**
	 * METHOD: ASSEMBLE COMPONENTS
	 * Assemble initial view components
	 */
	private void assembleComponents() {

		int row = 0;

		//TODO Livesearch
		//	searchOracle.add("Cat");
		//	searchOracle.add("Dog");
		//	searchOracle.add("Horse");
		//	searchOracle.add("Canary");
		//	searchComponentsTable.setWidget(row, 0, new Label("Quick Search:"));
		//	searchComponentsTable.setWidget(row, 1, quickSearchSuggestbox);
		//	++row;

		searchComponentsTable.setWidget(row, 0, cabinetTypeInfoList);
		//searchComponentsTable.setWidget(row, 1, documentTypeList);
		searchComponentsTable.setWidget(row, 1, documentTypeList);

		// Test cabinet type.
		if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
			// Build Listboxes.
			cabinetTypeInfoList.addItem("Search by Broker Info");
			for (Broker brokerInfo : searchComponents.getBrokerList()) {
				cabinetTypeInfoList.addItem(brokerInfo.getBrokerFullInfo());
			}
		} else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
			// Build Listboxes.
			cabinetTypeInfoList.addItem("Search by Client Info");
			for (Client clientInfo : searchComponents.getClientList()) {
				cabinetTypeInfoList.addItem(clientInfo.getClientFullInfo());
			}
		} else {
			cabinetTypeInfoList.addItem("Cabinet not loaded!");
		}
		cabinetTypeInfoList.addChangeHandler(this);

		documentTypeList.addItem("Search by Document Type");
		for (DocumentType documentTypeInfo : searchComponents.getDocumentTypeList()) {
			documentTypeList.addItem(documentTypeInfo.getDocumentTypeFullInfo());
		}
		documentTypeList.addChangeHandler(this);
		++row;

		searchDocumentsPanel.add(searchComponentsTable);

	}

	/**
	 * METHOD: DISPLAY SEARCH RESULTS
	 * Clear table from previous search, display new results.
	 */
	private void displaySearchResults() {

		searchResultsTable.removeAllRows();

		if (scannedDocuments.size() > 0) {
			String numberTypeText = new String();
			String cabinetType = new String();
			if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
				numberTypeText = "Broker";
				cabinetType = "broker";
			} else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
				numberTypeText = "Client";
				cabinetType = "client";
			}

			int row = 0;
			searchResultsTable.setText(row, 0, numberTypeText + " Info");
			searchResultsTable.setText(row, 1, "Document Type");
			searchResultsTable.setText(row, 2, "File Name");
			searchResultsTable.setText(row, 3, "Upload Date");
			searchResultsTable.getCellFormatter().setStyleName(row, 0, "document-search-results-table");
			searchResultsTable.getCellFormatter().setStyleName(row, 1, "document-search-results-table");
			searchResultsTable.getCellFormatter().setStyleName(row, 2, "document-search-results-table");
			searchResultsTable.getCellFormatter().setStyleName(row, 3, "document-search-results-table");
			++row;

			for (final ScannedDocument scannedDocument : scannedDocuments) {
				String link = GWT.getModuleBaseURL() + "filedownload?user="+authenticatedUser.getUsername()+"&cabinet=" + cabinetType + "&uploadID=" + scannedDocument.getUploadID(); 
				HTML pdfFileName = new HTML();
				String htmlString = "<a href='" + link + "'><img src='" + GWT.getHostPageBaseURL() + "images/pdfImage.gif' width='20px' height='20px' align='center' border='0'>" + scannedDocument.getFileName() + "</a>";
				pdfFileName.setHTML(htmlString);
				Button disButton = new Button("Disassociate");
				if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
					searchResultsTable.setText(row, 0, searchComponents.returnBrokerByRepNumber(scannedDocument.getGroupedBy()).getBrokerFullInfo());
				} else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
					searchResultsTable.setText(row, 0, searchComponents.returnClientByAccountNumber(scannedDocument.getGroupedBy()).getClientFullInfo());
				}
				searchResultsTable.setText(row, 1, scannedDocument.getDocumentTypeAbbr());
				searchResultsTable.setWidget(row, 2, pdfFileName);
				searchResultsTable.setText(row, 3, scannedDocument.getUploadDate());
				searchResultsTable.setWidget(row, 4, disButton);
				disButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (Window.confirm(scannedDocument.getFileName() + " - Are you sure you want to Disassociate this Document? This cannot be undone!")) {
							idb = new InformationDialogBox();
							idb.loadingDialogBox("Disassociating Document");
							// Execute procedure
							fileCabinetAsync.disassociateDocument(authenticatedUser, fileCabinet, scannedDocument, disassociateDocumentHandler);
						} else {
							Window.alert("phew... That was close...");
						}
					}
				});
				
				searchResultsTable.getCellFormatter().setStyleName(row, 0, "document-search-results-table");
				searchResultsTable.getCellFormatter().setStyleName(row, 1, "document-search-results-table");
				searchResultsTable.getCellFormatter().setStyleName(row, 2, "document-search-results-table");
				searchResultsTable.getCellFormatter().addStyleName(row, 2, "link-element");
				searchResultsTable.getCellFormatter().setStyleName(row, 3, "document-search-results-table");
				searchResultsTable.getCellFormatter().setStyleName(row, 4, "document-search-results-table");
				++row;
			}
			searchResultsTable.setStyleName("document-search-results-table");
			searchDocumentsPanel.add(searchResultsTable);
		} else {
			Window.alert("No Associated Documents.");
		}
	}

	/**
	 * METHOD: INITIALIZE COMPONENTS
	 * Initialize initial components
	 */
	private void initializeComponents() {

		// Define the service to call  
		fileCabinetAsync = (FileCabinetServiceAsync) GWT.create(FileCabinetService.class);    

		// Initialize RPC handler
		disassociateDocumentHandler = new DisassociateDocumentHandler();
		searchResultsHandler = new SearchResultsHandler();

		searchComponentsTable = new FlexTable();
		searchOracle = new MultiWordSuggestOracle();
		quickSearchSuggestbox = new SuggestBox(searchOracle);
		quickSearchTextbox = new TextBox();
		cabinetTypeInfoList = new ListBox();
		documentTypeList = new ListBox();
		searchResultsTable = new FlexTable();

	}

	/**
	 * METHOD: SUBMIT SEARCH QUERY
	 * @param number
	 * @param documentType
	 * Display Loading Dialog, clear ScannedDocuments from previous search, call retrieveSearchResults().
	 */
	private void submitSearchQuery(String number, String documentType) {
		idb = new InformationDialogBox();
		idb.loadingDialogBox("Loading Search Results");

		// Empty last searchs results.
		if (scannedDocuments != null) {
			scannedDocuments.clear();
		}

		// Execute procedure
		fileCabinetAsync.retrieveSearchResults(fileCabinet, number, documentType, searchResultsHandler);
	}

	/**
	 * PRIVATE CLASS: DISASSOCIATE DOCUMENT HANDLER
	 * @author mweppler
	 * GWT AsyncCallback
	 */
	private class DisassociateDocumentHandler implements AsyncCallback<String> {

		@Override
		public void onFailure(Throwable caught) {
			idb.destroyTimer();
			idb.messageDialogBox(0, "RPC Failure" , "Disassociate Document RPC Failure");
		}

		@Override
		public void onSuccess(String resultMessage) {
			searchResultsTable.removeAllRows();
			cabinetTypeInfoList.setSelectedIndex(0);
			documentTypeList.setSelectedIndex(0);
			idb.destroyTimer();
			Window.alert(resultMessage);
		}

	}

	/**
	 * PRIVATE CLASS: SEARCH RESULTS HANDLER
	 * @author mweppler
	 * GWT AsyncCallback
	 */
	private class SearchResultsHandler implements AsyncCallback<ArrayList<ScannedDocument>> {

		@Override
		public void onFailure(Throwable caught) {
			idb.destroyTimer();
			idb.messageDialogBox(0, "RPC Failure" , "Load Search Results RPC Failure");
		}

		@Override
		public void onSuccess(ArrayList<ScannedDocument> sd) {
			scannedDocuments = sd;
			displaySearchResults();
			idb.destroyTimer();
		}

	}

}
