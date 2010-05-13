package com.interdevinc.efiling.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.processor.AuthenticationService;
import com.interdevinc.efiling.client.processor.AuthenticationServiceAsync;
import com.interdevinc.efiling.shared.processor.Hash;

public class LoginManager implements ClickHandler,KeyPressHandler {

    // Login fields
    private FlexTable loginFieldsTable;
    private Image userImg;
    private Image passImg;
    private TextBox nameBox;
    private PasswordTextBox passBox;
    private Button submit;

    // RPC objects
    private AuthenticationServiceAsync authentication;
    private AuthenticationHandler authenticationHandler;

    // Authenticated User
    private AuthenticatedUser authenticatedUser;
    
    /**
     * CONSTRUCTOR: LOGIN MANAGER
     */
    public LoginManager() {
	initializeComponents();
	initializeRemoteProcedureWorkers();
	assembleComponents();
    }

    /**
     * METHOD:	ON CLICK
     * Click events for associating buttons	 */
    public void onClick(ClickEvent ce) {

	if(validFormFields()){

	    //get fields and hash password
	    final String username = nameBox.getText();
	    final String password=Hash.md5(passBox.getText());

	    //execute authentication procedure
	    authentication.authenticateUser(username, password, authenticationHandler);
	}else{
	    //display error
	    Window.alert("Please fill all form fields!");
	}
    }

    /**
     * METHOD:	ON KEY PRESS
     * Keyboard events for associated widgets/components	 */
    public void onKeyPress(KeyPressEvent keyEvent) {
	if(keyEvent.getNativeEvent().getKeyCode()==KeyCodes.KEY_ENTER){
	    submit.click();
	}
    }
    
    /**
     * METHOD: SHOW VIEW
     * @return loginFieldsTable
     */
    public Widget showView() {
	return loginFieldsTable;
    }

    /**
     * METHOD: ASSEMBLE COMPONENTS
     * This method assembles all the components for 
     * the login component
     */
    private void assembleComponents() {

	//set row 0
	loginFieldsTable.setWidget(0, 0, userImg);
	loginFieldsTable.setWidget(0, 1, nameBox);

	//set row 1
	loginFieldsTable.setWidget(1, 0, passImg);
	loginFieldsTable.setWidget(1, 1, passBox);

	//set row 2
	loginFieldsTable.setWidget(2, 0, submit);
	loginFieldsTable.getFlexCellFormatter().setColSpan(2, 0, 2);
	loginFieldsTable.getFlexCellFormatter().setStylePrimaryName(2, 0, "form-button-row");

    }

    /**
     * METHOD: INITIALIZE COMPONENTS
     * This method initializes all components 
     * associated with the login widget: username,
     * password, and submit field
     */
    private void initializeComponents() {

	// Images for the username/password icons.
	userImg = new Image();
	//TODO Change before compiling...
	//userImg.setUrl("/Efiling/images/user.gif");
	userImg.setUrl("/images/user.gif");
	userImg.setSize("32px", "32px");
	passImg = new Image();
	//passImg.setUrl("/Efiling/images/pass.gif");
	passImg.setUrl("/images/pass.gif");
	passImg.setSize("32px", "32px");

	//init loginFieldsTable
	loginFieldsTable = new FlexTable();

	//init data fields
	nameBox = new TextBox();
	nameBox.setWidth("100px");		
	nameBox.addKeyPressHandler(this);
	nameBox.setText("developer");

	passBox = new PasswordTextBox();		
	passBox.setWidth("100px");
	passBox.addKeyPressHandler(this);
	passBox.setText("d3V3l0P3R");

	//init submit button
	submit = new Button("Login");
	submit.addClickHandler(this);

    }

    /**
     * METHOD: INIT REMOTE PROCEDURE WORKERS
     * This method initializes all object associated with
     * creating a valid RPC call.	 */
    private void initializeRemoteProcedureWorkers(){
	//define the service to call  
	authentication = (AuthenticationServiceAsync) GWT.create(AuthenticationService.class);    

	//init RPC handler
	authenticationHandler = new AuthenticationHandler();
    }
    
    /**
     * METHOD: GET AUTHENTICATED USER
     * @return authenticatedUser
     */
    public AuthenticatedUser getAuthenticatedUser() {
	return authenticatedUser;
    }
    
    /**
     * METHOD: SET AUTHENTICATED USER
     * @param user the authenticatedUser to set
     */
    private void setAuthenticatedUser(AuthenticatedUser user) {
	authenticatedUser = user;
    }
    
    /**
     * METHOD:	CHECK VALID FORM FIELDS
     * This method checks wether both username and
     * password fields have been filled properly
     * @return - true/false	 */
    private boolean validFormFields(){
	return (!nameBox.getText().equals("") && !passBox.getText().equals(""));
    }

    /**
     * PRIVATE CLASS: AUTHENTICATION HANDLER
     * @author mweppler
     * This class handles the Remote Procedure Calls associated with
     * AuthenticateUserImp.  Because a successful RPC call may still constitute and
     * NULL user, onSuccess(Auth...) will test for an unfound user as NULL and display
     * the appropriate error message  	 */
    private class AuthenticationHandler implements AsyncCallback<AuthenticatedUser>{

	/**
	 * METHOD:	ON SUCCESS
	 * @param user - valid user or NULL		 */
	public void onSuccess(AuthenticatedUser user){
	    if(user != null){
		setAuthenticatedUser(user);
		History.newItem("UserAuthenticated");
	    }else{
		Window.alert("Username/Password pair not found.");
	    }				
	}

	/**
	 * METHOD:	ON FAILURE
	 * @param ex - exception throw on RPC failure */
	public void onFailure(Throwable ex){
	    Window.alert("An error has occurred. \n Please contact your administrator.");
	}
    }
}
