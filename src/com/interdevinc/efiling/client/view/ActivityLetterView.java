package com.interdevinc.efiling.client.view;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.interdevinc.efiling.client.model.ActivityLetter;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.Client;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.ScannedDocument;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.client.processor.ActivityLetterService;
import com.interdevinc.efiling.client.processor.ActivityLetterServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class ActivityLetterView implements ChangeHandler, ClickHandler {

    // Models
    private AuthenticatedUser authenticatedUser;
    @SuppressWarnings("unused")
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
    private Grid mainDivderGrid;
    private VerticalPanel client12MonthPanel;
    private VerticalPanel client30dayPanel;
    private ListBox clientListbox12Month;
    private Label resultsLabel;
    private FlexTable client30dayResultsTable;
    private Button refresh30DayButton;
    private ThrityDayNotReturnedActivityLetterHandler thrityDayNotReturnedActivityLetterHandler;
    private TwelveMonthActivityLetterHandler twelveMonthActivityLetterHandler;

    // Update Activity Letter Components
    private ArrayList<ActivityLetter> activityLetterClients;
    private ListBox updateActivityLetterListbox;
    private ArrayList<ScannedDocument> scannedActivityLetters;
    private ListBox scannedDocumentListbox;
    private DateBox letterReceivedDatebox;
    private Button submitUpdateActivityLetterButton;
    private ActivityLetterClientsHandler activityLetterClientsHandler;
    private ScannedActivityLettersHandler scannedActivityLettersHandler;
    private UpdateActivityLetterHandler updateActivityLetterHandler;

    /**
     * CONSTRUCTOR: ACTIVITY LETTER VIEW
     * @param au the AuthenticatedUser
     * @param fc the FileCabinet
     * @param mp the VerticalPanel
     * @param sc the SearchComponents
     */
    public ActivityLetterView(AuthenticatedUser au, FileCabinet fc, VerticalPanel mp, SearchComponents sc) {

	authenticatedUser = au;
	fileCabinet = fc;
	mainPanel = mp;
	searchComponents = sc;
	idb = new InformationDialogBox();
	idb.loadingDialogBox("Activity Letter Service.");

	initializeRpcWorkers();
	retrieveActivityLetterClients();

    }

    /**
     * METHOD: ON CHANGE
     * If the updateActivityLetterListbox has changed, call retrieveScannedDocuments()
     */
    public void onChange(ChangeEvent event) {

	// Search Activity Letter Status
	if (event.getSource().equals(clientListbox12Month)) {
	    if (clientListbox12Month.getSelectedIndex() > 0) {
		activityLetterAsync.retrieveTwelveMonthStatus(authenticatedUser, searchComponents.getClientList().get(clientListbox12Month.getSelectedIndex() - 1).getAccountNumber(), twelveMonthActivityLetterHandler);
	    }
	}

	// Update Activity Letter
	if (event.getSource().equals(updateActivityLetterListbox)) {
	    if (updateActivityLetterListbox.getSelectedIndex() > 0) {
		retrieveScannedDocuments();
	    }
	}


    }

    /**
     * METHOD: ON CLICK
     * Submits either a new activity letter(submitNewActivityLetterButton) or updates and existing one(submitUpdateActivityLetterButton).
     */
    public void onClick(ClickEvent event) {

	// New Activity Letter
	if (event.getSource().equals(submitNewActivityLetterButton)) {

	    boolean clientOkToSubmit;
	    boolean dateOkToSubmit;

	    String accountNumber = null;

	    if (clientListbox.getSelectedIndex() > 0) {
		clientOkToSubmit = true;
		accountNumber = searchComponents.getClientList().get(clientListbox.getSelectedIndex() - 1).getAccountNumber();
	    } else {
		clientOkToSubmit = false;
		idb.messageDialogBox(1, "Submit New Activity Letter", "Please select a Client.");
	    }

	    if (letterSentDatebox.getValue() != null && !letterSentDatebox.getValue().equals("")) {
		dateOkToSubmit = true;
	    } else {
		dateOkToSubmit = false;
		idb.messageDialogBox(1, "Submit New Activity Letter", "Please choose a date.");
	    }

	    if (clientOkToSubmit && dateOkToSubmit) {
		activityLetterAsync.addNewActivityLetter(authenticatedUser, accountNumber, databaseTimeFormat.format(letterSentDatebox.getValue()), newActivityLetterHandler);
		idb.messageDialogBox(1, "Submit New Activity Letter", "Account#: " + accountNumber + " | Date Sent: " + databaseTimeFormat.format(letterSentDatebox.getValue()));
	    }

	}

	// Search Activity Letter Status
	if (event.getSource().equals(refresh30DayButton)) {
	    activityLetterAsync.retrieveThrityDayNotReceivedStatus(authenticatedUser, thrityDayNotReturnedActivityLetterHandler);
	}

	// Update Activity Letter
	if (event.getSource().equals(submitUpdateActivityLetterButton)) {

	    boolean clientOkToSubmit;
	    boolean dateOkToSubmit;
	    boolean documentOkToSubmit;
	    ActivityLetter activityLetter;

	    if (updateActivityLetterListbox.getSelectedIndex() > 0) {
		clientOkToSubmit = true;
	    } else {
		clientOkToSubmit = false;
		idb.messageDialogBox(1, "Update Activity Letter", "Please select a Client.");
	    }

	    if (letterReceivedDatebox.getValue() != null && !letterReceivedDatebox.getValue().equals("")) {
		dateOkToSubmit = true;
	    } else {
		dateOkToSubmit = false;
		idb.messageDialogBox(1, "Update Activity Letter", "Please choose a date.");
	    }

	    if (scannedDocumentListbox.getSelectedIndex() > 0) {
		documentOkToSubmit = true;
	    } else {
		documentOkToSubmit = false;
		idb.messageDialogBox(1, "Update Activity Letter", "Please select a scanned activity letter.");
	    }

	    if (clientOkToSubmit && dateOkToSubmit && documentOkToSubmit) {
		Date tempDate = databaseTimeFormat.parse(databaseTimeFormat.format(letterReceivedDatebox.getValue()));
		//Date tempDate = databaseTimeFormat.format(letterReceivedDatebox.getValue());

		activityLetter = new ActivityLetter(
			activityLetterClients.get(updateActivityLetterListbox.getSelectedIndex() - 1).getLetterID(), 
			activityLetterClients.get(updateActivityLetterListbox.getSelectedIndex() - 1).getAccountNumber(),
			activityLetterClients.get(updateActivityLetterListbox.getSelectedIndex() - 1).getDateSent(),
			tempDate,
			scannedActivityLetters.get(scannedDocumentListbox.getSelectedIndex() - 1).getUploadID(),
		"");
		// Comment me out...
		idb.messageDialogBox(1, "Update Activity Letter", 
			"LetterID: " + activityLetter.getLetterID() + "<br />" +
			"Account#: " + activityLetter.getAccountNumber() + "<br />" +
			"Date Sent: " + activityLetter.getDateSentFormattedString() + "<br />" +
			"Date Received: " + activityLetter.getDateReceivedFormattedString() + "<br />" +
			"Scanned Document: " + activityLetter.getScannedDocument());
		activityLetterAsync.updateActivityLetter(authenticatedUser, activityLetter, updateActivityLetterHandler);
	    }
	}

    }

    /**
     * METHOD: ASSEMBLE INITIAL COMPONENTS
     * Assmebles the pages initial components.
     */
    private void assembleInitialComponents() {

	mainPanel.add(mainTabPanel);

	// New Activity Letter
	mainTabPanel.add(newActivityLetterPanel, "Add New Activity Letter");
	clientListbox.addItem("Select a Client");
	for (Client clientInfo : searchComponents.getClientList()) {
	    clientListbox.addItem(clientInfo.getClientFullInfo());
	}
	letterSentDatebox.setFormat(new DateBox.DefaultFormat(dateFieldFormat));
	submitNewActivityLetterButton.addClickHandler(this);
	newActivityLetterPanel.add(clientListbox);
	newActivityLetterPanel.add(new Label("Date Sent:"));
	newActivityLetterPanel.add(letterSentDatebox);
	newActivityLetterPanel.add(submitNewActivityLetterButton);

	// Update Activity Letter
	updateActivityLetterListbox.clear();

	mainTabPanel.add(updateActivityLetterPanel, "Update Activity Letter");
	if (activityLetterClients!=null) {
	    updateActivityLetterListbox.addItem("Select a Client");
	    for (ActivityLetter alc : activityLetterClients) {
		updateActivityLetterListbox.addItem(alc.getAccountNumber() + " | " + alc.getDateSentFormattedString());
	    }
	} else {
	    updateActivityLetterListbox.addItem("No open Activity Letters.");
	}
	updateActivityLetterListbox.addChangeHandler(this);
	letterReceivedDatebox.setFormat(new DateBox.DefaultFormat(dateFieldFormat));
	scannedDocumentListbox.addItem("Select a Client to load data...");
	submitUpdateActivityLetterButton.addClickHandler(this);
	updateActivityLetterPanel.add(new Label("*Please upload the clients scanned Activity Letter before updating."));
	updateActivityLetterPanel.add(updateActivityLetterListbox);
	updateActivityLetterPanel.add(new Label("Date Received:"));
	updateActivityLetterPanel.add(letterReceivedDatebox);
	updateActivityLetterPanel.add(scannedDocumentListbox);
	updateActivityLetterPanel.add(submitUpdateActivityLetterButton);

	// Search Activity Letter
	mainTabPanel.add(searchActivityLetterPanel, "Search Activity Letter");
	clientListbox12Month.addItem("Select a Client");
	for (Client clientInfo : searchComponents.getClientList()) {
	    clientListbox12Month.addItem(clientInfo.getClientFullInfo());
	}
	clientListbox12Month.addChangeHandler(this);
	refresh30DayButton.addClickHandler(this);
	client12MonthPanel.add(clientListbox12Month);
	client12MonthPanel.add(resultsLabel);
	client30dayPanel.add(refresh30DayButton);
	client30dayPanel.add(client30dayResultsTable);
	mainDivderGrid.setWidget(0, 0, client12MonthPanel);
	mainDivderGrid.setWidget(0, 1, client30dayPanel);
	searchActivityLetterPanel.add(mainDivderGrid);

    }

    /**
     * METHOD: INITIALIZE INITIAL COMPONENTS
     */
    private void initializeInitialComponents() {

	mainTabPanel = new DecoratedTabPanel();

	// New Activity Letter
	newActivityLetterPanel = new VerticalPanel();
	clientListbox = new ListBox();
	letterSentDatebox = new DateBox();
	submitNewActivityLetterButton = new Button("Submit");

	// Update Activity Letter
	updateActivityLetterPanel = new VerticalPanel();
	updateActivityLetterListbox = new ListBox();
	scannedDocumentListbox = new ListBox();
	letterReceivedDatebox = new DateBox();
	submitUpdateActivityLetterButton = new Button("Submit");

	// Search Activity Letter
	searchActivityLetterPanel = new VerticalPanel();
	mainDivderGrid = new Grid(1, 2);
	client12MonthPanel = new VerticalPanel();
	client30dayPanel = new VerticalPanel();
	clientListbox12Month = new ListBox();
	resultsLabel = new Label();
	refresh30DayButton = new Button("Refresh");
	client30dayResultsTable = new FlexTable();

    }

    /**
     * METHOD: INITIALIZE RPC WORKERS
     */
    private void initializeRpcWorkers() {

	// Define the service to call  
	activityLetterAsync = (ActivityLetterServiceAsync) GWT.create(ActivityLetterService.class); 

	// Initialize the RPC Classes.
	activityLetterClientsHandler = new ActivityLetterClientsHandler();
	scannedActivityLettersHandler = new ScannedActivityLettersHandler();
	newActivityLetterHandler = new NewActivityLetterHandler();
	thrityDayNotReturnedActivityLetterHandler = new ThrityDayNotReturnedActivityLetterHandler();
	twelveMonthActivityLetterHandler = new TwelveMonthActivityLetterHandler();
	updateActivityLetterHandler = new UpdateActivityLetterHandler();

    }

    /**
     * METHOD: RETRIEVE ACTIVITY LETTER CLIENTS
     * Gets a list of clients with existing activity letters that need updating
     */
    private void retrieveActivityLetterClients() {
	activityLetterAsync.retrieveClientsWithNullUpdates(authenticatedUser, activityLetterClientsHandler);
    }

    /**
     * LOAD INITIAL COMPONENTS
     */
    private void loadInitialComponents() {

	mainPanel.clear();

	initializeInitialComponents();
	assembleInitialComponents();

    }

    /**
     * METHOD: LOAD SCANNED DOCUMENTS
     * Loads the returned list of scanned activity letters that need to be associated with a given client.
     */
    private void loadScannedDocuments() {
	scannedDocumentListbox.clear();
	if (scannedActivityLetters != null) {
	    scannedDocumentListbox.addItem("Please choose a Scanned Document.");
	    for (ScannedDocument sal : scannedActivityLetters) {
		scannedDocumentListbox.addItem(sal.getFileName() + " | " + sal.getDocumentTypeAbbr() + " | " + sal.getUploadDate());
	    }
	} else {
	    scannedDocumentListbox.addItem("No Activity Letter uploaded for selected client");
	}
    }

    /**
     * METHOD: LOAD THRITY DAY NON RETURNED LIST
     * @param c30 arraylist of activityLetters
     * Prints a table of Activity Letter data.
     */
    private void loadThrityDayNonReturnedList(ArrayList<ActivityLetter> c30) {

	if (c30 != null) {
	    int row = 0;

	    client30dayResultsTable.setText(row, 0, "Account#");
	    client30dayResultsTable.setText(row, 1, "Date Sent");
	    client30dayResultsTable.setText(row, 2, "Status");
	    ++row;

	    for (ActivityLetter activityLetter : c30) {
		client30dayResultsTable.setText(row, 0, activityLetter.getAccountNumber());
		client30dayResultsTable.setText(row, 1, activityLetter.getDateSentFormattedString());
		client30dayResultsTable.setText(row, 2, activityLetter.getMisc());
		++row;
	    }
	} else {
	    client30dayResultsTable.setText(0, 0, "No Results Returned.");
	}

    }

    /**
     * METHOD: LOAD TWELVE MONTH ACTIVITY LETTER STATUS
     * @param c12 activityLetter
     */
    private void loadTwelveMonthActivityLetterStatus(ActivityLetter c12) {

	String status = c12.getAccountNumber() + " - " + c12.getMisc(); 
	resultsLabel.setText(status);

    }

    /**
     * METHOD: RETRIEVE SCANNED DOCUMENTS
     * Gets a list of scanned activity letters that need to be associated with a given client.
     */
    private void retrieveScannedDocuments() {
	activityLetterAsync.retrieveScannedActivityLetterForClient(authenticatedUser, activityLetterClients.get(updateActivityLetterListbox.getSelectedIndex() - 1).getAccountNumber(), scannedActivityLettersHandler);
    }

    /**
     * PRIVATE CLASS: ACTIVITY LETTER CLIENTS HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class ActivityLetterClientsHandler implements AsyncCallback<ArrayList<ActivityLetter>> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.destroyTimer();
	    idb.messageDialogBox(0, "Retrieve Client List", "RPC Failure.");
	}

	@Override
	public void onSuccess(ArrayList<ActivityLetter> alc) {
	    activityLetterClients = alc;
	    loadInitialComponents();
	    idb.destroyTimer();
	}

    }

    /**
     * PRIVATE CLASS: NEW ACTIVITY LETTER HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class NewActivityLetterHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.messageDialogBox(0, "Submit New Activity Letter", "RPC Failure.");
	}

	@Override
	public void onSuccess(String resultMessage) {
	    idb.messageDialogBox(1, "Submit New Activity Letter", resultMessage);
	    retrieveActivityLetterClients();
	}

    }

    /**
     * PRIVATE CLASS: SCANNED ACTIVITY LETTERS HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class ScannedActivityLettersHandler implements AsyncCallback<ArrayList<ScannedDocument>> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.destroyTimer();
	    idb.messageDialogBox(0, "Retrieve Scanned Activity Letters", "RPC Failure.");
	}

	@Override
	public void onSuccess(ArrayList<ScannedDocument> sal) {
	    scannedActivityLetters = sal;
	    loadScannedDocuments();
	    idb.destroyTimer();
	}

    }

    /**
     * PRIVATE CLASS: UPDATE ACTIVITY LETTER HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class ThrityDayNotReturnedActivityLetterHandler implements AsyncCallback<ArrayList<ActivityLetter>> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.messageDialogBox(0, "30 Day non-returned Activity Letters", "RPC Failure.");
	}

	@Override
	public void onSuccess(ArrayList<ActivityLetter> client30DayNonReturned) {
	    loadThrityDayNonReturnedList(client30DayNonReturned);
	}

    }

    /**
     * PRIVATE CLASS: TWELVE MONTH ACTIVITY LETTER HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class TwelveMonthActivityLetterHandler implements AsyncCallback<ActivityLetter> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.messageDialogBox(0, "Client 12 Month Activity Letter Status", "RPC Failure.");
	}

	@Override
	public void onSuccess(ActivityLetter client12MonthActivityLetterStatus) {
	    loadTwelveMonthActivityLetterStatus(client12MonthActivityLetterStatus);
	}

    }

    /**
     * PRIVATE CLASS: UPDATE ACTIVITY LETTER HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class UpdateActivityLetterHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.messageDialogBox(0, "Update Activity Letter", "RPC Failure.");
	}

	@Override
	public void onSuccess(String resultMessage) {
	    idb.messageDialogBox(1, "Update Activity Letter", resultMessage);
	    retrieveActivityLetterClients();
	}

    }

}
